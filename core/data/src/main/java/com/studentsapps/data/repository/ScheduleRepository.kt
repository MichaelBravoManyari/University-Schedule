package com.studentsapps.data.repository

import com.studentsapps.database.model.ScheduleEntity
import com.studentsapps.model.Schedule
import com.studentsapps.model.ScheduleDetails
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ScheduleRepository {

    suspend fun getSchedulesForTimetableInGridMode(
        showSaturday: Boolean,
        showSunday: Boolean,
        startDate: LocalDate,
        endDate: LocalDate,
        userId: String
    ): List<ScheduleDetails>

    suspend fun getSchedulesForTimetableInListMode(
        date: LocalDate, userId: String
    ): List<ScheduleDetails>

    suspend fun registerSchedule(
        schedule: Schedule,
        specificDate: LocalDate?,
        courseName: String,
        courseColor: Int,
        userId: String
    )

    suspend fun registerScheduleEntity(scheduleEntity: ScheduleEntity)

    suspend fun getScheduleDetailsById(scheduleId: String, userId: String): ScheduleDetails

    suspend fun updateSchedule(
        schedule: Schedule,
        specificDate: LocalDate?,
        courseName: String,
        courseColor: Int,
        userId: String
    )

    suspend fun updateScheduleEntity(scheduleEntity: ScheduleEntity)

    suspend fun deleteSchedule(scheduleId: String, userId: String)

    suspend fun getAllScheduleDetails(userId: String): List<ScheduleDetails>

    fun getAllScheduleEntity(userId: String): Flow<List<ScheduleEntity>>
}