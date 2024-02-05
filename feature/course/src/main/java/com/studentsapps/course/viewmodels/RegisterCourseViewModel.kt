package com.studentsapps.course.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studentsapps.data.repository.CourseRepository
import com.studentsapps.model.Course
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterCourseViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<RegisterCourseUiState> =
        MutableStateFlow(RegisterCourseUiState())

    val uiState: StateFlow<RegisterCourseUiState> = _uiState

    fun displayCourseData(courseId: Int) {
        viewModelScope.launch {
            val course = courseRepository.getCourse(courseId).first()
            _uiState.update {
                RegisterCourseUiState(
                    courseId = course.id,
                    name = course.name,
                    nameProfessor = course.nameProfessor,
                    color = course.color
                )
            }
        }
    }

    fun registerCourse() {
        viewModelScope.launch {
            performCourseOperation {
                courseRepository.registerCourse(it)
            }
        }
    }

    fun updateCourse() {
        viewModelScope.launch {
            performCourseOperation {
                courseRepository.updateCourse(it)
            }
        }
    }

    private suspend fun performCourseOperation(courseOperation: suspend (Course) -> Unit) {
        if (validateCourseFields()) {
            val course = Course(
                uiState.value.courseId,
                uiState.value.name,
                uiState.value.nameProfessor,
                uiState.value.color
            )
            courseOperation(course)
            _uiState.update { currentState ->
                currentState.copy(isCourseRecorded = true)
            }
        }
    }

    private fun validateCourseFields(): Boolean {
        val hasNameError = uiState.value.name.isEmpty()

        _uiState.update { currentState ->
            currentState.copy(courseNameError = hasNameError)
        }

        return !hasNameError
    }

    fun selectColorCourse(colorCourse: Int) {
        _uiState.update { currentState ->
            currentState.copy(color = colorCourse)
        }
    }

    fun setCourseName(courseName: String) {
        _uiState.update { currentState ->
            currentState.copy(name = courseName)
        }
    }

    fun setNameProfessor(nameProfessor: String?) {
        _uiState.update { currentState ->
            currentState.copy(nameProfessor = nameProfessor)
        }
    }
}

data class RegisterCourseUiState(
    val courseId: Int = 0,
    val name: String = "",
    val nameProfessor: String? = "",
    val color: Int = 0xffffff00.toInt(),
    val isCourseRecorded: Boolean = false,
    val courseNameError: Boolean = false,
)