package com.studentsapps.database.datasources

import com.studentsapps.database.dao.ScheduleDao
import com.studentsapps.database.test.data.testdoubles.TestScheduleDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

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
    fun getSchedulesForTimetableInGridMode_returnScheduleDetailsView() = runTest(testDispatcher) {
        assertThat(
            subject.getSchedulesForTimetableInGridMode(
                showSaturday = true,
                showSunday = true,
                startDate = LocalDate.of(2023, 11, 20),
                endDate = LocalDate.of(2023, 11, 26)
            ),
            `is`(
                scheduleDao.getSchedulesForTimetableInGridMode(
                    showSaturday = true,
                    showSunday = true,
                    startDate = LocalDate.of(2023, 11, 20),
                    endDate = LocalDate.of(2023, 11, 26)
                )
            )
        )
    }

    @Test
    fun getSchedulesForTimetableInListMode_returnScheduleDetailsView() = runTest(testDispatcher) {
        val date = LocalDate.of(2023, 11, 20)
        assertThat(
            subject.getSchedulesForTimetableInListMode(
                date = date,
                dayOfWeek = date.dayOfWeek
            ),
            `is`(
                scheduleDao.getSchedulesForTimetableInListMode(
                    specificDate = date,
                    dayOfWeek = date.dayOfWeek
                )
            )
        )
    }
}