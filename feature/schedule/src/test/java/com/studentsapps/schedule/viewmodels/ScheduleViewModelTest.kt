package com.studentsapps.schedule.viewmodels

import com.studentsapps.data.repository.fake.FakeTimetableUserPreferencesRepository
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
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class ScheduleViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val timetableUserPreferencesRepository = FakeTimetableUserPreferencesRepository()
    private lateinit var subject: ScheduleViewModel

    @Before
    fun setup() {
        subject = ScheduleViewModel(timetableUserPreferencesRepository)
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        assertEquals(ScheduleUiState.Loading, subject.uiState.value)
    }

    @Test
    fun setShowAsGrid_trueAndFalse() = runTest {
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
}