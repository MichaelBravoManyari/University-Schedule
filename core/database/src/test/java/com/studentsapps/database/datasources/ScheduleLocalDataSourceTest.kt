package com.studentsapps.database.datasources

import com.studentsapps.database.dao.ScheduleDao
import com.studentsapps.database.testdoubles.TestScheduleDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ScheduleLocalDataSourceTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var subject: ScheduleLocalDataSource
    private lateinit var scheduleDao: ScheduleDao

    @Before
    fun setup() {
        scheduleDao = TestScheduleDao()
        subject = ScheduleLocalDataSource(scheduleDao, testDispatcher)
    }

    @Test
    fun getSchedulesForTimetableInGridMode() = runTest(testDispatcher) {

    }
}