package com.studentsapps.database.datasources

import com.studentsapps.common.Dispatcher
import com.studentsapps.common.Dispatchers.IO
import com.studentsapps.database.dao.ScheduleDao
import com.studentsapps.database.model.ScheduleDetailsView
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class ScheduleLocalDataSource @Inject constructor(
    private val scheduleDao: ScheduleDao,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getSchedulesForTimetableInGridMode(
        showSaturday: Boolean,
        showMonday: Boolean,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<ScheduleDetailsView> =
        withContext(ioDispatcher) {
            scheduleDao.getSchedulesForTimetableInGridMode(
                showSaturday,
                showMonday,
                startDate,
                endDate
            )
        }
}