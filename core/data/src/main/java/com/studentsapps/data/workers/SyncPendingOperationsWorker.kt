package com.studentsapps.data.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.studentsapps.common.serialization.JsonConfig.appJson
import com.studentsapps.data.repository.PendingOperationRepository
import com.studentsapps.database.datasources.CourseLocalDataSource
import com.studentsapps.database.datasources.ScheduleLocalDataSource
import com.studentsapps.database.model.CourseEntity
import com.studentsapps.database.model.ScheduleEntity
import com.studentsapps.model.PendingOperation
import com.studentsapps.network.datasources.CourseNetworkDataSource
import com.studentsapps.network.datasources.ScheduleNetworkDataSource
import com.studentsapps.network.model.NetworkCourse
import com.studentsapps.network.model.NetworkSchedule
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDateTime

@HiltWorker
class SyncPendingOperationsWorker
    @AssistedInject
    constructor(
        private val pendingOperationRepository: PendingOperationRepository,
        private val courseNetworkDataSource: CourseNetworkDataSource,
        private val scheduleNetworkDataSource: ScheduleNetworkDataSource,
        private val auth: FirebaseAuth,
        private val courseLocalDataSource: CourseLocalDataSource,
        private val scheduleLocalDataSource: ScheduleLocalDataSource,
        @Assisted private val context: Context,
        @Assisted private val params: WorkerParameters,
    ) : CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            val userId =
                auth.currentUser?.uid
                    ?: return Result.retry()

            val operation =
                pendingOperationRepository.getFirstPendingOperation("PENDING", userId).firstOrNull()
                    ?: return Result.success()

            return try {
                processOperation(userId, operation)
                Result.success()
            } catch (e: Exception) {
                Log.e("SyncWorker", "Error processing operation", e)
                Result.retry()
            }
        }

        private suspend fun processOperation(
            userId: String,
            operation: PendingOperation,
        ) {
            when (operation.operationType) {
                "REGISTER", "UPDATE" -> syncEntity(userId, operation, isDelete = false)
                "DELETE" -> syncEntity(userId, operation, isDelete = true)
                "READ" -> syncSingleEntity(userId, operation)
                "READ_LIST" -> syncEntityList(userId, operation)
            }
        }

        private suspend fun syncEntity(
            userId: String,
            operation: PendingOperation,
            isDelete: Boolean,
        ) {
            val isCourse = operation.entityType == "COURSE"
            if (isCourse) {
                val course = deserializeCourse(operation.payload, operation.timestamp, userId)
                if (isDelete) {
                    courseNetworkDataSource.deleteCourse(userId, course.id)
                } else {
                    courseNetworkDataSource.updateOrCreateCourse(userId, course)
                }
            } else {
                val schedule = deserializeSchedule(operation.payload, operation.timestamp, userId)
                if (isDelete) {
                    scheduleNetworkDataSource.deleteSchedule(userId, schedule.id)
                } else {
                    scheduleNetworkDataSource.updateOrCreateSchedule(userId, schedule)
                }
            }
            pendingOperationRepository.updateStatus(operation.id, "SYNCED")
        }

        private suspend fun syncSingleEntity(
            userId: String,
            operation: PendingOperation,
        ) {
            val isCourse = operation.entityType == "COURSE"
            if (isCourse) {
                val courseId = deserializeCourse(operation.payload, operation.timestamp, userId).id
                val remoteCourse = courseNetworkDataSource.getCourseById(userId, courseId)

                if (remoteCourse != null) {
                    val localCourse = courseLocalDataSource.getCourse(remoteCourse.id).first()
                    if (remoteCourse.lastModified.isAfter(localCourse.lastModified)) {
                        courseLocalDataSource.updateCourse(remoteCourse.toCourseEntity())
                    }
                }
            } else {
                val scheduleId = deserializeSchedule(operation.payload, operation.timestamp, userId).id
                val remoteSchedule =
                    scheduleNetworkDataSource.getScheduleById(userId, scheduleId)

                if (remoteSchedule != null) {
                    val localSchedule =
                        scheduleLocalDataSource.getScheduleById(remoteSchedule.id).first()
                    if (remoteSchedule.lastModified.isAfter(localSchedule.lastModified)) {
                        scheduleLocalDataSource.updateSchedule(remoteSchedule.toScheduleEntity())
                    }
                }
            }

            markOperationsAsSynced(operation, userId)
        }

        private suspend fun syncEntityList(
            userId: String,
            operation: PendingOperation,
        ) {
            val isCourse = operation.entityType == "COURSE"
            if (isCourse) {
                val ids =
                    deserializeCourses(operation.payload, operation.timestamp, userId).map { it.id }
                val remoteCourses = courseNetworkDataSource.getCoursesByIds(userId, ids)
                val localCourses = courseLocalDataSource.getCoursesByIds(ids).first()

                remoteCourses.forEach { remoteCourse ->
                    val localCourse = localCourses.find { it.id == remoteCourse.id }
                    if (localCourse == null || remoteCourse.lastModified.isAfter(localCourse.lastModified)) {
                        courseLocalDataSource.updateCourse(remoteCourse.toCourseEntity())
                    }
                }
            } else {
                val ids =
                    deserializeSchedules(operation.payload, operation.timestamp, userId).map { it.id }
                val remoteSchedules = scheduleNetworkDataSource.getSchedulesByIds(userId, ids)
                val localSchedules = scheduleLocalDataSource.getSchedulesByIds(ids).first()

                remoteSchedules.forEach { remoteSchedule ->
                    val localSchedule = localSchedules.find { it.id == remoteSchedule.id }
                    if (localSchedule == null || remoteSchedule.lastModified.isAfter(localSchedule.lastModified)) {
                        scheduleLocalDataSource.updateSchedule(remoteSchedule.toScheduleEntity())
                    }
                }
            }

            markOperationsAsSynced(operation, userId)
        }

        private suspend fun markOperationsAsSynced(
            operation: PendingOperation,
            userId: String,
        ) {
            val pendingOperations =
                pendingOperationRepository
                    .getPendingReadOperations(
                        operation.operationType,
                        operation.entityType,
                        userId,
                    ).first()

            when (operation.operationType) {
                "READ" -> {
                    if (operation.entityType == "COURSE") {
                        val targetCourse =
                            deserializeCourse(operation.payload, operation.timestamp, userId)

                        pendingOperations
                            .filter { pendingOp ->
                                try {
                                    val entity =
                                        deserializeCourse(pendingOp.payload, pendingOp.timestamp, userId)
                                    entity.id == targetCourse.id
                                } catch (e: Exception) {
                                    false
                                }
                            }.forEach { pendingOp ->
                                pendingOperationRepository.updateStatus(pendingOp.id, "SYNCED")
                            }
                    } else { // Para "SCHEDULE"
                        val targetSchedule =
                            deserializeSchedule(operation.payload, operation.timestamp, userId)

                        pendingOperations
                            .filter { pendingOp ->
                                try {
                                    val entity =
                                        deserializeSchedule(pendingOp.payload, pendingOp.timestamp, userId)
                                    entity.id == targetSchedule.id
                                } catch (e: Exception) {
                                    false
                                }
                            }.forEach { pendingOp ->
                                pendingOperationRepository.updateStatus(pendingOp.id, "SYNCED")
                            }
                    }
                }

                "READ_LIST" -> {
                    if (operation.entityType == "COURSE") {
                        val targetCourseIds =
                            deserializeCourses(operation.payload, operation.timestamp, userId)
                                .map { it.id }
                                .sorted()

                        pendingOperations
                            .filter { pendingOp ->
                                try {
                                    val storedCourses =
                                        deserializeCourses(pendingOp.payload, pendingOp.timestamp, userId)
                                    val storedCourseIds = storedCourses.map { it.id }.sorted()
                                    storedCourseIds == targetCourseIds
                                } catch (e: Exception) {
                                    false
                                }
                            }.forEach { pendingOp ->
                                pendingOperationRepository.updateStatus(pendingOp.id, "SYNCED")
                            }
                    } else { // Para "SCHEDULE"
                        val targetScheduleIds =
                            deserializeSchedules(operation.payload, operation.timestamp, userId)
                                .map { it.id }
                                .sorted()

                        pendingOperations
                            .filter { pendingOp ->
                                try {
                                    val storedSchedules =
                                        deserializeSchedules(pendingOp.payload, pendingOp.timestamp, userId)
                                    val storedScheduleIds = storedSchedules.map { it.id }.sorted()
                                    storedScheduleIds == targetScheduleIds
                                } catch (e: Exception) {
                                    false
                                }
                            }.forEach { pendingOp ->
                                pendingOperationRepository.updateStatus(pendingOp.id, "SYNCED")
                            }
                    }
                }
            }
        }

        private fun NetworkCourse.toCourseEntity() = CourseEntity(id, name, nameProfessor, color, lastModified, userId)

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

private fun deserializeCourse(
    payload: String,
    lastModified: LocalDateTime,
    userId: String,
): NetworkCourse {
    val course = appJson.decodeFromString<NetworkCourse>(payload)
    return course.copy(lastModified = lastModified, userId = userId)
}

private fun deserializeCourses(
    payload: String,
    lastModified: LocalDateTime,
    userId: String,
): List<NetworkCourse> {
    val courses = appJson.decodeFromString<List<NetworkCourse>>(payload)
    return courses.map { it.copy(lastModified = lastModified, userId = userId) }
}

private fun deserializeSchedule(
    payload: String,
    lastModified: LocalDateTime,
    userId: String,
): NetworkSchedule {
    val schedule = appJson.decodeFromString<NetworkSchedule>(payload)
    return schedule.copy(lastModified = lastModified, userId = userId)
}

private fun deserializeSchedules(
    payload: String,
    lastModified: LocalDateTime,
    userId: String,
): List<NetworkSchedule> {
    val schedules = appJson.decodeFromString<List<NetworkSchedule>>(payload)
    return schedules.map { it.copy(lastModified = lastModified, userId = userId) }
}
