package com.studentsapps.database.datasources

import com.studentsapps.database.dao.ScheduleDao
import com.studentsapps.database.model.ScheduleEntity
import com.studentsapps.database.test.data.testdoubles.TestScheduleDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

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
            ), `is`(
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
                date = date, dayOfWeek = date.dayOfWeek
            ), `is`(
                scheduleDao.getSchedulesForTimetableInListMode(
                    specificDate = date, dayOfWeek = date.dayOfWeek
                )
            )
        )
    }

    @Test
    fun insert_scheduleEntity() = runTest(testDispatcher) {
        val scheduleEntity = ScheduleEntity(
            id = 10, LocalTime.of(12, 0), LocalTime.of(13, 0), null, DayOfWeek.MONDAY, null, 1
        )
        assertThat(
            subject.insert(scheduleEntity), `is`(scheduleDao.insert(scheduleEntity))
        )
    }
}