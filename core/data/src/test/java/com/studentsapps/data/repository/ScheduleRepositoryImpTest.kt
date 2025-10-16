package com.studentsapps.data.repository

import com.studentsapps.data.repository.fake.FakeScheduleRepository
import com.studentsapps.database.datasources.CourseLocalDataSource
import com.studentsapps.database.datasources.PendingOperationLocalDataSource
import com.studentsapps.database.datasources.ScheduleLocalDataSource
import com.studentsapps.database.model.ScheduleDetailsView
import com.studentsapps.database.model.asExternalModel
import com.studentsapps.database.test.data.testdoubles.TestCourseDao
import com.studentsapps.database.test.data.testdoubles.TestPendingOperationDao
import com.studentsapps.database.test.data.testdoubles.TestScheduleDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
    private lateinit var subject: ScheduleRepository
    private lateinit var scheduleLocalDataSource: ScheduleLocalDataSource
    private lateinit var pendingOperatiDataSource: PendingOperationLocalDataSource
    private lateinit var courseDataSource: CourseLocalDataSource

    @Before
    fun setup() {
        scheduleLocalDataSource = ScheduleLocalDataSource(TestScheduleDao(), testDispatcher)
        pendingOperatiDataSource =
            PendingOperationLocalDataSource(TestPendingOperationDao(), testDispatcher)
        courseDataSource = CourseLocalDataSource(TestCourseDao(), testDispatcher)

        subject = FakeScheduleRepository()
    }

    @Test
    fun getSchedulesForTimetableInGridMode_returnsScheduleDetails() =
        runTest(testDispatcher) {
            assertThat(
                subject.getSchedulesForTimetableInGridMode(
                    showSaturday = true,
                    showSunday = true,
                    startDate = LocalDate.of(2023, 11, 20),
                    endDate = LocalDate.of(2023, 11, 26),
                    userId = ""
                ).first(),
                `is`(
                    scheduleLocalDataSource
                        .getSchedulesForTimetableInGridMode(
                            showSaturday = true,
                            showSunday = true,
                            startDate = LocalDate.of(2023, 11, 20),
                            endDate = LocalDate.of(2023, 11, 26),
                            userId = ""
                        ).map { it.map(ScheduleDetailsView::asExternalModel) }.first(),
                ),
            )
        }

    @Test
    fun getSchedulesForTimetableInListMode_returnsScheduleDetails() =
        runTest(testDispatcher) {
            val date = LocalDate.of(2023, 11, 20)
            assertThat(
                subject.getSchedulesForTimetableInListMode(date, "").first(),
                `is`(
                    scheduleLocalDataSource
                        .getSchedulesForTimetableInListMode(
                            dayOfWeek = DayOfWeek.MONDAY,
                            date = date,
                            userId = ""
                        ).map{ it.map(ScheduleDetailsView::asExternalModel) }.first(),
                ),
            )
        }

    @Test
    fun getScheduleDetailsById_returnsScheduleDetails() =
        runTest(testDispatcher) {
            val scheduleId = 1
            assertThat(
                subject.getScheduleDetailsById(scheduleId.toString(), userId = ""),
                `is`(scheduleLocalDataSource.getScheduleDetailsView(scheduleId.toString()).asExternalModel()),
            )
        }
}
