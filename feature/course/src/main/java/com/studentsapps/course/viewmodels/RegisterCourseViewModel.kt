package com.studentsapps.course.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.studentsapps.data.repository.CourseRepository
import com.studentsapps.model.Course
import com.studentsapps.sync.workers.SyncPendingOperationsWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterCourseViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    @ApplicationContext private val context: Context
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
                scheduleSyncWorker()
            }
        }
    }

    fun updateCourse() {
        viewModelScope.launch {
            performCourseOperation {
                courseRepository.updateCourse(it)
                scheduleSyncWorker()
            }
        }
    }

    fun deleteCourse() {
        viewModelScope.launch {
            try {
                courseRepository.deleteCourse(uiState.value.courseId)
                scheduleSyncWorker()

                _uiState.update { currentState ->
                    currentState.copy(
                        isCourseDeleted = true
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
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

    private fun scheduleSyncWorker() {
        val syncRequest = OneTimeWorkRequestBuilder<SyncPendingOperationsWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueue(syncRequest)
    }
}

data class RegisterCourseUiState(
    val courseId: Int = 0,
    val name: String = "",
    val nameProfessor: String? = "",
    val color: Int = 0xffffff00.toInt(),
    val isCourseRecorded: Boolean = false,
    val courseNameError: Boolean = false,
    val isCourseDeleted: Boolean = false,
)