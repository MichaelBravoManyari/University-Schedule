package com.studentsapps.schedule.viewmodels

import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studentsapps.data.repository.CourseRepository
import com.studentsapps.data.repository.ScheduleRepository
import com.studentsapps.model.Course
import com.studentsapps.model.Schedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime
import javax.inject.Inject

data class RegisterScheduleUiState(
    val day: DayOfWeek = DayOfWeek.MONDAY,
    val startTime: LocalTime = LocalTime.of(9, 0),
    val endTime: LocalTime = LocalTime.of(10, 0),
    val existingCourses: Boolean = true,
    val visibilityEditTextCourse: Boolean = false,
    val visibilitySelectCourse: Boolean = true,
    val visibilityColorSection: Boolean = false,
    val selectedCourse: Course? = null,
    val colorCourse: Int = 0xffffff00.toInt(),
    val classroom: String? = null,
    val courseName: String? = null,
    val noSelectCourse: Boolean = false,
    @StringRes val userMessage: Int? = null,
    val hourError: Boolean = false,
    val isScheduleRecorded: Boolean = false
)

@HiltViewModel
class RegisterScheduleViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {
    private var _uiState = MutableStateFlow(RegisterScheduleUiState())
    val uiState: StateFlow<RegisterScheduleUiState> = _uiState.asStateFlow()

    fun selectDay(day: DayOfWeek) {
        _uiState.update {
            it.copy(day = day)
        }
    }

    fun selectHour(time: LocalTime, startEndTime: Int) {
        _uiState.update {
            if (startEndTime == 1) {
                it.copy(startTime = time, hourError = false)
            } else {
                it.copy(endTime = time, hourError = false)
            }
        }
    }

    fun selectCourse(courseId: Int) {
        viewModelScope.launch {
            val course = courseRepository.getCourse(courseId)
            _uiState.update {
                it.copy(selectedCourse = course, noSelectCourse = false)
            }
        }
    }

    fun selectColorCourse(colorCourse: Int) {
        _uiState.update {
            it.copy(colorCourse = colorCourse)
        }
    }

    fun existingCourseChecked(isChecked: Boolean) {
        _uiState.update {
            if (isChecked) {
                it.copy(
                    existingCourses = true,
                    visibilityEditTextCourse = false,
                    visibilitySelectCourse = true,
                    visibilityColorSection = false
                )
            } else {
                it.copy(
                    existingCourses = false,
                    visibilityEditTextCourse = true,
                    visibilitySelectCourse = false,
                    visibilityColorSection = true
                )
            }
        }
    }

    fun setClassroom(classroom: String?) {
        _uiState.update {
            it.copy(classroom = classroom)
        }
    }

    fun setCourseName(courseName: String?) {
        _uiState.update {
            it.copy(courseName = courseName, noSelectCourse = false)
        }
    }

    fun registerSchedule() {
        if (startTimeIsLessThanEndTime()) {
            if (uiState.value.existingCourses) {
                if (uiState.value.selectedCourse != null) {
                    val schedule = Schedule(
                        0,
                        uiState.value.startTime,
                        uiState.value.endTime,
                        uiState.value.classroom,
                        uiState.value.day,
                        uiState.value.selectedCourse!!.id
                    )
                    viewModelScope.launch {
                        scheduleRepository.registerSchedule(schedule)
                        _uiState.update {
                            it.copy(isScheduleRecorded = true)
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            noSelectCourse = View.VISIBLE,
                            userMessage = R.string.select_course
                        )
                    }
                }
            } else {
                if (uiState.value.courseName != "") {
                    val course =
                        Course(0, uiState.value.courseName!!, null, uiState.value.colorCourse)
                    viewModelScope.launch {
                        val courseId = courseRepository.registerCourse(course).toInt()
                        val schedule = Schedule(
                            0,
                            uiState.value.startTime,
                            uiState.value.endTime,
                            uiState.value.classroom,
                            uiState.value.day,
                            courseId
                        )
                        scheduleRepository.registerSchedule(schedule)
                        _uiState.update {
                            it.copy(isScheduleRecorded = true)
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            noSelectCourse = View.VISIBLE,
                            userMessage = R.string.enter_course_name
                        )
                    }
                }
            }
        } else {
            _uiState.update {
                it.copy(
                    userMessage = R.string.hour_error,
                    hourError = View.VISIBLE
                )
            }
        }
    }

    fun userMessageShown() {
        _uiState.update {
            it.copy(userMessage = null)
        }
    }

    private fun startTimeIsLessThanEndTime(): Boolean {
        val startHour = uiState.value.startTime.toCalendar()
        val endHour = uiState.value.endTime.toCalendar()
        return startHour < endHour
    }
}