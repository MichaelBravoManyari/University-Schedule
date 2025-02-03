package com.studentsapps.network.datasources

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.studentsapps.network.model.NetworkCourse
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.time.ZoneOffset
import javax.inject.Inject

class CourseNetworkDataSource @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun updateOrCreateCourse(userId: String, course: NetworkCourse) {
        try {
            val courseData = mapOf(
                "name" to course.name,
                "nameProfessor" to course.nameProfessor,
                "color" to course.color,
                "lastModified" to Timestamp(
                    course.lastModified.toInstant(ZoneOffset.UTC).epochSecond,
                    course.lastModified.toInstant(ZoneOffset.UTC).nano / 1000
                )
            )

            firestore.collection("users")
                .document(userId)
                .collection("courses")
                .document(course.id.toString())
                .set(courseData, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            throw RuntimeException("Error updating or creating course: ${e.message}", e)
        }
    }

    suspend fun deleteCourse(userId: String, courseId: Int) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("courses")
                .document(courseId.toString())
                .delete()
                .await()
        } catch (e: Exception) {
            throw RuntimeException("Error deleting course: ${e.message}", e)
        }
    }

}