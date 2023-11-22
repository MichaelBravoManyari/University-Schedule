package com.studentsapps.data.repository

import com.studentsapps.model.ScheduleDetails
import java.time.LocalDate

interface ScheduleRepository {

    suspend fun getSchedulesForTimetableInGridMode(
        showSaturday: Boolean,
        showMonday: Boolean,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<ScheduleDetails>
}