package com.studentsapps.schedule.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import java.time.format.TextStyle
import java.util.Locale
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

    private val _currentMonth = MutableLiveData(getMonth())

    val currentMonth = _currentMonth as LiveData<String>

    init {
        viewModelScope.launch {
            timetableUserPreferencesRepository.userData.collect { prefs ->
                val initialDate = LocalDate.now()
                val initialScheduleList = if (prefs.showAsGrid) {
                    val startDate = getStartDate(prefs, initialDate)
                    val endDate = getEndDate(prefs, initialDate)
                    scheduleRepository.getSchedulesForTimetableInGridMode(
                        prefs.showSaturday, prefs.showSunday, startDate, endDate, userId
                    )
                } else {
                    scheduleRepository.getSchedulesForTimetableInListMode(initialDate, userId)
                }

                _uiState.value = ScheduleUiState.Success(
                    timetableUserPreferences = prefs,
                    scheduleByDate = mapOf(initialDate to initialScheduleList)
                )
            }
        }
    }

    fun setShowAsGrid() {
        viewModelScope.launch {
            timetableUserPreferencesRepository.updateShowAsGrid()
        }
    }

    fun setCurrentMonth(date: LocalDate) {
        _currentMonth.value = getMonth(date)
    }

    fun loadScheduleForDate(date: LocalDate) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                if (currentState is ScheduleUiState.Success &&
                    !currentState.scheduleByDate.containsKey(date)
                ) {
                    val prefs = currentState.timetableUserPreferences
                    val scheduleList = if (prefs.showAsGrid) {
                        val startDate = getStartDate(prefs, date)
                        val endDate = getEndDate(prefs, date)
                        scheduleRepository.getSchedulesForTimetableInGridMode(
                            prefs.showSaturday, prefs.showSunday, startDate, endDate, userId
                        )
                    } else {
                        scheduleRepository.getSchedulesForTimetableInListMode(date, userId)
                    }

                    currentState.copy(
                        scheduleByDate = currentState.scheduleByDate + (date to scheduleList)
                    )
                } else currentState
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

    fun selectNowDay(select: Boolean) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                if (currentState is ScheduleUiState.Success) {
                    currentState.copy(selectNowDay = select)
                } else currentState
            }
        }
    }

    private fun getStartDate(prefs: TimetableUserPreferences, date: LocalDate) =
        getDaysOfMonthOfWeek(
            prefs.isMondayFirstDayOfWeek, prefs.showSaturday, prefs.showSunday, date
        ).first()

    private fun getEndDate(prefs: TimetableUserPreferences, date: LocalDate) =
        getDaysOfMonthOfWeek(
            prefs.isMondayFirstDayOfWeek, prefs.showSaturday, prefs.showSunday, date
        ).last()

    private fun getMonth(date: LocalDate = LocalDate.now()): String {
        return date.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
            .replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
    }
}

sealed interface ScheduleUiState {

    data object Loading : ScheduleUiState

    data class Success(
        val timetableUserPreferences: TimetableUserPreferences,
        val scheduleByDate: Map<LocalDate, List<ScheduleDetails>>,
        val selectNowDay: Boolean = false
    ) : ScheduleUiState
}
