package com.studentsapps.schedule.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.studentsapps.data.repository.ScheduleRepository
import com.studentsapps.model.ScheduleDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomSheetScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    auth: FirebaseAuth
) : ViewModel() {
    private val userId = auth.currentUser?.uid ?: ""

    private val _uiState: MutableStateFlow<BottomSheetScheduleUiState> =
        MutableStateFlow(BottomSheetScheduleUiState())

    val uiState: StateFlow<BottomSheetScheduleUiState> = _uiState

    fun setScheduleDetails(scheduleId: String) {
        viewModelScope.launch {
            _uiState.update {
                val scheduleDetails = scheduleRepository.getScheduleDetailsById(scheduleId, userId)
                BottomSheetScheduleUiState(scheduleDetails)
            }
        }
    }

    fun deleteSchedule(scheduleId: String) {
        viewModelScope.launch {
            scheduleRepository.deleteSchedule(scheduleId, userId)
            _uiState.update { currentState ->
                currentState.copy(
                    isScheduleDeleted = true
                )
            }
        }
    }
}

data class BottomSheetScheduleUiState(
    val scheduleDetails: ScheduleDetails? = null,
    val isScheduleDeleted: Boolean = false
)