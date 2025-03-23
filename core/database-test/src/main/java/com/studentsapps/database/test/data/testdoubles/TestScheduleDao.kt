package com.studentsapps.database.test.data.testdoubles

import com.studentsapps.database.dao.ScheduleDao
import com.studentsapps.database.model.ScheduleDetailsView
import com.studentsapps.database.model.ScheduleEntity
import com.studentsapps.database.test.data.scheduleDetailsList
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.LocalDate

class TestScheduleDao : ScheduleDao() {
    override fun getScheduleById(scheduleId: String): Flow<ScheduleEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun getScheduleDetailsById(scheduleId: String): ScheduleDetailsView = scheduleDetailsList.find { it.scheduleId == scheduleId }!!

    override suspend fun getSchedulesForTimetableInGridMode(
        showSaturday: Boolean,
        showSunday: Boolean,
        startDate: LocalDate,
        endDate: LocalDate,
        userId: String
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
        specificDate: LocalDate,
        userId: String
    ): List<ScheduleDetailsView> {
        return scheduleDetailsList.filter {
            (it.dayOfWeek == dayOfWeek && it.specificDate == null) ||
                    it.specificDate == specificDate
        }
    }

    override suspend fun getAllSchedule(userId: String): List<ScheduleDetailsView> {
        TODO("Not yet implemented")
    }

    override fun getAllScheduleEntity(userId: String): Flow<List<ScheduleEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun getSchedulesByCourseId(courseId: String): List<ScheduleEntity> {
        TODO("Not yet implemented")
    }

    override fun getSchedulesByIds(schedulesIds: List<String>): Flow<List<ScheduleEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(obj: ScheduleEntity): Long {
        return obj.id.toLong()
    }

    override suspend fun update(obj: ScheduleEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(obj: ScheduleEntity) {
        TODO("Not yet implemented")
    }
}