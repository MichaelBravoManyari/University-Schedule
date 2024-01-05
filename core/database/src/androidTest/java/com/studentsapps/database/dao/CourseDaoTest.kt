package com.studentsapps.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.studentsapps.database.UniversityScheduleDatabase
import com.studentsapps.database.model.CourseEntity
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class CourseDaoTest {

    private lateinit var courseDao: CourseDao
    private lateinit var db: UniversityScheduleDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, UniversityScheduleDatabase::class.java).build()
        courseDao = db.courseDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insert_course_returnCourseId() = runTest {
        val expectedCourse = testCourse(id = 1)
        val expectedCourseId = courseDao.insert(expectedCourse)
        val actualCourse = courseDao.getCourseById(expectedCourseId.toInt()).first()
        assertThat(1L, `is`(expectedCourseId))
        assertThat(actualCourse, `is`(expectedCourse))
    }

    @Test
    fun update_course() = runTest {
        val expectedCourse = testCourse(id = 2)
        courseDao.insert(expectedCourse)
        val updateCourse = expectedCourse.copy(nameProfessor = "Miguel")
        courseDao.update(updateCourse)
        val actualCourse = courseDao.getCourseById(2).first()
        assertThat(actualCourse, `is`(updateCourse))
    }

    @Test
    fun delete_schedule() = runTest {
        val expectedCourse = testCourse(id = 1)
        val expectedCourseId = courseDao.insert(expectedCourse)
        assertThat(1L, `is`(expectedCourseId))
        courseDao.delete(expectedCourse)
        val actualCourse = courseDao.getCourseById(expectedCourseId.toInt()).first()
        assertThat(actualCourse, `is`(nullValue()))
    }

    @Test
    fun get_schedule_by_id() = runTest {
        val expectedCourse = testCourse(id = 3)
        courseDao.insert(expectedCourse)
        val actualCourse = courseDao.getCourseById(3).first()
        assertThat(actualCourse, `is`(expectedCourse))
    }

    @Test
    fun getAllCourses_returnCourses() = runTest {
        val expectedCourses = listOf(
            testCourse(id = 1),
            testCourse(id = 2),
            testCourse(id = 3)
        )
        expectedCourses.forEach { expectedCourse ->
            courseDao.insert(expectedCourse)
        }
        val actualCourses = courseDao.getAll()
        assertTrue(actualCourses.first() == expectedCourses)
    }

    private fun testCourse(id: Int) =
        CourseEntity(
            id = id,
            name = "Math",
            nameProfessor = "",
            color = 1245
        )
}