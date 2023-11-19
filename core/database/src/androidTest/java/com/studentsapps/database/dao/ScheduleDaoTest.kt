package com.studentsapps.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.studentsapps.database.UniversityScheduleDatabase
import com.studentsapps.database.model.CourseEntity
import com.studentsapps.database.model.ScheduleEntity
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.DayOfWeek
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class ScheduleDaoTest {

    private lateinit var scheduleDao: ScheduleDao
    private lateinit var courseDao: CourseDao
    private lateinit var db: UniversityScheduleDatabase

    @Before
    fun createDb() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, UniversityScheduleDatabase::class.java).build()
        scheduleDao = db.scheduleDao()
        courseDao = db.courseDao()
        courseDao.insert(CourseEntity(id = 1, name = "Math", nameProfessor = "", 123))
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insert_schedule_returnsScheduleId() = runTest {
        val schedule = testSchedule(id = 1)
        val scheduleId = scheduleDao.insert(schedule)
        val actualSchedule = scheduleDao.getScheduleById(scheduleId.toInt())
        assertThat(1L, `is`(scheduleId))
        assertThat(actualSchedule, `is`(schedule))
    }

    @Test
    fun update_schedule() = runTest {
        val schedule = testSchedule(id = 2)
        scheduleDao.insert(schedule)
        val updateSchedule = schedule.copy(classPlace = "408 Ed. A")
        scheduleDao.update(updateSchedule)
        val actualSchedule = scheduleDao.getScheduleById(2)
        assertThat(actualSchedule, `is`(updateSchedule))
    }

    @Test
    fun delete_schedule() = runTest {
        val schedule = testSchedule(id = 1)
        val scheduleId = scheduleDao.insert(schedule)
        assertThat(1L, `is`(scheduleId))
        scheduleDao.delete(schedule)
        val actualSchedule = scheduleDao.getScheduleById(scheduleId.toInt())
        assertThat(actualSchedule, `is`(nullValue()))
    }

    @Test
    fun get_schedule_by_id() = runTest {
        val expectedSchedule = testSchedule(id = 3)
        scheduleDao.insert(expectedSchedule)
        val actualSchedule = scheduleDao.getScheduleById(3)
        assertThat(actualSchedule, `is`(expectedSchedule))
    }

    private fun testSchedule(id: Int) =
        ScheduleEntity(
            id = id,
            startTime = LocalTime.of(14, 5),
            endTime = LocalTime.of(15, 5),
            classPlace = "",
            dayOfWeek = DayOfWeek.SATURDAY,
            courseId = 1
        )
}