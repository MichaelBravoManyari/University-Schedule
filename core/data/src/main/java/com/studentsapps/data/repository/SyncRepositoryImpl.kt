package com.studentsapps.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.studentsapps.database.model.CourseEntity
import com.studentsapps.database.model.ScheduleEntity
import com.studentsapps.domain.sync.model.NetworkConnectivityChecker
import com.studentsapps.domain.sync.repository.SyncRepository
import com.studentsapps.network.datasources.CourseNetworkDataSource
import com.studentsapps.network.datasources.ScheduleNetworkDataSource
import com.studentsapps.network.model.NetworkCourse
import com.studentsapps.network.model.NetworkSchedule
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Data-layer implementation of [com.studentsapps.domain.sync.repository.SyncRepository].
 */
class SyncRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val pendingOperations: PendingOperationRepository,
    private val courseNetworkDataSource: CourseNetworkDataSource,
    private val scheduleNetworkDataSource: ScheduleNetworkDataSource,
    private val courseRepository: CourseRepository,
    private val scheduleRepository: ScheduleRepository,
    private val connectivityChecker: NetworkConnectivityChecker,
) : SyncRepository {

    /**
     * Starts a full sync only when:
     * 1. A Firebase user is authenticated.
     * 2. The device has a validated internet connection.
     * 3. There are no local write operations still awaiting upload.
     *
     * Returns immediately (without syncing) if any condition is not met.
     */
    override suspend fun startSyncIfNeeded() {
        val userId = auth.currentUser?.uid ?: return
        if (!connectivityChecker.isConnected()) return

        // Wait until there are no pending write operations before pulling
        // remote data, so we don't overwrite locally-modified records that
        // haven't been uploaded yet.
        pendingOperations
            .getPendingOperations(status = "PENDING", userId = userId)
            .map { pendingOps -> pendingOps.isEmpty() && connectivityChecker.isConnected() }
            .filter { canSync -> canSync }
            .first()

        startSynchronization(userId)
    }

    /**
     * Runs the full remote→local sync for [userId]:
     *
     * 1. Fetches all courses and schedules from Firestore.
     * 2. For each remote record, compares lastModified with the local copy:
     *    - If the remote record is newer → update local.
     *    - If the local record is missing → insert it.
     * 3. Deletes local records that no longer exist in the remote source.
     * 4. Re-schedules all device alarms to reflect the updated data.
     */
    override suspend fun startSynchronization(userId: String) {
        val remoteCourses = courseNetworkDataSource.getAllCourses(userId)
        val remoteSchedules = scheduleNetworkDataSource.getAllSchedules(userId)
        val localCourses = courseRepository.getAllCourseEntity(userId).first()
        val localSchedules = scheduleRepository.getAllScheduleEntity(userId).first()

        syncCourses(remoteCourses, localCourses)
        syncSchedules(remoteSchedules, localSchedules)
        deleteRemovedCourses(remoteCourses, localCourses)
        deleteRemovedSchedules(remoteSchedules, localSchedules)

        scheduleRepository.scheduleAllUserAlarms(userId)
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private suspend fun syncCourses(
        remoteCourses: List<NetworkCourse>,
        localCourses: List<CourseEntity>,
    ) {
        remoteCourses.forEach { remote ->
            val local = localCourses.find { it.id == remote.id }
            when {
                local == null -> courseRepository.registerCourseEntity(remote.toCourseEntity())
                remote.lastModified.isAfter(local.lastModified) ->
                    courseRepository.updateCourseEntity(remote.toCourseEntity())
            }
        }
    }

    private suspend fun syncSchedules(
        remoteSchedules: List<NetworkSchedule>,
        localSchedules: List<ScheduleEntity>,
    ) {
        remoteSchedules.forEach { remote ->
            val local = localSchedules.find { it.id == remote.id }
            when {
                local == null -> scheduleRepository.registerScheduleEntity(remote.toScheduleEntity())
                remote.lastModified.isAfter(local.lastModified) ->
                    scheduleRepository.updateScheduleEntity(remote.toScheduleEntity())
            }
        }
    }

    private suspend fun deleteRemovedCourses(
        remoteCourses: List<NetworkCourse>,
        localCourses: List<CourseEntity>,
    ) {
        val remoteIds = remoteCourses.map { it.id }.toSet()
        localCourses
            .filterNot { it.id in remoteIds }
            .forEach { courseRepository.deleteCourseEntity(it.id) }
    }

    private suspend fun deleteRemovedSchedules(
        remoteSchedules: List<NetworkSchedule>,
        localSchedules: List<ScheduleEntity>,
    ) {
        val remoteIds = remoteSchedules.map { it.id }.toSet()
        localSchedules
            .filterNot { it.id in remoteIds }
            .forEach { scheduleRepository.deleteScheduleEntity(it.id, it.userId) }
    }

    // ── Mapping extensions ────────────────────────────────────────────────────

    private fun NetworkCourse.toCourseEntity() =
        CourseEntity(id, name, nameProfessor, color, lastModified, userId)

    private fun NetworkSchedule.toScheduleEntity() =
        ScheduleEntity(
            id,
            startTime,
            endTime,
            classPlace,
            dayOfWeek,
            specificDate,
            lastModified,
            userId,
            courseId,
        )
}