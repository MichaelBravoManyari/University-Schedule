package com.studentsapps.sync

import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.studentsapps.data.repository.CourseRepository
import com.studentsapps.data.repository.PendingOperationRepository
import com.studentsapps.data.repository.ScheduleRepository
import com.studentsapps.database.model.CourseEntity
import com.studentsapps.database.model.ScheduleEntity
import com.studentsapps.network.datasources.CourseNetworkDataSource
import com.studentsapps.network.datasources.ScheduleNetworkDataSource
import com.studentsapps.network.model.NetworkCourse
import com.studentsapps.network.model.NetworkSchedule
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import javax.inject.Inject

class SynchronizationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val pendingOperations: PendingOperationRepository,
    private val courseNetworkDataSource: CourseNetworkDataSource,
    private val scheduleNetworkDataSource: ScheduleNetworkDataSource,
    private val courseRepository: CourseRepository,
    private val scheduleRepository: ScheduleRepository
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun startSyncIfNeeded(dialog: AlertDialog) {
        val userId = auth.currentUser?.uid ?: return

        if (!isInternetAvailable()) return

        coroutineScope.launch {
            dialog.show()
            var hasSynced = false

            pendingOperations.getPendingOperations("PENDING", userId)
                .takeWhile { !hasSynced }
                .collect { pendingOperations ->
                    if (isInternetAvailable()) {
                        if (pendingOperations.isEmpty() && !hasSynced) {
                            hasSynced = true
                            startSynchronization(dialog, userId)
                        }
                    } else {
                        hasSynced = true
                        dialog.dismiss()
                    }
                }
        }
    }

    private suspend fun startSynchronization(dialog: Dialog, userId: String) {
        try {
            val coursesFirebase = courseNetworkDataSource.getAllCourses(userId)
            val scheduleFirebase = scheduleNetworkDataSource.getAllSchedules(userId)
            val coursesLocal = courseRepository.getAllCourseEntity(userId).first()
            val schedulesLocal = scheduleRepository.getAllScheduleEntity(userId).first()

            coursesFirebase.forEach { remoteCourse ->
                val localCourse = coursesLocal.find { it.id == remoteCourse.id }
                if (localCourse != null) {
                    if (remoteCourse.lastModified.isAfter(localCourse.lastModified)) {
                        courseRepository.updateCourseEntity(remoteCourse.toCourseEntity())
                    }
                } else {
                    courseRepository.registerCourseEntity(remoteCourse.toCourseEntity())
                }
            }

            scheduleFirebase.forEach { remoteSchedule ->
                val localSchedule = schedulesLocal.find { it.id == remoteSchedule.id }
                if (localSchedule != null) {
                    if (remoteSchedule.lastModified.isAfter(localSchedule.lastModified)) {
                        scheduleRepository.updateScheduleEntity(remoteSchedule.toScheduleEntity())
                    }
                } else {
                    scheduleRepository.registerScheduleEntity(remoteSchedule.toScheduleEntity())
                }
            }

            val remoteCourseIds = coursesFirebase.map { it.id }.toSet()
            val toCourseDelete = coursesLocal.filterNot { it.id in remoteCourseIds }
            toCourseDelete.forEach {
                courseRepository.deleteCourseEntity(it.id)
            }

            val remoteScheduleIds = scheduleFirebase.map { it.id }.toSet()
            val toScheduleDelete = schedulesLocal.filterNot { it.id in remoteScheduleIds }
            toScheduleDelete.forEach {
                scheduleRepository.deleteScheduleEntity(it.id, it.userId)
            }

            scheduleRepository.scheduleAllUserAlarms(userId)
        } finally {
            dialog.dismiss()
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private fun NetworkCourse.toCourseEntity() =
        CourseEntity(id, name, nameProfessor, color, lastModified, userId)

    private fun NetworkSchedule.toScheduleEntity() = ScheduleEntity(
        id,
        startTime,
        endTime,
        classPlace,
        dayOfWeek,
        specificDate,
        lastModified,
        userId,
        courseId
    )
}