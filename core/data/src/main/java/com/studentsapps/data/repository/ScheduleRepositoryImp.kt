package com.studentsapps.data.repository

import com.studentsapps.database.datasources.ScheduleLocalDataSource
import com.studentsapps.database.model.ScheduleDetailsView
import com.studentsapps.database.model.asExternalModel
import com.studentsapps.model.ScheduleDetails
import java.time.LocalDate
import javax.inject.Inject

class ScheduleRepositoryImp @Inject constructor(
    private val scheduleLocalDataSource: ScheduleLocalDataSource
) : ScheduleRepository {
    override suspend fun getSchedulesForTimetableInGridMode(
        showSaturday: Boolean,
        showMonday: Boolean,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<ScheduleDetails> {
        return scheduleLocalDataSource.getSchedulesForTimetableInGridMode(
            showSaturday,
            showMonday,
            startDate,
            endDate
        ).map(ScheduleDetailsView::asExternalModel)
    }
}