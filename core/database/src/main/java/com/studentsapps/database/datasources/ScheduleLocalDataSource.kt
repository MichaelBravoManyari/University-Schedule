package com.studentsapps.database.datasources

import com.studentsapps.common.Dispatcher
import com.studentsapps.common.Dispatchers.IO
import com.studentsapps.database.dao.ScheduleDao
import com.studentsapps.database.model.ScheduleDetailsView
import com.studentsapps.database.model.ScheduleEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

class ScheduleLocalDataSource
    @Inject
    constructor(
        private val scheduleDao: ScheduleDao,
        @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    ) {
        fun getSchedulesForTimetableInGridMode(
            showSaturday: Boolean,
            showSunday: Boolean,
            startDate: LocalDate,
            endDate: LocalDate,
            userId: String,
        ): Flow<List<ScheduleDetailsView>> =
            scheduleDao.getSchedulesForTimetableInGridMode(
                showSaturday,
                showSunday,
                startDate,
                endDate,
                userId,
            )

        fun getSchedulesForTimetableInListMode(
            dayOfWeek: DayOfWeek,
            date: LocalDate,
            userId: String,
        ): Flow<List<ScheduleDetailsView>> =
            scheduleDao
                .getSchedulesForTimetableInListMode(dayOfWeek, date, userId)
                .map { list -> list.sortedBy { it.startTime } }

        suspend fun insert(schedule: ScheduleEntity): Long =
            withContext(ioDispatcher) {
                scheduleDao.insert(schedule)
            }

        suspend fun getScheduleDetailsView(scheduleId: String): ScheduleDetailsView =
            withContext(ioDispatcher) {
                scheduleDao.getScheduleDetailsById(scheduleId)
            }

        suspend fun updateSchedule(schedule: ScheduleEntity) =
            withContext(ioDispatcher) {
                scheduleDao.update(schedule)
            }

        suspend fun deleteSchedule(schedule: ScheduleEntity) =
            withContext(ioDispatcher) {
                scheduleDao.delete(schedule)
            }

        suspend fun getAllSchedules(userId: String): List<ScheduleDetailsView> =
            withContext(ioDispatcher) {
                scheduleDao.getAllSchedule(userId)
            }

        suspend fun getSchedulesByCourseId(courseId: String): List<ScheduleEntity> =
            withContext(ioDispatcher) {
                scheduleDao.getSchedulesByCourseId(courseId)
            }

        fun getScheduleById(scheduleId: String): Flow<ScheduleEntity> = scheduleDao.getScheduleById(scheduleId)

        fun getSchedulesByIds(schedulesIds: List<String>): Flow<List<ScheduleEntity>> = scheduleDao.getSchedulesByIds(schedulesIds)

        fun getAllScheduleEntity(userId: String): Flow<List<ScheduleEntity>> = scheduleDao.getAllScheduleEntity(userId)
    }
