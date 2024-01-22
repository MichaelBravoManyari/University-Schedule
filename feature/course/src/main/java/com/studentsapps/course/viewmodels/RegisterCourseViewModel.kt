package com.studentsapps.course.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studentsapps.data.repository.CourseRepository
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
                    name = course.name,
                    nameProfessor = course.nameProfessor,
                    color = course.color
                )
            }
        }
    }
}

data class RegisterCourseUiState(
    val name: String = "",
    val nameProfessor: String? = "",
    val color: Int = 0
)