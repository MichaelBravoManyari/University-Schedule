package com.studentsapps.schedule.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studentsapps.data.repository.CourseRepository
import com.studentsapps.data.repository.ScheduleRepository
import com.studentsapps.model.Course
import com.studentsapps.model.Schedule
import com.studentsapps.schedule.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class RegisterScheduleViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {
    private var _uiState = MutableStateFlow(RegisterScheduleUiState())
    val uiState: StateFlow<RegisterScheduleUiState> = _uiState

    fun selectDay(day: DayOfWeek) {
        _uiState.update { currentState ->
            currentState.copy(day = day)
        }
    }

    fun selectStartHour(time: LocalTime) {
        _uiState.update { currentState ->
            currentState.copy(startTime = time, hourError = false)
        }
    }

    fun selectEndHour(time: LocalTime) {
        _uiState.update { currentState ->
            currentState.copy(endTime = time, hourError = false)
        }
    }

    fun selectCourse(courseId: Int) {
        viewModelScope.launch {
            val course = courseRepository.getCourse(courseId).first()
            _uiState.update { currentState ->
                currentState.copy(selectedCourse = course, noSelectCourse = false)
            }
        }
    }

    fun selectColorCourse(colorCourse: Int) {
        _uiState.update { currentState ->
            currentState.copy(colorCourse = colorCourse)
        }
    }

    fun existingCourseChecked(isChecked: Boolean) {
        _uiState.update { currentState ->
            if (isChecked) {
                currentState.copy(
                    existingCourses = true,
                    visibilityEditTextCourse = false,
                    visibilitySelectCourse = true,
                    visibilityColorSection = false
                )
            } else {
                currentState.copy(
                    existingCourses = false,
                    visibilityEditTextCourse = true,
                    visibilitySelectCourse = false,
                    visibilityColorSection = true
                )
            }
        }
    }

    fun setClassroom(classroom: String?) {
        _uiState.update { currentState ->
            currentState.copy(classroom = classroom)
        }
    }

    fun setCourseName(courseName: String?) {
        _uiState.update { currentState ->
            currentState.copy(courseName = courseName, noSelectCourse = false)
        }
    }

    fun setRecurrentOption(recurrenceOption: RecurrenceOption) {
        _uiState.update { currentState ->
            if (recurrenceOption == RecurrenceOption.EVERY_WEEK) currentState.copy(
                repetition = recurrenceOption, day = DayOfWeek.MONDAY, specificDate = null
            )
            else currentState.copy(
                repetition = recurrenceOption,
                day = LocalDate.now().dayOfWeek,
                specificDate = LocalDate.now()
            )
        }
    }

    fun registerSchedule() {
        if (startTimeIsLessThanEndTime()) {
            if (uiState.value.existingCourses) {
                registerScheduleWithExistingCourse()
            } else {
                registerScheduleWithNewCourse()
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    userMessage = R.string.hour_error, hourError = true
                )
            }
        }
    }

    fun displayScheduleData(scheduleId: Int) {
        viewModelScope.launch {
            val schedule = scheduleRepository.getScheduleDetailsById(scheduleId)
            val course = courseRepository.getCourse(schedule.courseId).first()
            _uiState.update { currentState ->
                with(schedule) {
                    currentState.copy(
                        scheduleId = scheduleId,
                        day = dayOfWeek,
                        startTime = startTime,
                        endTime = endTime,
                        selectedCourse = course,
                        repetition = if (specificDate != null) RecurrenceOption.SPECIFIC_DATE else RecurrenceOption.EVERY_WEEK,
                        specificDate = specificDate,
                        classroom = classPlace
                    )
                }
            }
        }
    }

    fun updateSchedule() {
        if (startTimeIsLessThanEndTime()) {
            if (uiState.value.existingCourses) {
                updateScheduleWithExistingCourse()
            } else {
                updateScheduleWithNewCourse()
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    userMessage = R.string.hour_error, hourError = true
                )
            }
        }
    }

    private fun registerScheduleWithExistingCourse() {
        if (uiState.value.selectedCourse != null) {
            val schedule = createSchedule(uiState.value.selectedCourse!!.id)
            viewModelScope.launch {
                scheduleRepository.registerSchedule(schedule, uiState.value.specificDate)
                _uiState.update { currentState ->
                    currentState.copy(isScheduleRecorded = true)
                }
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    noSelectCourse = true, userMessage = R.string.select_course
                )
            }
        }
    }

    private fun updateScheduleWithExistingCourse() {
        if (uiState.value.selectedCourse != null) {
            val schedule = createSchedule(uiState.value.selectedCourse!!.id)
            viewModelScope.launch {
                scheduleRepository.updateSchedule(schedule, uiState.value.specificDate)
                _uiState.update { currentState ->
                    currentState.copy(isScheduleRecorded = true)
                }
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    noSelectCourse = true, userMessage = R.string.select_course
                )
            }
        }
    }

    private fun updateScheduleWithNewCourse() {
        if (!uiState.value.courseName.isNullOrBlank()) {
            val course = createCourse()
            viewModelScope.launch {
                val courseId = courseRepository.registerCourse(course).toInt()
                val schedule = createSchedule(courseId)
                scheduleRepository.updateSchedule(schedule, uiState.value.specificDate)
                _uiState.update { currentState ->
                    currentState.copy(isScheduleRecorded = true)
                }
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    noSelectCourse = true, userMessage = R.string.enter_course_name
                )
            }
        }
    }

    private fun registerScheduleWithNewCourse() {
        if (!uiState.value.courseName.isNullOrBlank()) {
            val course = createCourse()
            viewModelScope.launch {
                val courseId = courseRepository.registerCourse(course).toInt()
                val schedule = createSchedule(courseId)
                scheduleRepository.registerSchedule(schedule, uiState.value.specificDate)
                _uiState.update { currentState ->
                    currentState.copy(isScheduleRecorded = true)
                }
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    noSelectCourse = true, userMessage = R.string.enter_course_name
                )
            }
        }
    }

    private fun createSchedule(courseId: Int): Schedule {
        return Schedule(
            uiState.value.scheduleId,
            uiState.value.startTime,
            uiState.value.endTime,
            uiState.value.classroom,
            uiState.value.day,
            courseId
        )
    }

    private fun createCourse(): Course {
        return Course(0, uiState.value.courseName!!, null, uiState.value.colorCourse)
    }

    fun setSpecificDate(date: LocalDate?) {
        _uiState.update { currentState ->
            currentState.copy(
                specificDate = date, day = date?.dayOfWeek ?: currentState.day
            )
        }
    }

    fun userMessageShown() {
        _uiState.update { currentState ->
            currentState.copy(userMessage = null)
        }
    }

    private fun startTimeIsLessThanEndTime(): Boolean =
        uiState.value.startTime < uiState.value.endTime
}

data class RegisterScheduleUiState(
    val scheduleId: Int = 0,
    val day: DayOfWeek = DayOfWeek.MONDAY,
    val startTime: LocalTime = LocalTime.of(9, 0),
    val endTime: LocalTime = LocalTime.of(10, 0),
    val existingCourses: Boolean = true,
    val visibilityEditTextCourse: Boolean = false,
    val visibilitySelectCourse: Boolean = true,
    val visibilityColorSection: Boolean = false,
    val selectedCourse: Course? = null,
    val colorCourse: Int = 0xffffff00.toInt(),
    val repetition: RecurrenceOption = RecurrenceOption.EVERY_WEEK,
    val classroom: String? = null,
    val courseName: String? = null,
    val noSelectCourse: Boolean = false,
    @StringRes val userMessage: Int? = null,
    val hourError: Boolean = false,
    val isScheduleRecorded: Boolean = false,
    val specificDate: LocalDate? = null,
)

enum class RecurrenceOption {
    EVERY_WEEK, SPECIFIC_DATE
}