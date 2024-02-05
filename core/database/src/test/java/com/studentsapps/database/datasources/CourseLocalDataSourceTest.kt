package com.studentsapps.database.datasources

import com.studentsapps.database.dao.CourseDao
import com.studentsapps.database.model.CourseEntity
import com.studentsapps.database.test.data.courseList
import com.studentsapps.database.test.data.testdoubles.TestCourseDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class CourseLocalDataSourceTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var subject: CourseLocalDataSource
    private lateinit var courseDao: CourseDao

    @Before
    fun setup() {
        courseDao = TestCourseDao()
        subject = CourseLocalDataSource(courseDao, testDispatcher)
    }

    @Test
    fun getCourse_courseEntity() = runTest(testDispatcher) {
        assertThat(
            subject.getCourse(1).first(), `is`(courseDao.getCourseById(1).first())
        )
    }

    @Test
    fun insert_scheduleEntityId() = runTest(testDispatcher) {
        val courseEntity = CourseEntity(
            id = 10,
            name = "Philosophy",
            nameProfessor = null,
            color = 1234,
        )
        assertThat(
            subject.insert(courseEntity), `is`(courseDao.insert(courseEntity))
        )
    }

    @Test
    fun getAllCourses_returnCourses() = runTest {
        val expectedCourseList = courseList
        val actualCourseList = subject.getAllCourse().first()
        assertEquals(actualCourseList, expectedCourseList)
    }

    @Test
    fun updateCourse_courseEntity() = runTest {
        val expectedCourse = CourseEntity(1, "Math 1", "Professor 1", 1234)
        subject.updateCourse(expectedCourse)
        assertThat(subject.getCourse(1).first(), `is`(expectedCourse))
    }
}