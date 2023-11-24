package com.studentsapps.database.testdoubles

import com.studentsapps.database.dao.ScheduleDao
import com.studentsapps.database.model.ScheduleDetailsView
import com.studentsapps.database.model.ScheduleEntity
import com.studentsapps.database.test.data.scheduleDetailsList
import java.time.DayOfWeek
import java.time.LocalDate

class TestScheduleDao : ScheduleDao() {
    override suspend fun getScheduleById(scheduleId: Int): ScheduleEntity {
        TODO("Not yet implemented")
    }

    override suspend fun getSchedulesForTimetableInGridMode(
        showSaturday: Boolean,
        showSunday: Boolean,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<ScheduleDetailsView> {
        return scheduleDetailsList.toMutableList().apply {
            removeIf {
                if (!showSaturday)
                    it.dayOfWeek == DayOfWeek.SATURDAY
                else if (!showSunday)
                    it.dayOfWeek == DayOfWeek.SUNDAY
            }
        }
    }

    override suspend fun getSchedulesForTimetableInListMode(
        dayOfWeek: DayOfWeek,
        specificDate: LocalDate
    ): List<ScheduleDetailsView> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(obj: ScheduleEntity): Long {
        TODO("Not yet implemented")
    }

    override suspend fun update(obj: ScheduleEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(obj: ScheduleEntity) {
        TODO("Not yet implemented")
    }
}