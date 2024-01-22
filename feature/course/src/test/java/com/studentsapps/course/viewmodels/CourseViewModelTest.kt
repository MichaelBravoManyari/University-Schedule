package com.studentsapps.course.viewmodels

import com.studentsapps.data.repository.CourseRepository
import com.studentsapps.data.repository.fake.FakeCourseRepository
import com.studentsapps.model.Course
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
class CourseViewModelTest {

    @get: Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var subject: CourseViewModel
    private lateinit var courseRepository: CourseRepository

    @Before
    fun setup() {
        courseRepository = FakeCourseRepository()
        subject = CourseViewModel(courseRepository)
    }

    @Test
    fun stateIsInitiallyUiState() = runTest {
        assertEquals(
            CourseUiState.Loading, subject.uiState.value
        )
    }

    @Test
    fun testSendCourseListAfterInitialLoadingState() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        assertEquals(
            CourseUiState.Success(
                listOf(
                    Course(1, "Math", null, 1234),
                    Course(2, "History", null, 1234),
                    Course(3, "Sciences", null, 1234),
                    Course(4, "Statistics", null, 1234)
                )
            ), subject.uiState.value
        )
        collectJob.cancel()
    }
}