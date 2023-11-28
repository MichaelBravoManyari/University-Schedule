package com.studentsapps.data.repository.fake

import com.studentsapps.data.repository.ScheduleRepository
import com.studentsapps.database.model.ScheduleDetailsView
import com.studentsapps.database.model.asExternalModel
import com.studentsapps.database.test.data.testdoubles.TestScheduleDao
import com.studentsapps.model.ScheduleDetails
import java.time.LocalDate
import javax.inject.Inject

class FakeScheduleRepository @Inject constructor() : ScheduleRepository {

    private val scheduleDao = TestScheduleDao()

    override suspend fun getSchedulesForTimetableInGridMode(
        showSaturday: Boolean,
        showSunday: Boolean,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<ScheduleDetails> {
        return scheduleDao.getSchedulesForTimetableInGridMode(
            showSaturday,
            showSunday,
            startDate,
            endDate
        ).map(ScheduleDetailsView::asExternalModel)
    }
}