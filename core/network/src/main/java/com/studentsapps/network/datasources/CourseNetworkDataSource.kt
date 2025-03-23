package com.studentsapps.network.datasources

import android.util.Log
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
                "id" to course.id,
                "name" to course.name,
                "nameProfessor" to course.nameProfessor,
                "color" to course.color,
                "lastModified" to Timestamp(
                    course.lastModified.toInstant(ZoneOffset.UTC).toEpochMilli() / 1000, 0
                ),
                "userId" to userId
            )

            firestore.collection("users")
                .document(userId)
                .collection("courses")
                .document(course.id)
                .set(courseData, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            throw RuntimeException("Error updating or creating course: ${e.message}", e)
        }
    }

    suspend fun deleteCourse(userId: String, courseId: String) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("courses")
                .document(courseId)
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
                    id = courseId,
                    name = name,
                    nameProfessor = nameProfessor,
                    color = color,
                    lastModified = lastModified!!,
                    userId = userId
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
                val color = documentSnapshot.getLong("color")?.toInt() ?: 0
                val timestamp = documentSnapshot.getTimestamp("lastModified")?.toDate()
                val lastModifiedUtc =
                    timestamp?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()

                NetworkCourse(
                    id = documentSnapshot.getString("id") ?: "",
                    name = name,
                    nameProfessor = nameProfessor,
                    color = color,
                    lastModified = lastModifiedUtc!!,
                    userId = userId
                )
            }
        } catch (e: Exception) {
            Log.e("CourseNetworkDataSource", "Error obteniendo los cursos: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getAllCourses(userId: String): List<NetworkCourse> {
        return try {
            val querySnapshot = firestore.collection("users")
                .document(userId)
                .collection("courses")
                .get()
                .await()

            querySnapshot.documents.mapNotNull { documentSnapshot ->
                val id = documentSnapshot.getString("id")
                val name = documentSnapshot.getString("name")
                val nameProfessor = documentSnapshot.getString("nameProfessor")
                val color = documentSnapshot.getLong("color")?.toInt()
                val timestamp = documentSnapshot.getTimestamp("lastModified")?.toDate()

                if (id == null || name.isNullOrEmpty()) return@mapNotNull null

                val lastModifiedUtc = timestamp?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()

                NetworkCourse(
                    id = id,
                    name = name,
                    nameProfessor = nameProfessor.orEmpty(),
                    color = color ?: 0,
                    lastModified = lastModifiedUtc!!,
                    userId = userId
                )
            }
        } catch (e: Exception) {
            Log.e("CourseNetworkDataSource", "Error obteniendo todos los cursos: ${e.message}", e)
            emptyList()
        }
    }

}