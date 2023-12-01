package com.studentsapps.data.repository

import com.studentsapps.database.datasources.ScheduleLocalDataSource
import com.studentsapps.database.model.ScheduleDetailsView
import com.studentsapps.database.model.asExternalModel
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

@ExperimentalCoroutinesApi
class ScheduleRepositoryImpTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var subject: ScheduleRepositoryImp
    private lateinit var scheduleLocalDataSource: ScheduleLocalDataSource

    @Before
    fun setup() {
        scheduleLocalDataSource = ScheduleLocalDataSource(TestScheduleDao(), testDispatcher)
        subject = ScheduleRepositoryImp(scheduleLocalDataSource)
    }

    @Test
    fun getSchedulesForTimetableInGridMode_returnsScheduleDetails() = runTest(testDispatcher) {
        assertThat(
            subject.getSchedulesForTimetableInGridMode(
                showSaturday = true,
                showSunday = true,
                startDate = LocalDate.of(2023, 11, 20),
                endDate = LocalDate.of(2023, 11, 26)
            ), `is`(
                scheduleLocalDataSource.getSchedulesForTimetableInGridMode(
                    showSaturday = true,
                    showSunday = true,
                    startDate = LocalDate.of(2023, 11, 20),
                    endDate = LocalDate.of(2023, 11, 26)
                ).map(ScheduleDetailsView::asExternalModel)
            )
        )
    }

    @Test
    fun getSchedulesForTimetableInListMode_returnsScheduleDetails() = runTest(testDispatcher) {
        val date = LocalDate.of(2023, 11, 20)
        assertThat(
            subject.getSchedulesForTimetableInListMode(date),
            `is`(
                scheduleLocalDataSource.getSchedulesForTimetableInListMode(
                    dayOfWeek = DayOfWeek.MONDAY,
                    date = date
                ).map(ScheduleDetailsView::asExternalModel)
            )
        )
    }
}