package com.studentsapps.data.repository.fake

import com.studentsapps.data.repository.ScheduleRepository
import com.studentsapps.database.model.ScheduleDetailsView
import com.studentsapps.database.model.ScheduleEntity
import com.studentsapps.database.model.asExternalModel
import com.studentsapps.database.test.data.testdoubles.TestScheduleDao
import com.studentsapps.model.Schedule
import com.studentsapps.model.ScheduleDetails
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class FakeScheduleRepository @Inject constructor() : ScheduleRepository {

    private val scheduleDao = TestScheduleDao()

    override suspend fun getSchedulesForTimetableInGridMode(
        showSaturday: Boolean,
        showSunday: Boolean,
        startDate: LocalDate,
        endDate: LocalDate,
        userId: String
    ): List<ScheduleDetails> {
        return scheduleDao.getSchedulesForTimetableInGridMode(
            showSaturday, showSunday, startDate, endDate, userId
        ).map(ScheduleDetailsView::asExternalModel)
    }

    override suspend fun getSchedulesForTimetableInListMode(
        date: LocalDate,
        userId: String
    ): List<ScheduleDetails> {
        return scheduleDao.getSchedulesForTimetableInListMode(date.dayOfWeek, date, userId)
            .map(ScheduleDetailsView::asExternalModel)
    }

    override suspend fun registerSchedule(
        schedule: Schedule,
        specificDate: LocalDate?,
        courseName: String,
        courseColor: Int,
        userId: String
    ) {
        scheduleDao.insert(with(schedule) {
            ScheduleEntity(
                id,
                startTime,
                endTime,
                classPlace,
                dayOfWeek,
                specificDate,
                LocalDateTime.now(),
                courseId,
                userId
            )
        })
    }

    override suspend fun registerScheduleEntity(scheduleEntity: ScheduleEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun getScheduleDetailsById(
        scheduleId: String,
        userId: String
    ): ScheduleDetails =
        scheduleDao.getScheduleDetailsById(scheduleId).asExternalModel()

    override suspend fun updateSchedule(
        schedule: Schedule,
        specificDate: LocalDate?,
        courseName: String,
        courseColor: Int,
        userId: String
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun updateScheduleEntity(scheduleEntity: ScheduleEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSchedule(scheduleId: String, userId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllScheduleDetails(userId: String): List<ScheduleDetails> {
        TODO("Not yet implemented")
    }

    override fun getAllScheduleEntity(userId: String): Flow<List<ScheduleEntity>> {
        TODO("Not yet implemented")
    }
}