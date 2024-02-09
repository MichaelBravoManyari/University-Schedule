package com.studentsapps.data.repository

import com.studentsapps.database.datasources.ScheduleLocalDataSource
import com.studentsapps.database.model.ScheduleDetailsView
import com.studentsapps.database.model.ScheduleEntity
import com.studentsapps.database.model.asExternalModel
import com.studentsapps.model.Schedule
import com.studentsapps.model.ScheduleDetails
import java.time.LocalDate
import javax.inject.Inject

class ScheduleRepositoryImp @Inject constructor(
    private val scheduleLocalDataSource: ScheduleLocalDataSource
) : ScheduleRepository {
    override suspend fun getSchedulesForTimetableInGridMode(
        showSaturday: Boolean,
        showSunday: Boolean,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<ScheduleDetails> {
        return scheduleLocalDataSource.getSchedulesForTimetableInGridMode(
            showSaturday,
            showSunday,
            startDate,
            endDate
        ).map(ScheduleDetailsView::asExternalModel)
    }

    override suspend fun getSchedulesForTimetableInListMode(date: LocalDate): List<ScheduleDetails> {
        return scheduleLocalDataSource.getSchedulesForTimetableInListMode(date.dayOfWeek, date)
            .map(ScheduleDetailsView::asExternalModel)
    }

    override suspend fun registerSchedule(schedule: Schedule, specificDate: LocalDate?) {
        scheduleLocalDataSource.insert(
            with(schedule) {
                ScheduleEntity(
                    id,
                    startTime,
                    endTime,
                    classPlace,
                    dayOfWeek,
                    specificDate,
                    courseId
                )
            }
        )
    }

    override suspend fun getScheduleDetailsById(scheduleId: Int): ScheduleDetails =
        scheduleLocalDataSource.getScheduleDetailsView(scheduleId).asExternalModel()
}