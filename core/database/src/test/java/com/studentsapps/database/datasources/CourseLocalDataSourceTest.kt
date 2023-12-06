package com.studentsapps.database.datasources

import com.studentsapps.database.dao.CourseDao
import com.studentsapps.database.model.CourseEntity
import com.studentsapps.database.test.data.testdoubles.TestCourseDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

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
            subject.getCourse(1), `is`(courseDao.getCourseById(1))
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
}