package com.studentsapps.data.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.studentsapps.data.repository.CourseRepository
import com.studentsapps.data.repository.PendingOperationRepository
import com.studentsapps.database.datasources.CourseLocalDataSource
import com.studentsapps.database.model.CourseEntity
import com.studentsapps.network.datasources.CourseNetworkDataSource
import com.studentsapps.network.datasources.ScheduleNetworkDataSource
import com.studentsapps.network.model.NetworkCourse
import com.studentsapps.network.model.NetworkSchedule
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

@HiltWorker
class SyncPendingOperationsWorker @AssistedInject constructor(
    private val pendingOperationRepository: PendingOperationRepository,
    private val courseNetworkDataSource: CourseNetworkDataSource,
    private val scheduleNetworkDataSource: ScheduleNetworkDataSource,
    private val auth: FirebaseAuth,
    private val courseLocalDataSource: CourseLocalDataSource,
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
                        val isCourse = operation.entityType == "COURSE"
                        if (isCourse) {
                            val course = deserializeCourse(operation.payload, operation.timestamp)
                            courseNetworkDataSource.updateOrCreateCourse(userId = userId, course)
                        } else {
                            val schedule =
                                deserializeSchedule(operation.payload, operation.timestamp)
                            scheduleNetworkDataSource.updateOrCreateSchedule(userId, schedule)
                        }
                    }

                    "DELETE" -> {
                        val isCourse = operation.entityType == "COURSE"
                        if (isCourse) {
                            val course = deserializeCourse(operation.payload, operation.timestamp)
                            courseNetworkDataSource.deleteCourse(
                                userId = userId,
                                courseId = course.id
                            )
                        } else {
                            val schedule =
                                deserializeSchedule(operation.payload, operation.timestamp)
                            scheduleNetworkDataSource.deleteSchedule(
                                userId = userId,
                                scheduleId = schedule.id
                            )
                        }
                    }

                    "READ" -> {
                        if (operation.entityType == "COURSE") {
                            // Deserealizar el course y obtener el courseId.
                            val courseId =
                                deserializeCourse(operation.payload, operation.timestamp).id
                            // Comunicarse con courseNetworkDataSource para obtener el curso de firebase mediante el courseId.
                            val remoteCourse =
                                courseNetworkDataSource.getCourseById(userId, courseId.toString())
                            // Obtener la version mas reciente del course de la base de datos local.
                            val localCourse = courseLocalDataSource.getCourse(courseId).first()
                            Log.e(
                                "SyncPendingOperationsWorker",
                                "remoteCourse: $remoteCourse, localCourse: $localCourse"
                            )
                            // Verificar su atributo lastModified, si el lastModified de firebase es mayor al del local, entonces se
                            // debe actualizar el curso de la base de datos local con la info de firebase.
                            if (remoteCourse != null && remoteCourse.lastModified.isAfter(
                                    localCourse.lastModified
                                )
                            ) {
                                courseLocalDataSource.updateCourse(with(remoteCourse) {
                                    CourseEntity(
                                        id,
                                        name,
                                        nameProfessor,
                                        color,
                                        lastModified
                                    )
                                })
                            }
                        } else {
                            // para Schedules
                        }
                    }

                    "READ_LIST" -> {
                        if (operation.entityType == "COURSE") {
                            // Deserealizar la lista de cursos y obtener sus coursesIds.
                            val courseIds =
                                deserializeCourses(
                                    operation.payload,
                                    operation.timestamp
                                ).map { it.id.toString() }
                            // Comunicarse con courseNetworkDataSource para obtener los cursos de firebase mediante el courseId.
                            val remoteCourses =
                                courseNetworkDataSource.getCoursesByIds(userId, courseIds)
                            // Obtener la version mas reciente de los courses de la base de datos local.
                            val localCourses =
                                courseLocalDataSource.getCoursesByIds(courseIds).first()
                            // Por cada curso de la base de datos local, buscar por el courseId en la lista de remoteCourses,
                            // si el lastModified de firebase es mayor al del local, entonces se
                            // debe actualizar el curso de la base de datos local con la info de firebase.
                            remoteCourses.forEach { remoteCourse ->
                                val localCourse = localCourses.find { it.id == remoteCourse.id }

                                if (localCourse == null || remoteCourse.lastModified.isAfter(
                                        localCourse.lastModified
                                    )
                                ) {
                                    courseLocalDataSource.updateCourse(
                                        CourseEntity(
                                            id = remoteCourse.id,
                                            name = remoteCourse.name,
                                            nameProfessor = remoteCourse.nameProfessor,
                                            color = remoteCourse.color,
                                            lastModified = remoteCourse.lastModified
                                        )
                                    )
                                    Log.d("SyncWorker", "Updated course: ${remoteCourse.id}")
                                }
                            }
                        } else {
                            // Para schedules
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

private fun deserializeCourses(payload: String, lastModified: LocalDateTime): List<NetworkCourse> {
    val courses = Json.decodeFromString<List<NetworkCourse>>(payload)
    return courses.map { it.copy(lastModified = lastModified) }
}

private fun deserializeSchedule(payload: String, lastModified: LocalDateTime): NetworkSchedule {
    val schedule = Json.decodeFromString<NetworkSchedule>(payload)
    return schedule.copy(lastModified = lastModified)
}