package com.studentsapps.data.repository

import androidx.test.core.app.ApplicationProvider
import com.studentsapps.data.repository.fake.FakeScheduleRepository
import com.studentsapps.database.datasources.CourseLocalDataSource
import com.studentsapps.database.datasources.PendingOperationLocalDataSource
import com.studentsapps.database.datasources.ScheduleLocalDataSource
import com.studentsapps.database.model.CourseEntity
import com.studentsapps.database.model.asExternalModel
import com.studentsapps.database.test.data.courseList
import com.studentsapps.database.test.data.testdoubles.TestCourseDao
import com.studentsapps.database.test.data.testdoubles.TestPendingOperationDao
import com.studentsapps.database.test.data.testdoubles.TestScheduleDao
import com.studentsapps.model.Course
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class CourseRepositoryImpTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var subject: CourseRepositoryImp
    private lateinit var courseDataSource: CourseLocalDataSource
    private lateinit var scheduleDataSource: ScheduleLocalDataSource
    private lateinit var pendingOperatiDataSource: PendingOperationLocalDataSource
    private lateinit var scheduleRepository: ScheduleRepository

    @Before
    fun setup() {
        courseDataSource = CourseLocalDataSource(TestCourseDao(), testDispatcher)
        scheduleDataSource = ScheduleLocalDataSource(TestScheduleDao(), testDispatcher)
        pendingOperatiDataSource =
            PendingOperationLocalDataSource(TestPendingOperationDao(), testDispatcher)
        scheduleRepository = FakeScheduleRepository()

        subject = CourseRepositoryImp(
            courseDataSource, scheduleDataSource, pendingOperatiDataSource, scheduleRepository,
            ApplicationProvider.getApplicationContext()
        )
    }

    @Test
    fun getCourse_returnSchedule() =
        runTest(testDispatcher) {
            assertThat(
                subject.getCourse("1").first(),
                `is`(courseDataSource.getCourse("1").map(CourseEntity::asExternalModel).first()),
            )
        }

    @Test
    fun registerCourse_returnCourseId() =
        runTest(testDispatcher) {
            val course = Course("1", "Math", null, 1234)
            assertThat(subject.registerCourse(course, ""), `is`(1))
        }

    @Test
    fun getAllCourse_returnCourses() =
        runTest(testDispatcher) {
            val expectedCourseList = courseList.map(CourseEntity::asExternalModel)
            assertEquals(expectedCourseList, subject.getAllCourse(false, "").first())
        }

    @Test
    fun updateCourse_course() =
        runTest(testDispatcher) {
            val expectedCourse = Course("1", "Math 1", "Professor 1", 1234)
            subject.updateCourse(expectedCourse, "")
            assertEquals(expectedCourse, subject.getCourse("1").first())
        }
}
