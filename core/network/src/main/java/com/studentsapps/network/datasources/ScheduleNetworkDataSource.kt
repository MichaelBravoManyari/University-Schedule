package com.studentsapps.network.datasources

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ScheduleNetworkDataSource @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()

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