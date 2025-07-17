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
            timetableUserPreferencesRepository.userData.collect { prefs ->
                /*_uiState.update { currentState ->
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
                            scheduleRepository.getSchedulesForTimetableInListMode(
                                LocalDate.now(),
                                userId
                            )
                        }
                        currentState.copy(
                            timetableUserPreferences = timetableUserPreferences,
                            scheduleDetailsList
                        )
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
                }*/
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
                    scheduleByDate = mapOf(initialDate to initialScheduleList),
                    currentDate = initialDate
                )
            }
        }
    }

    fun setShowAsGrid() {
        viewModelScope.launch {
            timetableUserPreferencesRepository.updateShowAsGrid()
        }
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
                        scheduleByDate = currentState.scheduleByDate + (date to scheduleList),
                        currentDate = date
                    )
                } else currentState
            }
        }
    }

    /*fun updateScheduleDetailsListInGridMode(
        currentDate: LocalDate
    ) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                if (currentState is ScheduleUiState.Success) {
                    val startDate = getStartDate(
                        currentState.timetableUserPreferences.isMondayFirstDayOfWeek,
                        currentState.timetableUserPreferences.showSaturday,
                        currentState.timetableUserPreferences.showSunday,
                        currentDate
                    )

                    val endDate = getEndDate(
                        currentState.timetableUserPreferences.isMondayFirstDayOfWeek,
                        currentState.timetableUserPreferences.showSaturday,
                        currentState.timetableUserPreferences.showSunday,
                        currentDate
                    )
                    val scheduleDetails = scheduleRepository.getSchedulesForTimetableInGridMode(
                        currentState.timetableUserPreferences.showSaturday,
                        currentState.timetableUserPreferences.showSunday,
                        startDate,
                        endDate,
                        userId
                    )
                    currentState.copy(scheduleDetailsList = scheduleDetails)
                } else
                    ScheduleUiState.Loading
            }
        }
    }*/

    /*fun updateScheduleDetailsListInListMode(date: LocalDate) {
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
    }*/

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

    /*fun goToNextWeek() {
        _uiState.update { currentState ->
            if (currentState is ScheduleUiState.Success) {
                currentState.copy(currentDate = currentState.currentDate.plusWeeks(1))
            } else currentState
        }
    }

    fun goToPreviousWeek() {
        _uiState.update { currentState ->
            if (currentState is ScheduleUiState.Success) {
                currentState.copy(currentDate = currentState.currentWkDate.minusWeeks(1))
            } else currentState
        }
    }*/

    /*private fun getStartDate(
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
    }*/

    private fun getStartDate(prefs: TimetableUserPreferences, date: LocalDate) =
        getDaysOfMonthOfWeek(
            prefs.isMondayFirstDayOfWeek, prefs.showSaturday, prefs.showSunday, date
        ).first()

    private fun getEndDate(prefs: TimetableUserPreferences, date: LocalDate) =
        getDaysOfMonthOfWeek(
            prefs.isMondayFirstDayOfWeek, prefs.showSaturday, prefs.showSunday, date
        ).last()
}

sealed interface ScheduleUiState {

    data object Loading : ScheduleUiState

    data class Success(
        val timetableUserPreferences: TimetableUserPreferences,
        val scheduleByDate: Map<LocalDate, List<ScheduleDetails>>,
        val currentDate: LocalDate = LocalDate.now()
    ) : ScheduleUiState
}
