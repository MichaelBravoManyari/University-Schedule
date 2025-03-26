package com.studentsapps.data.repository

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.studentsapps.data.broadcasts.asScheduleDetails
import com.studentsapps.data.broadcasts.cancelAlarm
import com.studentsapps.data.broadcasts.scheduleAlarm
import com.studentsapps.data.workers.SyncPendingOperationsWorker
import com.studentsapps.database.datasources.CourseLocalDataSource
import com.studentsapps.database.datasources.PendingOperationLocalDataSource
import com.studentsapps.database.datasources.ScheduleLocalDataSource
import com.studentsapps.database.model.PendingOperationEntity
import com.studentsapps.database.model.ScheduleDetailsView
import com.studentsapps.database.model.ScheduleEntity
import com.studentsapps.database.model.asExternalModel
import com.studentsapps.model.Schedule
import com.studentsapps.model.ScheduleDetails
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScheduleRepositoryImp @Inject constructor(
    private val scheduleLocalDataSource: ScheduleLocalDataSource,
    private val courseLocalDataSource: CourseLocalDataSource,
    private val pendingOperationLocalDataSource: PendingOperationLocalDataSource,
    @ApplicationContext private val context: Context
) : ScheduleRepository {
    override suspend fun getSchedulesForTimetableInGridMode(
        showSaturday: Boolean,
        showSunday: Boolean,
        startDate: LocalDate,
        endDate: LocalDate,
        userId: String
    ): List<ScheduleDetails> {
        val schedules = scheduleLocalDataSource.getSchedulesForTimetableInGridMode(
            showSaturday, showSunday, startDate, endDate, userId
        ).map(ScheduleDetailsView::asExternalModel)

        if (schedules.isNotEmpty()) {
            val pendingOperation = PendingOperationEntity(
                operationType = "READ_LIST",
                entityType = "SCHEDULE",
                payload = serializeSchedules(schedules.map { scheduleDetails ->
                    with(scheduleDetails) {
                        Schedule(
                            id = scheduleId,
                            startTime,
                            endTime,
                            classPlace,
                            dayOfWeek,
                            specificDate,
                            courseId
                        )
                    }
                }),
                status = "PENDING",
                timestamp = LocalDateTime.now(ZoneOffset.UTC),
                userId = userId
            )
            pendingOperationLocalDataSource.insert(pendingOperation)
            scheduleSyncWorker()
        }

        return schedules
    }

    override suspend fun getSchedulesForTimetableInListMode(
        date: LocalDate, userId: String
    ): List<ScheduleDetails> {
        val schedules =
            scheduleLocalDataSource.getSchedulesForTimetableInListMode(date.dayOfWeek, date, userId)
                .map(ScheduleDetailsView::asExternalModel)

        if (schedules.isNotEmpty()) {
            val pendingOperation = PendingOperationEntity(
                operationType = "READ_LIST",
                entityType = "SCHEDULE",
                payload = serializeSchedules(schedules.map { scheduleDetails ->
                    with(scheduleDetails) {
                        Schedule(
                            id = scheduleId,
                            startTime,
                            endTime,
                            classPlace,
                            dayOfWeek,
                            specificDate,
                            courseId
                        )
                    }
                }),
                status = "PENDING",
                timestamp = LocalDateTime.now(ZoneOffset.UTC),
                userId = userId
            )
            pendingOperationLocalDataSource.insert(pendingOperation)
            scheduleSyncWorker()
        }

        return schedules
    }

    override suspend fun registerSchedule(
        schedule: Schedule,
        specificDate: LocalDate?,
        courseName: String,
        courseColor: Int,
        userId: String
    ) {
        val timestamp = LocalDateTime.now(ZoneOffset.UTC)
        val scheduleEntity = ScheduleEntity(
            startTime = schedule.startTime,
            endTime = schedule.endTime,
            classPlace = schedule.classPlace,
            dayOfWeek = schedule.dayOfWeek,
            specificDate = specificDate,
            lastModified = timestamp,
            courseId = schedule.courseId,
            userId = userId
        )

        scheduleLocalDataSource.insert(scheduleEntity)

        val pendingOperation = PendingOperationEntity(
            operationType = "REGISTER", entityType = "SCHEDULE", payload = serializeSchedule(
                schedule.copy(
                    id = scheduleEntity.id, specificDate = specificDate
                )
            ), status = "PENDING", timestamp = timestamp, userId = userId
        )
        pendingOperationLocalDataSource.insert(pendingOperation)

        scheduleSyncWorker()

        scheduleAlarm(
            context,
            schedule.asScheduleDetails(scheduleEntity.id, specificDate, courseName, courseColor)
        )
    }

    override suspend fun registerScheduleEntity(scheduleEntity: ScheduleEntity) {
        scheduleLocalDataSource.insert(scheduleEntity)
        val course = courseLocalDataSource.getCourse(scheduleEntity.courseId).first()
        val scheduleDetails = with(scheduleEntity) {
            ScheduleDetails(
                id,
                startTime,
                endTime,
                classPlace,
                dayOfWeek,
                specificDate,
                courseId,
                course.name,
                course.color
            )
        }
        scheduleAlarm(context, scheduleDetails)
    }

    override suspend fun getScheduleDetailsById(
        scheduleId: String,
        userId: String
    ): ScheduleDetails {
        val schedule = scheduleLocalDataSource.getScheduleDetailsView(scheduleId).asExternalModel()
        val pendingOperation = PendingOperationEntity(
            operationType = "READ",
            entityType = "SCHEDULE",
            payload = serializeSchedule(with(schedule) {
                Schedule(
                    scheduleId, startTime, endTime, classPlace, dayOfWeek, specificDate, courseId
                )
            }),
            status = "PENDING",
            timestamp = LocalDateTime.now(ZoneOffset.UTC),
            userId = userId
        )
        pendingOperationLocalDataSource.insert(pendingOperation)
        scheduleSyncWorker()

        return schedule
    }

    override suspend fun updateSchedule(
        schedule: Schedule,
        specificDate: LocalDate?,
        courseName: String,
        courseColor: Int,
        userId: String
    ) {
        val timestamp = LocalDateTime.now(ZoneOffset.UTC)
        val scheduleDetails =
            schedule.asScheduleDetails(schedule.id, specificDate, courseName, courseColor)
        cancelAlarm(context, scheduleDetails)
        scheduleLocalDataSource.updateSchedule(with(schedule) {
            ScheduleEntity(
                id = id,
                startTime = startTime,
                endTime = endTime,
                classPlace = classPlace,
                dayOfWeek = dayOfWeek,
                specificDate = specificDate,
                timestamp,
                courseId = courseId,
                userId = userId
            )
        })

        val pendingOperation = PendingOperationEntity(
            operationType = "UPDATE",
            entityType = "SCHEDULE",
            payload = serializeSchedule(schedule.copy(specificDate = specificDate)),
            status = "PENDING",
            timestamp = timestamp,
            userId = userId
        )

        pendingOperationLocalDataSource.insert(pendingOperation)

        scheduleSyncWorker()

        scheduleAlarm(context, scheduleDetails)
    }

    override suspend fun updateScheduleEntity(scheduleEntity: ScheduleEntity) {
        scheduleLocalDataSource.updateSchedule(scheduleEntity)
        val course = courseLocalDataSource.getCourse(scheduleEntity.courseId).first()
        val scheduleDetails = with(scheduleEntity) {
            ScheduleDetails(
                scheduleId = id,
                startTime,
                endTime,
                classPlace,
                dayOfWeek,
                specificDate,
                courseId,
                course.name,
                course.color
            )
        }
        cancelAlarm(context, scheduleDetails)
        scheduleAlarm(context, scheduleDetails)
    }

    override suspend fun deleteSchedule(scheduleId: String, userId: String) {
        val timestamp = LocalDateTime.now(ZoneOffset.UTC)
        val scheduleDetails =
            scheduleLocalDataSource.getScheduleDetailsView(scheduleId).asExternalModel()

        cancelAlarm(context, scheduleDetails)
        scheduleLocalDataSource.deleteSchedule(with(scheduleDetails) {
            ScheduleEntity(
                id = scheduleId,
                startTime = startTime,
                endTime = endTime,
                classPlace = classPlace,
                dayOfWeek = dayOfWeek,
                specificDate = specificDate,
                timestamp,
                courseId = courseId,
                userId = userId
            )
        })

        val pendingOperationSchedule = PendingOperationEntity(
            operationType = "DELETE",
            entityType = "SCHEDULE",
            payload = serializeSchedule(with(scheduleDetails) {
                Schedule(
                    id = scheduleId,
                    startTime,
                    endTime,
                    classPlace,
                    dayOfWeek,
                    specificDate,
                    courseId
                )
            }),
            status = "PENDING",
            timestamp = timestamp,
            userId = userId
        )

        pendingOperationLocalDataSource.insert(pendingOperationSchedule)

        scheduleSyncWorker()
    }

    override suspend fun getAllScheduleDetails(userId: String): List<ScheduleDetails> {
        return scheduleLocalDataSource.getAllSchedules(userId)
            .map(ScheduleDetailsView::asExternalModel)
    }

    override fun getAllScheduleEntity(userId: String): Flow<List<ScheduleEntity>> {
        return scheduleLocalDataSource.getAllScheduleEntity(userId)
    }

    private fun serializeSchedule(schedule: Schedule): String {
        val gson = GsonBuilder().registerTypeAdapter(LocalTime::class.java, LocalTimeAdapter())
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter()).create()
        return gson.toJson(schedule)
    }

    private fun serializeSchedules(schedules: List<Schedule>): String {
        val gson = GsonBuilder().registerTypeAdapter(LocalTime::class.java, LocalTimeAdapter())
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter()).create()
        return gson.toJson(schedules)
    }

    private fun scheduleSyncWorker() {
        val syncRequest = OneTimeWorkRequestBuilder<SyncPendingOperationsWorker>().setConstraints(
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        ).setBackoffCriteria(
            BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS
        ).build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork("SyncPendingOperations", ExistingWorkPolicy.APPEND, syncRequest)
    }
}

class LocalDateAdapter : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun serialize(
        src: LocalDate?, typeOfSrc: Type?, context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.format(formatter))
    }

    override fun deserialize(
        json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?
    ): LocalDate {
        return LocalDate.parse(
            json?.asString, formatter
        )
    }
}