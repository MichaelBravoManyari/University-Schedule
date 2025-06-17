package com.studentsapps.course.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studentsapps.common.UserManager
import com.studentsapps.data.repository.CourseRepository
import com.studentsapps.model.Course
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    userManager: UserManager,
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<CourseUiState> = userManager.userId
        .filterNotNull()
        .distinctUntilChanged()
        .flatMapLatest { userId ->
            courseRepository.getAllCourse(userId = userId)
        }
        .map(CourseUiState::Success)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CourseUiState.Loading
        )

    /*init {
        observeAndSyncCourses()
    }*/

    /*@OptIn(ExperimentalCoroutinesApi::class)
    private fun observeAndSyncCourses() {
        userManager.userId
            .filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { userId ->
                courseNetworkDataSource.observeCourses(userId).onEach { remoteCourses ->
                    val localCourses = courseRepository.getAllCourseEntity(userId).lastOrNull()

                    remoteCourses.forEach { remoteCourse ->
                        val local = localCourses?.find { it.id == remoteCourse.id }
                        if (local != null) {
                            if (remoteCourse.lastModified.isAfter(local.lastModified)) {
                                courseRepository.updateCourseEntity(remoteCourse.toCourseEntity())
                            }
                        } else {
                            courseRepository.registerCourseEntity(remoteCourse.toCourseEntity())
                        }
                    }

                    val remoteIds = remoteCourses.map { it.id }.toSet()
                    val toDelete = localCourses?.filterNot { it.id in remoteIds }
                    toDelete?.forEach {
                        courseRepository.deleteCourseEntity(it.id)
                    }
                }
            }
            .launchIn(viewModelScope)
    }*/
}

sealed interface CourseUiState {

    data object Loading : CourseUiState

    data class Success(
        val courseList: List<Course>
    ) : CourseUiState
}