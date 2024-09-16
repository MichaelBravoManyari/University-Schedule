package com.studentsapps.data.repository

import android.content.Context
import com.studentsapps.data.broadcasts.asScheduleDetails
import com.studentsapps.data.broadcasts.cancelAlarm
import com.studentsapps.data.broadcasts.scheduleAlarm
import com.studentsapps.database.datasources.ScheduleLocalDataSource
import com.studentsapps.database.model.ScheduleDetailsView
import com.studentsapps.database.model.ScheduleEntity
import com.studentsapps.database.model.asExternalModel
import com.studentsapps.model.Schedule
import com.studentsapps.model.ScheduleDetails
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import javax.inject.Inject

class ScheduleRepositoryImp @Inject constructor(
    private val scheduleLocalDataSource: ScheduleLocalDataSource,
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
        val scheduleId = scheduleLocalDataSource.insert(with(schedule) {
            ScheduleEntity(
                id, startTime, endTime, classPlace, dayOfWeek, specificDate, courseId
            )
        })
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
                courseId = courseId
            )
        })
        scheduleAlarm(context, scheduleDetails)
    }

    override suspend fun deleteSchedule(scheduleId: Int) {
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
                courseId = courseId
            )
        })
    }

    override suspend fun getAllScheduleDetails(): List<ScheduleDetails> {
        return scheduleLocalDataSource.getAllSchedules().map(ScheduleDetailsView::asExternalModel)
    }
}