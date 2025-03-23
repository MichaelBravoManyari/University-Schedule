package com.studentsapps.schedule.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.studentsapps.data.repository.CourseRepository
import com.studentsapps.model.Course
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BottomSheetCourseViewModel @Inject constructor(
    courseRepository: CourseRepository,
    auth: FirebaseAuth
) : ViewModel() {
    private val userId = auth.currentUser?.uid ?: ""

    val uiState: StateFlow<BottomSheetCourseUiState> =
        courseRepository.getAllCourse(userId = userId).map(BottomSheetCourseUiState::Success).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = BottomSheetCourseUiState.Loading
        )
}

sealed interface BottomSheetCourseUiState {

    data object Loading : BottomSheetCourseUiState

    data class Success(
        val courseList: List<Course>
    ) : BottomSheetCourseUiState
}