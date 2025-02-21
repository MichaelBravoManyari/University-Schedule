package com.studentsapps.network.datasources

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.studentsapps.network.model.NetworkSchedule
import kotlinx.coroutines.tasks.await
import java.time.ZoneOffset
import javax.inject.Inject

class ScheduleNetworkDataSource @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun updateOrCreateSchedule(userId: String, schedule: NetworkSchedule) {
        try {
            val scheduleData = mapOf(
                "startTime" to schedule.startTime,
                "endTime" to schedule.endTime,
                "classPlace" to schedule.classPlace,
                "dayOfWeek" to schedule.dayOfWeek,
                "specificDate" to schedule.specificDate,
                "courseId" to schedule.courseId,
                "lastModified" to Timestamp(
                    schedule.lastModified.toInstant(ZoneOffset.UTC).epochSecond,
                    schedule.lastModified.toInstant(ZoneOffset.UTC).nano / 1000
                )
            )

            firestore.collection("users")
                .document(userId)
                .collection("schedules")
                .document(schedule.id.toString())
                .set(scheduleData, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            throw RuntimeException("Error updating or creating schedule: ${e.message}", e)
        }
    }

    suspend fun deleteSchedule(userId: String, scheduleId: Int) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("schedules")
                .document(scheduleId.toString())
                .delete()
                .await()
        } catch (e: Exception) {
            throw RuntimeException("Error deleting course: ${e.message}", e)
        }
    }
}