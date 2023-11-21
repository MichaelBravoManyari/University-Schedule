package com.studentsapps.database.datasources

import com.studentsapps.common.Dispatcher
import com.studentsapps.common.Dispatchers.IO
import com.studentsapps.database.dao.ScheduleDao
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class ScheduleLocalDataSource @Inject constructor(
    private val scheduleDao: ScheduleDao,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher
) {
}