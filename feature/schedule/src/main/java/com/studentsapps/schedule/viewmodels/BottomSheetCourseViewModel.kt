package com.studentsapps.schedule.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studentsapps.data.repository.CourseRepository
import com.studentsapps.model.Course
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BottomSheetCourseUiState(
    val coursesItems: List<Course> = listOf()
)

@HiltViewModel
class BottomSheetCourseViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {
    private var _uiState = MutableStateFlow(BottomSheetCourseUiState())
    val uiState: StateFlow<BottomSheetCourseUiState> = _uiState.asStateFlow()

    init {
        loadCourses()
    }

    private fun loadCourses() {
        viewModelScope.launch {
            val coursesList = courseRepository.getAllCourse()
            _uiState.value = BottomSheetCourseUiState(coursesList)
        }
    }
}