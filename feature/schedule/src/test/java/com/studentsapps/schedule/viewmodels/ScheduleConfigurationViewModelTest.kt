package com.studentsapps.schedule.viewmodels

import com.studentsapps.data.repository.TimetableUserPreferencesRepository
import com.studentsapps.data.repository.fake.FakeTimetableUserPreferencesRepository
import com.studentsapps.schedule.viewmodels.ScheduleConfigurationUiState.Loading
import com.studentsapps.schedule.viewmodels.ScheduleConfigurationUiState.Success
import com.studentsapps.testing.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class ScheduleConfigurationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var timetableUserPreferencesRepository: TimetableUserPreferencesRepository
    private lateinit var subject: ScheduleConfigurationViewModel

    @Before
    fun setup() {
        timetableUserPreferencesRepository = FakeTimetableUserPreferencesRepository()
        subject = ScheduleConfigurationViewModel(timetableUserPreferencesRepository)
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        assertEquals(Loading, subject.uiState.value)
    }

    @Test
    fun setIsMondayFirstDayOfWeek_trueAndFalse() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.setIsMondayFirstDayOfWeek()
        assertThat(
            subject.uiState.value, `is`(
                Success(
                    isMondayFirstDayOfWeek = false,
                    is12HoursFormat = true,
                    showSaturday = true,
                    showSunday = true
                )
            )
        )
        subject.setIsMondayFirstDayOfWeek()
        assertThat(
            subject.uiState.value, `is`(
                Success(
                    isMondayFirstDayOfWeek = true,
                    is12HoursFormat = true,
                    showSaturday = true,
                    showSunday = true
                )
            )
        )
        collectJob.cancel()
    }

    @Test
    fun setIs12HoursFormat_trueAndFalse() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.setIs12HoursFormat()
        assertThat(
            subject.uiState.value, `is`(
                Success(
                    isMondayFirstDayOfWeek = true,
                    is12HoursFormat = false,
                    showSaturday = true,
                    showSunday = true
                )
            )
        )
        subject.setIs12HoursFormat()
        assertThat(
            subject.uiState.value, `is`(
                Success(
                    isMondayFirstDayOfWeek = true,
                    is12HoursFormat = true,
                    showSaturday = true,
                    showSunday = true
                )
            )
        )
        collectJob.cancel()
    }

    @Test
    fun setShowSaturday_trueAndFalse() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.setShowSaturday()
        assertThat(
            subject.uiState.value, `is`(
                Success(
                    isMondayFirstDayOfWeek = true,
                    is12HoursFormat = true,
                    showSaturday = false,
                    showSunday = true
                )
            )
        )
        subject.setShowSaturday()
        assertThat(
            subject.uiState.value, `is`(
                Success(
                    isMondayFirstDayOfWeek = true,
                    is12HoursFormat = true,
                    showSaturday = true,
                    showSunday = true
                )
            )
        )
        collectJob.cancel()
    }

    @Test
    fun setShowSunday_trueAndFalse() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.setShowSunday()
        assertThat(
            subject.uiState.value, `is`(
                Success(
                    isMondayFirstDayOfWeek = true,
                    is12HoursFormat = true,
                    showSaturday = true,
                    showSunday = false
                )
            )
        )
        subject.setShowSunday()
        assertThat(
            subject.uiState.value, `is`(
                Success(
                    isMondayFirstDayOfWeek = true,
                    is12HoursFormat = true,
                    showSaturday = true,
                    showSunday = true
                )
            )
        )
        collectJob.cancel()
    }
}