package com.studentsapps.schedule.viewmodels

import com.studentsapps.data.repository.CourseRepository
import com.studentsapps.data.repository.fake.FakeCourseRepository
import com.studentsapps.testing.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class BottomSheetCourseViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var courseRepository: CourseRepository
    private lateinit var subject: BottomSheetCourseViewModel

    @Before
    fun setup() {
        courseRepository = FakeCourseRepository()
        subject = BottomSheetCourseViewModel(courseRepository)
    }

    @Test
    fun testInitialStateOfBottomSheetCourseUiStateWithLoadedCourses() = runTest {
        val expectedCourseList = courseRepository.getAllCourse().first()
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        assertEquals(BottomSheetCourseUiState.Success(expectedCourseList), subject.uiState.value)
        collectJob.cancel()
    }
}