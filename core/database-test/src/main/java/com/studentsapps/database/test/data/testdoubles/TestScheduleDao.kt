package com.studentsapps.database.test.data.testdoubles

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
        val filteredByDayOfWeek = scheduleDetailsList.filter {
            it.specificDate == null && (showSaturday || it.dayOfWeek != DayOfWeek.SATURDAY) && (showSunday || it.dayOfWeek != DayOfWeek.SUNDAY)
        }

        val filteredBySpecificDate = scheduleDetailsList.filter {
            it.specificDate != null && (showSaturday || it.dayOfWeek != DayOfWeek.SATURDAY) && (showSunday || it.dayOfWeek != DayOfWeek.SUNDAY) && it.specificDate!! in startDate..endDate
        }

        return filteredByDayOfWeek + filteredBySpecificDate
    }

    override suspend fun getSchedulesForTimetableInListMode(
        dayOfWeek: DayOfWeek,
        specificDate: LocalDate
    ): List<ScheduleDetailsView> {
        return scheduleDetailsList.filter {
            (it.dayOfWeek == dayOfWeek && it.specificDate == null) ||
                    it.specificDate == specificDate
        }
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