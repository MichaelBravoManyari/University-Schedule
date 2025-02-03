package com.studentsapps.data.repository

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.studentsapps.database.datasources.CourseLocalDataSource
import com.studentsapps.database.datasources.PendingOperationLocalDataSource
import com.studentsapps.database.datasources.ScheduleLocalDataSource
import com.studentsapps.database.model.CourseEntity
import com.studentsapps.database.model.PendingOperationEntity
import com.studentsapps.database.model.asExternalModel
import com.studentsapps.model.Course
import com.studentsapps.model.Schedule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

class CourseRepositoryImp @Inject constructor(
    private val courseLocalDataSource: CourseLocalDataSource,
    private val scheduleLocalDataSource: ScheduleLocalDataSource,
    private val pendingOperationLocalDataSource: PendingOperationLocalDataSource,
) : CourseRepository {
    override fun getCourse(courseId: Int): Flow<Course> =
        courseLocalDataSource.getCourse(courseId).map(CourseEntity::asExternalModel)

    override suspend fun registerCourse(course: Course): Long {
        val courseId = courseLocalDataSource.insert(with(course) {
            CourseEntity(
                id,
                name,
                nameProfessor,
                color,
                LocalDateTime.now()
            )
        })

        val pendingOperation = PendingOperationEntity(
            operationType = "REGISTER",
            entityType = "COURSE",
            payload = serializeCourse(course.copy(id = courseId.toInt())),
            status = "PENDING",
            timestamp = LocalDateTime.now()
        )
        pendingOperationLocalDataSource.insert(pendingOperation)

        return courseId
    }

    override fun getAllCourse(): Flow<List<Course>> =
        courseLocalDataSource.getAllCourse().map { it.map(CourseEntity::asExternalModel) }

    override suspend fun updateCourse(course: Course) {
        courseLocalDataSource.updateCourse(with(course) {
            CourseEntity(
                id, name, nameProfessor, color, LocalDateTime.now()
            )
        })

        val pendingOperation = PendingOperationEntity(
            operationType = "UPDATE",
            entityType = "COURSE",
            payload = serializeCourse(course),
            status = "PENDING",
            timestamp = LocalDateTime.now()
        )

        pendingOperationLocalDataSource.insert(pendingOperation)
    }

    override suspend fun deleteCourse(courseId: Int) {
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
                    timestamp = LocalDateTime.now()
                )
                pendingOperationLocalDataSource.insert(pendingOperationSchedule)
            }

            courseLocalDataSource.deleteCourse(courseEntity)

            val pendingOperationCourse = PendingOperationEntity(
                operationType = "DELETE",
                entityType = "COURSE",
                payload = serializeCourse(courseEntity.asExternalModel()),
                status = "PENDING",
                timestamp = LocalDateTime.now()
            )
            pendingOperationLocalDataSource.insert(pendingOperationCourse)
        }
    }

    private fun serializeCourse(course: Course): String {
        return Gson().toJson(course)
    }

    private fun serializeSchedule(schedule: Schedule): String {
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalTime::class.java, LocalTimeAdapter())
            .create()
        return gson.toJson(schedule)
    }
}

class LocalTimeAdapter : JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {
    override fun serialize(src: LocalTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src?.toString()) // "HH:mm:ss"
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalTime {
        return LocalTime.parse(json?.asString) // "HH:mm:ss" a LocalTime
    }
}