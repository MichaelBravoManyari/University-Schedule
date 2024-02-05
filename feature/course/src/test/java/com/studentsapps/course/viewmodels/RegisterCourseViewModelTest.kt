package com.studentsapps.course.viewmodels

import android.graphics.Color
import com.studentsapps.data.repository.CourseRepository
import com.studentsapps.data.repository.fake.FakeCourseRepository
import com.studentsapps.testing.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
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

    @After
    fun clear() {
        (courseRepository as FakeCourseRepository).restoreDatabase()
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
            RegisterCourseUiState(
                courseId = courseId, name = "Math", nameProfessor = null, color = 1234
            ), subject.uiState.value
        )
        collectJob.cancel()
    }

    @Test
    fun registerCourse_courseSuccessfullyRegistered() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.setCourseName("TestCourse")
        subject.registerCourse()
        assertEquals(
            RegisterCourseUiState().copy(name = "TestCourse", isCourseRecorded = true),
            subject.uiState.value
        )
        collectJob.cancel()
    }

    @Test
    fun registerCourse_courseNameError() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.registerCourse()
        assertEquals(RegisterCourseUiState().copy(courseNameError = true), subject.uiState.value)
        collectJob.cancel()
    }

    @Test
    fun updateCourse_courseSuccessfullyUpdated() = runTest {
        val courseId = 1
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.displayCourseData(courseId)
        subject.setCourseName("Math 1")
        subject.setNameProfessor("Professor 1")
        subject.updateCourse()
        assertEquals(
            RegisterCourseUiState().copy(
                courseId = courseId,
                name = "Math 1",
                nameProfessor = "Professor 1",
                color = 1234,
                isCourseRecorded = true
            ), subject.uiState.value
        )
        collectJob.cancel()
    }

    @Test
    fun updateCourse_courseNameError() = runTest {
        val courseId = 1
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.displayCourseData(courseId)
        subject.setCourseName("")
        subject.updateCourse()
        assertEquals(
            RegisterCourseUiState().copy(
                courseId = courseId,
                name = "",
                nameProfessor = null,
                color = 1234,
                isCourseRecorded = false,
                courseNameError = true
            ), subject.uiState.value
        )
        collectJob.cancel()
    }

    @Test
    fun selectColorCourse_colorRed() = runTest {
        val courseColor = Color.RED
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.selectColorCourse(courseColor)
        assertEquals(RegisterCourseUiState().copy(color = courseColor), subject.uiState.value)
        collectJob.cancel()
    }

    @Test
    fun setCourseName_courseNameTest() = runTest {
        val courseName = "courseNameTest"
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.setCourseName(courseName)
        assertEquals(RegisterCourseUiState().copy(name = courseName), subject.uiState.value)
        collectJob.cancel()
    }

    @Test
    fun setNameProfessor_nameProfessorTest() = runTest {
        val nameProfessor = "nameProfessorTest"
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.setNameProfessor(nameProfessor)
        assertEquals(
            RegisterCourseUiState().copy(nameProfessor = nameProfessor), subject.uiState.value
        )
        collectJob.cancel()
    }
}