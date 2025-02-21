package com.studentsapps.data.repository

import android.content.Context
import androidx.work.Constraints
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
import com.studentsapps.database.datasources.PendingOperationLocalDataSource
import com.studentsapps.database.datasources.ScheduleLocalDataSource
import com.studentsapps.database.model.PendingOperationEntity
import com.studentsapps.database.model.ScheduleDetailsView
import com.studentsapps.database.model.ScheduleEntity
import com.studentsapps.database.model.asExternalModel
import com.studentsapps.model.Schedule
import com.studentsapps.model.ScheduleDetails
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ScheduleRepositoryImp @Inject constructor(
    private val scheduleLocalDataSource: ScheduleLocalDataSource,
    private val pendingOperationLocalDataSource: PendingOperationLocalDataSource,
    @ApplicationContext private val context: Context
) : ScheduleRepository {
    override suspend fun getSchedulesForTimetableInGridMode(
        showSaturday: Boolean, showSunday: Boolean, startDate: LocalDate, endDate: LocalDate
    ): List<ScheduleDetails> {
        return scheduleLocalDataSource.getSchedulesForTimetableInGridMode(
            showSaturday, showSunday, startDate, endDate
        ).map(ScheduleDetailsView::asExternalModel)
    }

    override suspend fun getSchedulesForTimetableInListMode(date: LocalDate): List<ScheduleDetails> {
        return scheduleLocalDataSource.getSchedulesForTimetableInListMode(date.dayOfWeek, date)
            .map(ScheduleDetailsView::asExternalModel)
    }

    override suspend fun registerSchedule(
        schedule: Schedule,
        specificDate: LocalDate?,
        courseName: String,
        courseColor: Int
    ) {
        val timestamp = LocalDateTime.now(ZoneOffset.UTC)
        val scheduleId = scheduleLocalDataSource.insert(with(schedule) {
            ScheduleEntity(
                id,
                startTime,
                endTime,
                classPlace,
                dayOfWeek,
                specificDate,
                timestamp,
                courseId
            )
        })

        val pendingOperation = PendingOperationEntity(
            operationType = "REGISTER",
            entityType = "SCHEDULE",
            payload = serializeSchedule(
                schedule.copy(
                    id = scheduleId.toInt(),
                    specificDate = specificDate
                )
            ),
            status = "PENDING",
            timestamp = timestamp
        )
        pendingOperationLocalDataSource.insert(pendingOperation)

        scheduleSyncWorker()

        scheduleAlarm(
            context,
            schedule.asScheduleDetails(scheduleId.toInt(), specificDate, courseName, courseColor)
        )
    }

    override suspend fun getScheduleDetailsById(scheduleId: Int): ScheduleDetails =
        scheduleLocalDataSource.getScheduleDetailsView(scheduleId).asExternalModel()

    override suspend fun updateSchedule(
        schedule: Schedule,
        specificDate: LocalDate?,
        courseName: String,
        courseColor: Int
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
                courseId = courseId
            )
        })

        val pendingOperation = PendingOperationEntity(
            operationType = "UPDATE",
            entityType = "SCHEDULE",
            payload = serializeSchedule(schedule.copy(specificDate = specificDate)),
            status = "PENDING",
            timestamp = timestamp
        )

        pendingOperationLocalDataSource.insert(pendingOperation)

        scheduleSyncWorker()

        scheduleAlarm(context, scheduleDetails)
    }

    override suspend fun deleteSchedule(scheduleId: Int) {
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
                courseId = courseId
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
            timestamp = timestamp
        )

        pendingOperationLocalDataSource.insert(pendingOperationSchedule)

        scheduleSyncWorker()
    }

    override suspend fun getAllScheduleDetails(): List<ScheduleDetails> {
        return scheduleLocalDataSource.getAllSchedules().map(ScheduleDetailsView::asExternalModel)
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
            .build()

        WorkManager.getInstance(context).enqueue(syncRequest)
    }
}

class LocalDateAdapter : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun serialize(
        src: LocalDate?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.format(formatter))
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDate {
        return LocalDate.parse(
            json?.asString,
            formatter
        )
    }
}