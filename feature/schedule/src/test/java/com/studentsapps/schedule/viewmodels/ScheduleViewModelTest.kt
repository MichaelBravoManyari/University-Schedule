package com.studentsapps.schedule.viewmodels

import com.studentsapps.data.repository.ScheduleRepository
import com.studentsapps.data.repository.fake.FakeScheduleRepository
import com.studentsapps.data.repository.fake.FakeTimetableUserPreferencesRepository
import com.studentsapps.model.ScheduleDetails
import com.studentsapps.model.TimetableUserPreferences
import com.studentsapps.testing.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class ScheduleViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var timetableUserPreferencesRepository: FakeTimetableUserPreferencesRepository
    private lateinit var scheduleRepository: ScheduleRepository
    private lateinit var subject: ScheduleViewModel

    @Before
    fun setup() {
        timetableUserPreferencesRepository = FakeTimetableUserPreferencesRepository()
        scheduleRepository = FakeScheduleRepository()
        subject = ScheduleViewModel(timetableUserPreferencesRepository, scheduleRepository)
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        timetableUserPreferencesRepository.init()
        assertEquals(
            ScheduleUiState.Success(
                TimetableUserPreferences(
                    showAsGrid = true,
                    is12HoursFormat = true,
                    showSaturday = true,
                    showSunday = true,
                    isMondayFirstDayOfWeek = true
                )
            ), subject.uiState.value
        )
    }

    @Test
    fun setShowAsGrid_trueAndFalse() = runTest {
        timetableUserPreferencesRepository.init()
        val collectJob1 = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }

        subject.setShowAsGrid()

        assertEquals(
            ScheduleUiState.Success(
                TimetableUserPreferences(
                    showAsGrid = false,
                    is12HoursFormat = true,
                    showSaturday = true,
                    showSunday = true,
                    isMondayFirstDayOfWeek = true
                )
            ), subject.uiState.value
        )

        collectJob1.cancel()
    }

    @Test
    fun updateScheduleListOnDateChangeDisplaySatSunInGridMode() = runTest {
        timetableUserPreferencesRepository.init()
        val collectJob1 = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.updateScheduleDetailsListInGridMode(
            showSaturday = true,
            showSunday = true,
            startDate = LocalDate.of(2023, 11, 20),
            endDate = LocalDate.of(2023, 11, 26)
        )

        assertEquals(
            ScheduleUiState.Success(
                timetableUserPreferences = TimetableUserPreferences(
                    showAsGrid = true,
                    showSunday = true,
                    is12HoursFormat = true,
                    showSaturday = true,
                    isMondayFirstDayOfWeek = true
                ),
                scheduleDetailsList = listOf(
                    createTestSchedule(scheduleId = 1, dayOfWeek = DayOfWeek.MONDAY),
                    createTestSchedule(
                        scheduleId = 2,
                        dayOfWeek = DayOfWeek.TUESDAY,
                        courseName = "Math1"
                    ),
                    createTestSchedule(
                        scheduleId = 3,
                        dayOfWeek = DayOfWeek.WEDNESDAY,
                        courseName = "Math2"
                    ),
                    createTestSchedule(
                        scheduleId = 4,
                        dayOfWeek = DayOfWeek.THURSDAY,
                        courseName = "Math3"
                    ),
                    createTestSchedule(
                        scheduleId = 5,
                        dayOfWeek = DayOfWeek.FRIDAY,
                        courseName = "Math4"
                    ),
                    createTestSchedule(
                        scheduleId = 6,
                        dayOfWeek = DayOfWeek.SATURDAY,
                        courseName = "Math5"
                    ),
                    createTestSchedule(
                        scheduleId = 7,
                        dayOfWeek = DayOfWeek.SUNDAY,
                        courseName = "Math6"
                    ),
                    createTestSchedule(
                        scheduleId = 8,
                        dayOfWeek = DayOfWeek.MONDAY,
                        specificDate = LocalDate.of(2023, 11, 20),
                        courseName = "Math7"
                    ),
                    createTestSchedule(
                        scheduleId = 9,
                        dayOfWeek = DayOfWeek.MONDAY,
                        specificDate = LocalDate.of(2023, 11, 20),
                        courseName = "Math8"
                    ),
                    createTestSchedule(
                        scheduleId = 10,
                        dayOfWeek = DayOfWeek.THURSDAY,
                        specificDate = LocalDate.of(2023, 11, 23),
                        courseName = "Math9"
                    ),
                )
            ), subject.uiState.value
        )

        collectJob1.cancel()
    }

    @Test
    fun testCheckScheduleListUpdatesForSpecificDateInListMode() = runTest {
        timetableUserPreferencesRepository.run {
            setShowAsGrid(false)
            init()
        }
        val date = LocalDate.of(2023, 11, 20)
        val collectJob1 = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.updateScheduleDetailsListInListMode(date)

        assertEquals(
            ScheduleUiState.Success(
                timetableUserPreferences = TimetableUserPreferences(
                    showAsGrid = false,
                    is12HoursFormat = true,
                    showSaturday = true,
                    showSunday = true,
                    isMondayFirstDayOfWeek = true
                ),
                scheduleDetailsList = listOf(
                    createTestSchedule(scheduleId = 1, dayOfWeek = DayOfWeek.MONDAY),
                    createTestSchedule(
                        scheduleId = 8,
                        dayOfWeek = DayOfWeek.MONDAY,
                        courseName = "Math7",
                        specificDate = date
                    ),
                    createTestSchedule(
                        scheduleId = 9,
                        dayOfWeek = DayOfWeek.MONDAY,
                        courseName = "Math8",
                        specificDate = date
                    ),
                )
            ),
            subject.uiState.value
        )

        collectJob1.cancel()
    }

    private fun createTestSchedule(
        scheduleId: Int,
        dayOfWeek: DayOfWeek,
        specificDate: LocalDate? = null,
        courseName: String = "Math"
    ) =
        ScheduleDetails(
            scheduleId = scheduleId,
            startTime = LocalTime.of(14, 5),
            endTime = LocalTime.of(15, 5),
            classPlace = null,
            dayOfWeek = dayOfWeek,
            specificDate = specificDate,
            courseId = 1,
            courseName = courseName,
            courseColor = 1234
        )
}