package com.studentsapps.course.viewmodels

import com.studentsapps.data.repository.CourseRepository
import com.studentsapps.data.repository.fake.FakeCourseRepository
import com.studentsapps.testing.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class RegisterCourseViewModelTest {

    @get: Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var subject: RegisterCourseViewModel
    private lateinit var courseRepository: CourseRepository

    @Before
    fun setup() {
        courseRepository = FakeCourseRepository()
        subject = RegisterCourseViewModel(courseRepository)
    }

    @Test
    fun stateIsInitiallyUiState() = runTest {
        assertEquals(
            RegisterCourseUiState(), subject.uiState.value
        )
    }

    @Test
    fun displayCourseData_courseId1() = runTest {
        val courseId = 1
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.displayCourseData(courseId)
        assertEquals(
            RegisterCourseUiState(name = "Math", nameProfessor = null, color = 1234),
            subject.uiState.value
        )
        collectJob.cancel()
    }
}