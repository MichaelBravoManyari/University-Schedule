package com.studentsapps.data.repository.fake

import com.studentsapps.data.repository.ScheduleRepository
import com.studentsapps.database.model.ScheduleDetailsView
import com.studentsapps.database.model.ScheduleEntity
import com.studentsapps.database.model.asExternalModel
import com.studentsapps.database.test.data.testdoubles.TestScheduleDao
import com.studentsapps.model.Schedule
import com.studentsapps.model.ScheduleDetails
import java.time.LocalDate
import javax.inject.Inject

class FakeScheduleRepository @Inject constructor() : ScheduleRepository {

    private val scheduleDao = TestScheduleDao()

    override suspend fun getSchedulesForTimetableInGridMode(
        showSaturday: Boolean, showSunday: Boolean, startDate: LocalDate, endDate: LocalDate
    ): List<ScheduleDetails> {
        return scheduleDao.getSchedulesForTimetableInGridMode(
            showSaturday, showSunday, startDate, endDate
        ).map(ScheduleDetailsView::asExternalModel)
    }

    override suspend fun getSchedulesForTimetableInListMode(date: LocalDate): List<ScheduleDetails> {
        return scheduleDao.getSchedulesForTimetableInListMode(date.dayOfWeek, date)
            .map(ScheduleDetailsView::asExternalModel)
    }

    override suspend fun registerSchedule(schedule: Schedule, specificDate: LocalDate?) {
        scheduleDao.insert(with(schedule) {
            ScheduleEntity(
                id, startTime, endTime, classPlace, dayOfWeek, specificDate, courseId
            )
        })
    }

    override suspend fun getScheduleDetailsById(scheduleId: Int): ScheduleDetails =
        scheduleDao.getScheduleDetailsById(scheduleId).asExternalModel()

    override suspend fun updateSchedule(schedule: Schedule, specificDate: LocalDate?) {
        TODO("Not yet implemented")
    }
}