package com.studentsapps.schedule.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studentsapps.data.repository.ScheduleRepository
import com.studentsapps.data.repository.TimetableUserPreferencesRepository
import com.studentsapps.model.ScheduleDetails
import com.studentsapps.model.TimetableUserPreferences
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
) : ViewModel() {

    private val _uiState: MutableStateFlow<ScheduleUiState> =
        MutableStateFlow(ScheduleUiState.Loading)

    val uiState: StateFlow<ScheduleUiState> = _uiState

    init {
        viewModelScope.launch {
            timetableUserPreferencesRepository.userData.collect { timetableUserPreferences ->
                _uiState.update { currentState ->
                    if (currentState is ScheduleUiState.Success)
                        currentState.copy(timetableUserPreferences = timetableUserPreferences)
                    else
                        ScheduleUiState.Success(timetableUserPreferences)
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
                        endDate
                    )
                    currentState.copy(scheduleDetailsList = scheduleDetails)
                } else
                    ScheduleUiState.Loading
            }
        }
    }
}

sealed interface ScheduleUiState {

    data object Loading : ScheduleUiState

    data class Success(
        val timetableUserPreferences: TimetableUserPreferences,
        val scheduleDetailsList: List<ScheduleDetails>? = null,
    ) : ScheduleUiState
}
