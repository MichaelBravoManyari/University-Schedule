package com.studentsapps.sync.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.studentsapps.data.repository.PendingOperationRepository
import com.studentsapps.network.datasources.CourseNetworkDataSource
import com.studentsapps.network.datasources.ScheduleNetworkDataSource
import com.studentsapps.network.model.NetworkCourse
import com.studentsapps.network.model.NetworkSchedule
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

@HiltWorker
class SyncPendingOperationsWorker @AssistedInject constructor(
    private val pendingOperationRepository: PendingOperationRepository,
    private val courseNetworkDataSource: CourseNetworkDataSource,
    private val scheduleNetworkDataSource: ScheduleNetworkDataSource,
    private val auth: FirebaseAuth,
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val userId = auth.currentUser?.uid
            ?: return Result.failure()

        val pendingOperations =
            pendingOperationRepository.getPendingOperations("PENDING").firstOrNull()
                ?: return Result.success()

        pendingOperations.forEach { operation ->
            try {
                when (operation.operationType) {
                    "REGISTER", "UPDATE" -> {
                        val course = deserializeCourse(operation.payload, operation.timestamp)
                        courseNetworkDataSource.updateOrCreateCourse(userId = userId, course)
                    }

                    "DELETE" -> {
                        val isCourse = operation.entityType == "COURSE"
                        if (isCourse) {
                            val course = deserializeCourse(operation.payload, operation.timestamp)
                            courseNetworkDataSource.deleteCourse(userId = userId, courseId = course.id)
                        } else {
                            val schedule = deserializeSchedule(operation.payload, operation.timestamp)
                            scheduleNetworkDataSource.deleteSchedule(userId = userId, scheduleId = schedule.id)
                        }
                    }
                }
                pendingOperationRepository.updateStatus(operation.id, "SYNCED")
            } catch (e: Exception) {
                pendingOperationRepository.updateStatus(operation.id, "FAILED")
                Log.e("SyncPendingOperationsWorker", "Error en el Worker: ${e.message}", e)
            }
        }

        return Result.success()
    }
}

private fun deserializeCourse(payload: String, lastModified: LocalDateTime): NetworkCourse {
    val course = Json.decodeFromString<NetworkCourse>(payload)
    return course.copy(lastModified = lastModified)
}

private fun deserializeSchedule(payload: String, lastModified: LocalDateTime): NetworkSchedule {
    val schedule = Json.decodeFromString<NetworkSchedule>(payload)
    return schedule.copy(lastModified = lastModified)
}