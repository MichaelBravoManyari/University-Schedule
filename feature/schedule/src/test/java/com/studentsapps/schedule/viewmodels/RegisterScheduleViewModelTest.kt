package com.studentsapps.schedule.viewmodels

import android.graphics.Color
import com.studentsapps.data.repository.CourseRepository
import com.studentsapps.data.repository.ScheduleRepository
import com.studentsapps.data.repository.fake.FakeCourseRepository
import com.studentsapps.data.repository.fake.FakeScheduleRepository
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
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class RegisterScheduleViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var scheduleRepository: ScheduleRepository
    private lateinit var courseRepository: CourseRepository
    private lateinit var subject: RegisterScheduleViewModel

    @Before
    fun setup() {
        scheduleRepository = FakeScheduleRepository()
        courseRepository = FakeCourseRepository()
        subject = RegisterScheduleViewModel(courseRepository, scheduleRepository)
    }

    @Test
    fun stateIsInitiallyRegisterUiState() = runTest {
        assertEquals(
            RegisterScheduleUiState(), subject.uiState.value
        )
    }

    @Test
    fun selectDay_dayOfWeek() = runTest {
        val dayOfWeek = DayOfWeek.WEDNESDAY
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.selectDay(dayOfWeek)
        assertEquals(
            RegisterScheduleUiState().copy(day = dayOfWeek), subject.uiState.value
        )
        collectJob.cancel()
    }

    @Test
    fun selectStartHour_9AM() = runTest {
        val startTime = LocalTime.of(9, 0)
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.selectStartHour(startTime)
        assertEquals(
            RegisterScheduleUiState().copy(startTime = startTime), subject.uiState.value
        )
        collectJob.cancel()
    }

    @Test
    fun selectEndHour_10AM() = runTest {
        val endTime = LocalTime.of(10, 0)
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.selectEndHour(endTime)
        assertEquals(
            RegisterScheduleUiState().copy(endTime = endTime), subject.uiState.value
        )
        collectJob.cancel()
    }

    @Test
    fun selectCourse_courseId1() = runTest {
        val expectedCourse = courseRepository.getCourse(1)
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.selectCourse(1)
        assertEquals(
            RegisterScheduleUiState().copy(
                selectedCourse = expectedCourse, noSelectCourse = false
            ), subject.uiState.value
        )
        collectJob.cancel()
    }

    @Test
    fun selectColorCourse_colorRed() = runTest {
        val expectedColor = Color.RED
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.selectColorCourse(expectedColor)
        assertEquals(
            RegisterScheduleUiState().copy(colorCourse = expectedColor), subject.uiState.value
        )
        collectJob.cancel()
    }

    @Test
    fun existingCourseChecked_TruAndFalse() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.existingCourseChecked(true)
        assertEquals(
            RegisterScheduleUiState().copy(
                existingCourses = true,
                visibilityEditTextCourse = false,
                visibilitySelectCourse = true,
                visibilityColorSection = false
            ), subject.uiState.value
        )
        subject.existingCourseChecked(false)
        assertEquals(
            RegisterScheduleUiState().copy(
                existingCourses = false,
                visibilityEditTextCourse = true,
                visibilitySelectCourse = false,
                visibilityColorSection = true
            ), subject.uiState.value
        )
        collectJob.cancel()
    }

    @Test
    fun setClassroom_notNullAndNull() = runTest {
        val classroom = "Ed. New"
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.setClassroom(classroom)
        assertEquals(
            RegisterScheduleUiState().copy(classroom = classroom), subject.uiState.value
        )
        subject.setClassroom(null)
        assertEquals(
            RegisterScheduleUiState().copy(classroom = null), subject.uiState.value
        )
        collectJob.cancel()
    }

    @Test
    fun setCourseName_notNullAndNull() = runTest {
        val courseName = "Math"
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.setCourseName(courseName)
        assertEquals(
            RegisterScheduleUiState().copy(courseName = courseName, noSelectCourse = false),
            subject.uiState.value
        )
        subject.setCourseName(null)
        assertEquals(
            RegisterScheduleUiState().copy(courseName = null, noSelectCourse = false),
            subject.uiState.value
        )
        collectJob.cancel()
    }

    @Test
    fun userMessageShown_null() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.userMessageShown()
        assertEquals(
            RegisterScheduleUiState().copy(userMessage = null), subject.uiState.value
        )
        collectJob.cancel()
    }

    @Test
    fun registerSchedule_correctTimes() = runTest {
        val startTime = LocalTime.of(9, 0)
        val endTime = LocalTime.of(10, 0)
        val expectedCourse = Course(1, "Math", null, 1234)
        val courseId = 1
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.run {
            selectStartHour(startTime)
            selectEndHour(endTime)
            existingCourseChecked(true)
            selectCourse(courseId)
        }
        subject.registerSchedule()
        assertEquals(
            RegisterScheduleUiState().copy(
                startTime = startTime,
                endTime = endTime,
                existingCourses = true,
                visibilityEditTextCourse = false,
                visibilitySelectCourse = true,
                visibilityColorSection = false,
                selectedCourse = expectedCourse,
                noSelectCourse = false,
                isScheduleRecorded = true
            ), subject.uiState.value
        )
        collectJob.cancel()
    }

    @Test
    fun registerSchedule_noSelectCourse() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.registerSchedule()
        assertEquals(
            RegisterScheduleUiState().copy(
                noSelectCourse = true, userMessage = subject.uiState.value.userMessage
            ), subject.uiState.value
        )
        collectJob.cancel()
    }

    @Test
    fun registerSchedule_noExistingCourses_withCourseName() = runTest {
        val expectedCourseName = "Math"
        val expectedColor = 1234
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.run {
            existingCourseChecked(false)
            setCourseName(expectedCourseName)
            selectColorCourse(expectedColor)
        }
        subject.registerSchedule()
        assertEquals(
            RegisterScheduleUiState().copy(
                existingCourses = false,
                visibilityEditTextCourse = true,
                visibilitySelectCourse = false,
                visibilityColorSection = true,
                courseName = expectedCourseName,
                colorCourse = expectedColor,
                isScheduleRecorded = true
            ), subject.uiState.value
        )
        collectJob.cancel()
    }

    @Test
    fun registerSchedule_noExistingCoursesAndCourseName() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }
        subject.existingCourseChecked(false)
        subject.registerSchedule()
        assertEquals(
            RegisterScheduleUiState().copy(
                existingCourses = false,
                visibilityEditTextCourse = true,
                visibilitySelectCourse = false,
                visibilityColorSection = true,
                noSelectCourse = true,
                userMessage = subject.uiState.value.userMessage,
            ), subject.uiState.value
        )
        collectJob.cancel()
    }
}