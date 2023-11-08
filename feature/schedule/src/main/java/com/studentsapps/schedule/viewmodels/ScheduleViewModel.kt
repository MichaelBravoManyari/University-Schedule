package com.studentsapps.schedule.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studentsapps.data.repository.TimetableUserPreferencesRepository
import com.studentsapps.model.TimetableUserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val timetableUserPreferencesRepository: TimetableUserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<ScheduleUiState> =
        timetableUserPreferencesRepository.userData
            .map(ScheduleUiState::Success)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ScheduleUiState.Loading
            )

    fun setShowAsGrid() {
        viewModelScope.launch {
            timetableUserPreferencesRepository.updateShowAsGrid()
        }
    }
}

sealed interface ScheduleUiState {

    data object Loading : ScheduleUiState

    data class Success(
        val timetableUserPreferences: TimetableUserPreferences
    ) : ScheduleUiState
}
