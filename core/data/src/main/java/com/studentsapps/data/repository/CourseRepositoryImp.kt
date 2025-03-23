package com.studentsapps.data.repository

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.studentsapps.data.workers.SyncPendingOperationsWorker
import com.studentsapps.database.datasources.CourseLocalDataSource
import com.studentsapps.database.datasources.PendingOperationLocalDataSource
import com.studentsapps.database.datasources.ScheduleLocalDataSource
import com.studentsapps.database.model.CourseEntity
import com.studentsapps.database.model.PendingOperationEntity
import com.studentsapps.database.model.asExternalModel
import com.studentsapps.model.Course
import com.studentsapps.model.Schedule
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CourseRepositoryImp @Inject constructor(
    private val courseLocalDataSource: CourseLocalDataSource,
    private val scheduleLocalDataSource: ScheduleLocalDataSource,
    private val pendingOperationLocalDataSource: PendingOperationLocalDataSource,
    @ApplicationContext private val context: Context
) : CourseRepository {

    override fun getCourse(courseId: String): Flow<Course> {
        return courseLocalDataSource.getCourse(courseId)
            .onEach { course ->
                val pendingOperation = PendingOperationEntity(
                    operationType = "READ",
                    entityType = "COURSE",
                    payload = serializeCourse(course.asExternalModel()),
                    status = "PENDING",
                    timestamp = LocalDateTime.now(ZoneOffset.UTC),
                    userId = course.userId
                )
                pendingOperationLocalDataSource.insert(pendingOperation)
                scheduleSyncWorker()
            }
            .map(CourseEntity::asExternalModel)
    }

    override suspend fun registerCourse(course: Course, userId: String): String {
        val timestamp = LocalDateTime.now(ZoneOffset.UTC)
        val courseEntity = CourseEntity(
            name = course.name,
            nameProfessor = course.nameProfessor,
            color = course.color,
            lastModified = timestamp,
            userId = userId
        )
        courseLocalDataSource.insert(courseEntity)

        val pendingOperation = PendingOperationEntity(
            operationType = "REGISTER",
            entityType = "COURSE",
            payload = serializeCourse(course.copy(id = courseEntity.id)),
            status = "PENDING",
            timestamp = timestamp,
            userId = userId
        )
        pendingOperationLocalDataSource.insert(pendingOperation)
        scheduleSyncWorker()

        return courseEntity.id
    }

    override suspend fun registerCourseEntity(courseEntity: CourseEntity) {
        courseLocalDataSource.insert(courseEntity)
    }

    override fun getAllCourse(shouldSync: Boolean, userId: String): Flow<List<Course>> {
        return courseLocalDataSource.getAllCourse(userId)
            .onEach { courses ->
                if (shouldSync) {
                    val pendingOperation = PendingOperationEntity(
                        operationType = "READ_LIST",
                        entityType = "COURSE",
                        payload = serializeCourses(courses.map { it.asExternalModel() }),
                        status = "PENDING",
                        timestamp = LocalDateTime.now(ZoneOffset.UTC),
                        userId = userId
                    )
                    pendingOperationLocalDataSource.insert(pendingOperation)
                    scheduleSyncWorker()
                }
            }
            .map { it.map(CourseEntity::asExternalModel) }
    }

    override suspend fun updateCourse(course: Course, userId: String) {
        val timestamp = LocalDateTime.now(ZoneOffset.UTC)
        courseLocalDataSource.updateCourse(with(course) {
            CourseEntity(
                id, name, nameProfessor, color, timestamp, userId
            )
        })

        val pendingOperation = PendingOperationEntity(
            operationType = "UPDATE",
            entityType = "COURSE",
            payload = serializeCourse(course),
            status = "PENDING",
            timestamp = timestamp,
            userId = userId
        )

        pendingOperationLocalDataSource.insert(pendingOperation)

        scheduleSyncWorker()
    }

    override suspend fun updateCourseEntity(courseEntity: CourseEntity) {
        courseLocalDataSource.updateCourse(courseEntity)
    }

    override suspend fun deleteCourse(courseId: String) {
        val courseEntity = courseLocalDataSource.getCourse(courseId).first()

        if (courseEntity != null) {
            val schedules = scheduleLocalDataSource.getSchedulesByCourseId(courseId)

            for (schedule in schedules) {
                scheduleLocalDataSource.deleteSchedule(schedule)

                val pendingOperationSchedule = PendingOperationEntity(
                    operationType = "DELETE",
                    entityType = "SCHEDULE",
                    payload = serializeSchedule(schedule.asExternalModel()),
                    status = "PENDING",
                    timestamp = LocalDateTime.now(ZoneOffset.UTC),
                    userId = schedule.userId
                )

                pendingOperationLocalDataSource.insert(pendingOperationSchedule)

                scheduleSyncWorker()
            }

            courseLocalDataSource.deleteCourse(courseEntity)

            val pendingOperationCourse = PendingOperationEntity(
                operationType = "DELETE",
                entityType = "COURSE",
                payload = serializeCourse(courseEntity.asExternalModel()),
                status = "PENDING",
                timestamp = LocalDateTime.now(),
                userId = courseEntity.userId
            )

            pendingOperationLocalDataSource.insert(pendingOperationCourse)

            scheduleSyncWorker()
        }
    }

    override fun getCoursesByIds(courseIds: List<String>): Flow<List<Course>> =
        courseLocalDataSource.getCoursesByIds(courseIds).map { entities ->
            entities.map { it.asExternalModel() }
        }

    override fun getAllCourseEntity(userId: String): Flow<List<CourseEntity>> {
        return courseLocalDataSource.getAllCourse(userId)
    }

    private fun serializeCourse(course: Course): String {
        return Gson().toJson(course)
    }

    private fun serializeCourses(courses: List<Course>): String {
        return Gson().toJson(courses)
    }

    private fun serializeSchedule(schedule: Schedule): String {
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalTime::class.java, LocalTimeAdapter())
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .create()
        return gson.toJson(schedule)
    }

    private fun scheduleSyncWorker() {
        val syncRequest = OneTimeWorkRequestBuilder<SyncPendingOperationsWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                10,
                TimeUnit.SECONDS
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork("SyncPendingOperations", ExistingWorkPolicy.APPEND, syncRequest)
    }
}

class LocalTimeAdapter : JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {
    override fun serialize(
        src: LocalTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.toString())
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalTime {
        return LocalTime.parse(json?.asString)
    }
}