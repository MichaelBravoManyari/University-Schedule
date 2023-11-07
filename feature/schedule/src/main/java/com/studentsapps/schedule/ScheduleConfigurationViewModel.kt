package com.studentsapps.schedule

import androidx.lifecycle.ViewModel

class ScheduleConfigurationViewModel : ViewModel() {
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