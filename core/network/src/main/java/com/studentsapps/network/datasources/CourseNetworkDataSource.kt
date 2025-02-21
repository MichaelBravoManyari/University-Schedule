package com.studentsapps.network.datasources

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.studentsapps.network.model.NetworkCourse
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.time.ZoneId
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
                "lastModified" to Timestamp(course.lastModified.toInstant(ZoneOffset.UTC).toEpochMilli() / 1000, 0)
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

    suspend fun getCourseById(userId: String, courseId: String): NetworkCourse? {
        return try {
            val documentSnapshot = firestore.collection("users")
                .document(userId)
                .collection("courses")
                .document(courseId)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val name = documentSnapshot.getString("name") ?: ""
                val nameProfessor = documentSnapshot.getString("nameProfessor") ?: ""
                val color = documentSnapshot.get("color").toString().toInt()
                val timestamp = documentSnapshot.getTimestamp("lastModified")?.toDate()
                val lastModified = timestamp?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()

                NetworkCourse(
                    id = courseId.toInt(),
                    name = name,
                    nameProfessor = nameProfessor,
                    color = color,
                    lastModified = lastModified!!
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("CourseNetworkDataSource", "Error obteniendo el curso: ${e.message}", e)
            null
        }
    }

    suspend fun getCoursesByIds(userId: String, courseIds: List<String>): List<NetworkCourse> {
        if (courseIds.isEmpty()) return emptyList()

        return try {
            val querySnapshot = firestore.collection("users")
                .document(userId)
                .collection("courses")
                .whereIn("id", courseIds)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { documentSnapshot ->
                val name = documentSnapshot.getString("name") ?: ""
                val nameProfessor = documentSnapshot.getString("nameProfessor") ?: ""
                val color = documentSnapshot.getString("color") ?: ""
                val phoneTimeZone = ZoneId.systemDefault()
                val timestamp = documentSnapshot.getTimestamp("lastModified")?.toDate()
                val lastModifiedLocal = timestamp?.toInstant()
                    ?.atZone(ZoneId.of("UTC-5"))
                    ?.withZoneSameInstant(phoneTimeZone)
                    ?.toLocalDateTime()
                NetworkCourse(
                    id = documentSnapshot.id.toInt(),
                    name = name,
                    nameProfessor = nameProfessor,
                    color = color.toInt(),
                    lastModified = lastModifiedLocal!!
                )
            }
        } catch (e: Exception) {
            Log.e("CourseNetworkDataSource", "Error obteniendo los cursos: ${e.message}", e)
            emptyList()
        }
    }
}