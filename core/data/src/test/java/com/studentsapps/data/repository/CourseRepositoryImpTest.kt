package com.studentsapps.data.repository

import com.studentsapps.database.datasources.CourseLocalDataSource
import com.studentsapps.database.model.CourseEntity
import com.studentsapps.database.model.asExternalModel
import com.studentsapps.database.test.data.courseList
import com.studentsapps.database.test.data.testdoubles.TestCourseDao
import com.studentsapps.model.Course
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private lateinit var dataSource: CourseLocalDataSource

    @Before
    fun setup() {
        dataSource = CourseLocalDataSource(TestCourseDao(), testDispatcher)
        subject = CourseRepositoryImp(dataSource)
    }

    @Test
    fun getCourse_returnSchedule() = runTest(testDispatcher) {
        assertThat(
            subject.getCourse(1), `is`(dataSource.getCourse(1).asExternalModel())
        )
    }

    @Test
    fun registerCourse_returnCourseId() = runTest(testDispatcher) {
        val course = Course(1, "Math", null, 1234)
        assertThat(subject.registerCourse(course), `is`(1))
    }

    @Test
    fun getAllCourse_returnCourses() = runTest(testDispatcher) {
        val expectedCourseList = courseList.map(CourseEntity::asExternalModel)
        assertEquals(expectedCourseList, subject.getAllCourse())
    }
}