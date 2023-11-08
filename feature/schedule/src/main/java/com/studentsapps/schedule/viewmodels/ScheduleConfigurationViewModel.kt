package com.studentsapps.schedule.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studentsapps.data.repository.TimetableUserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleConfigurationViewModel @Inject constructor(
    private val timetableUserPreferencesRepository: TimetableUserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<ScheduleConfigurationUiState> =
        timetableUserPreferencesRepository.userData.map { preferences ->
            with(preferences) {
                ScheduleConfigurationUiState.Success(
                    isMondayFirstDayOfWeek,
                    is12HoursFormat,
                    showSaturday,
                    showSunday
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ScheduleConfigurationUiState.Loading
        )

    fun setIsMondayFirstDayOfWeek() {
        viewModelScope.launch {
            timetableUserPreferencesRepository.updateIsMondayFirstDayOfWeek()
        }
    }

    fun setIs12HoursFormat() {
        viewModelScope.launch {
            timetableUserPreferencesRepository.updateIs12HoursFormat()
        }
    }

    fun setShowSaturday() {
        viewModelScope.launch {
            timetableUserPreferencesRepository.updateShowSaturday()
        }
    }

    fun setShowSunday() {
        viewModelScope.launch {
            timetableUserPreferencesRepository.updateShowSunday()
        }
    }
}

sealed interface ScheduleConfigurationUiState {

    data object Loading : ScheduleConfigurationUiState

    data class Success(
        val isMondayFirstDayOfWeek: Boolean,
        val is12HoursFormat: Boolean,
        val showSaturday: Boolean,
        val showSunday: Boolean,
    ) : ScheduleConfigurationUiState
}