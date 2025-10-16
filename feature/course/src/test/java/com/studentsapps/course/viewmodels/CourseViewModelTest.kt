package com.studentsapps.course.viewmodels

import com.studentsapps.common.UserManager
import com.studentsapps.data.repository.CourseRepository
import com.studentsapps.data.repository.fake.FakeCourseRepository
import com.studentsapps.testing.util.MainDispatcherRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class CourseViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var subject: CourseViewModel
    private lateinit var courseRepository: CourseRepository
    private lateinit var userManager: UserManager

    @Before
    fun setup() {
        courseRepository = FakeCourseRepository()
        userManager = mockk(relaxed = true)
        subject = CourseViewModel(courseRepository, userManager)
    }

    @Test
    fun stateIsInitiallyUiState() =
        runTest {
            assertEquals(
                CourseUiState.Loading,
                subject.uiState.value,
            )
        }
}
