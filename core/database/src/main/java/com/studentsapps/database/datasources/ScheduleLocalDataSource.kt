package com.studentsapps.database.datasources

import com.studentsapps.common.Dispatcher
import com.studentsapps.common.Dispatchers.IO
import com.studentsapps.database.dao.ScheduleDao
import com.studentsapps.database.model.ScheduleDetailsView
import com.studentsapps.database.model.ScheduleEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

class ScheduleLocalDataSource @Inject constructor(
    private val scheduleDao: ScheduleDao,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getSchedulesForTimetableInGridMode(
        showSaturday: Boolean,
        showSunday: Boolean,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<ScheduleDetailsView> =
        withContext(ioDispatcher) {
            scheduleDao.getSchedulesForTimetableInGridMode(
                showSaturday,
                showSunday,
                startDate,
                endDate
            )
        }

    suspend fun getSchedulesForTimetableInListMode(
        dayOfWeek: DayOfWeek,
        date: LocalDate
    ): List<ScheduleDetailsView> =
        withContext(ioDispatcher) {
            scheduleDao.getSchedulesForTimetableInListMode(dayOfWeek, date)
        }

    suspend fun insert(schedule: ScheduleEntity): Long =
        withContext(ioDispatcher) {
            scheduleDao.insert(schedule)
        }

    suspend fun getScheduleDetailsView(scheduleId: Int): ScheduleDetailsView =
        withContext(ioDispatcher) {
            scheduleDao.getScheduleDetailsById(scheduleId)
        }
}