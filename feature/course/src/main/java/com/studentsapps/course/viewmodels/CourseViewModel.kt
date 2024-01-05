package com.studentsapps.course.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studentsapps.data.repository.CourseRepository
import com.studentsapps.model.Course
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
    courseRepository: CourseRepository
) : ViewModel() {

    val uiState: StateFlow<CourseUiState> =
        courseRepository.getAllCourse().map(CourseUiState::Success).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CourseUiState.Loading
        )
}

sealed interface CourseUiState {

    data object Loading : CourseUiState

    data class Success(
        val courseList: List<Course>
    ) : CourseUiState
}