package com.studentsapps.schedule.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.studentsapps.data.repository.ScheduleRepository
import com.studentsapps.data.repository.TimetableUserPreferencesRepository
import com.studentsapps.model.ScheduleDetails
import com.studentsapps.model.TimetableUserPreferences
import com.studentsapps.ui.timetable.TimetableUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val timetableUserPreferencesRepository: TimetableUserPreferencesRepository,
    private val scheduleRepository: ScheduleRepository,
    private val timetableUtils: TimetableUtils,
    auth: FirebaseAuth
) : ViewModel() {
    private val userId = auth.currentUser?.uid ?: ""

    private val _uiState: MutableStateFlow<ScheduleUiState> =
        MutableStateFlow(ScheduleUiState.Loading)

    val uiState: StateFlow<ScheduleUiState> = _uiState

    init {
        viewModelScope.launch {
            timetableUserPreferencesRepository.userData.collect { timetableUserPreferences ->
                _uiState.update { currentState ->
                    if (currentState is ScheduleUiState.Success) {
                        val scheduleDetailsList = if (timetableUserPreferences.showAsGrid) {
                            scheduleRepository.getSchedulesForTimetableInGridMode(
                                timetableUserPreferences.showSaturday,
                                timetableUserPreferences.showSunday,
                                getStartDate(
                                    timetableUserPreferences.isMondayFirstDayOfWeek,
                                    timetableUserPreferences.showSaturday,
                                    timetableUserPreferences.showSunday,
                                    LocalDate.now()
                                ),
                                getEndDate(
                                    timetableUserPreferences.isMondayFirstDayOfWeek,
                                    timetableUserPreferences.showSaturday,
                                    timetableUserPreferences.showSunday,
                                    LocalDate.now()
                                ),
                                userId
                            )
                        } else {
                            scheduleRepository.getSchedulesForTimetableInListMode(LocalDate.now(), userId)
                        }
                        currentState.copy(timetableUserPreferences = timetableUserPreferences, scheduleDetailsList)
                    } else {
                        val scheduleDetailsList = if (timetableUserPreferences.showAsGrid) {
                            scheduleRepository.getSchedulesForTimetableInGridMode(
                                timetableUserPreferences.showSaturday,
                                timetableUserPreferences.showSunday,
                                getStartDate(
                                    timetableUserPreferences.isMondayFirstDayOfWeek,
                                    timetableUserPreferences.showSaturday,
                                    timetableUserPreferences.showSunday,
                                    LocalDate.now()
                                ),
                                getEndDate(
                                    timetableUserPreferences.isMondayFirstDayOfWeek,
                                    timetableUserPreferences.showSaturday,
                                    timetableUserPreferences.showSunday,
                                    LocalDate.now()
                                ),
                                userId
                            )
                        } else {
                            scheduleRepository.getSchedulesForTimetableInListMode(
                                LocalDate.now(),
                                userId
                            )
                        }
                        ScheduleUiState.Success(timetableUserPreferences, scheduleDetailsList)
                    }
                }
            }
        }
    }

    fun setShowAsGrid() {
        viewModelScope.launch {
            timetableUserPreferencesRepository.updateShowAsGrid()
        }
    }

    fun updateScheduleDetailsListInGridMode(
        showSaturday: Boolean,
        showSunday: Boolean,
        startDate: LocalDate,
        endDate: LocalDate
    ) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                if (currentState is ScheduleUiState.Success) {
                    val scheduleDetails = scheduleRepository.getSchedulesForTimetableInGridMode(
                        showSaturday,
                        showSunday,
                        startDate,
                        endDate,
                        userId
                    )
                    currentState.copy(scheduleDetailsList = scheduleDetails)
                } else
                    ScheduleUiState.Loading
            }
        }
    }

    fun updateScheduleDetailsListInListMode(date: LocalDate) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                if (currentState is ScheduleUiState.Success) {
                    val scheduleDetails =
                        scheduleRepository.getSchedulesForTimetableInListMode(date, userId)
                    currentState.copy(scheduleDetailsList = scheduleDetails)
                } else
                    ScheduleUiState.Loading
            }
        }
    }

    fun cancelUserAlarms() {
        viewModelScope.launch {
            scheduleRepository.cancelUserAlarms(userId)
        }
    }

    fun getDaysOfMonthOfWeek(
        isMondayFirstDayOfWeek: Boolean,
        showSaturday: Boolean,
        showSunday: Boolean,
        date: LocalDate = LocalDate.now()
    ): List<LocalDate> {
        return timetableUtils.getDaysOfMonthOfWeek(
            isMondayFirstDayOfWeek,
            showSaturday,
            showSunday,
            date
        )
    }

    fun getDaysOfWeekOrder(
        isMondayFirstDayOfWeek: Boolean,
        showSaturday: Boolean,
        showSunday: Boolean
    ): List<Int> {
        return timetableUtils.getDaysOfWeekOrder(isMondayFirstDayOfWeek, showSaturday, showSunday)
    }

    fun goToNextWeek() {
        _uiState.update { currentState ->
            if (currentState is ScheduleUiState.Success) {
                currentState.copy(currentWeekDate = currentState.currentWeekDate.plusWeeks(1))
            } else currentState
        }
    }

    fun goToPreviousWeek() {
        _uiState.update { currentState ->
            if (currentState is ScheduleUiState.Success) {
                currentState.copy(currentWeekDate = currentState.currentWeekDate.minusWeeks(1))
            } else currentState
        }
    }

    private fun getStartDate(
        isMondayFirstDayOfWeek: Boolean,
        showSaturday: Boolean,
        showSunday: Boolean,
        date: LocalDate
    ): LocalDate {
        return getDaysOfMonthOfWeek(
            isMondayFirstDayOfWeek,
            showSaturday,
            showSunday,
            date
        ).first()
    }

    private fun getEndDate(
        isMondayFirstDayOfWeek: Boolean,
        showSaturday: Boolean,
        showSunday: Boolean,
        date: LocalDate
    ): LocalDate {
        return getDaysOfMonthOfWeek(
            isMondayFirstDayOfWeek,
            showSaturday,
            showSunday,
            date
        ).last()
    }
}

sealed interface ScheduleUiState {

    data object Loading : ScheduleUiState

    data class Success(
        val timetableUserPreferences: TimetableUserPreferences,
        val scheduleDetailsList: List<ScheduleDetails>,
        val currentWeekDate: LocalDate = LocalDate.now()
    ) : ScheduleUiState
}
