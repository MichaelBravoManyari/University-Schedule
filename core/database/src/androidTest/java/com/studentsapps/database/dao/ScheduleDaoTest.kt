package com.studentsapps.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.studentsapps.database.UniversityScheduleDatabase
import com.studentsapps.database.model.CourseEntity
import com.studentsapps.database.model.ScheduleDetails
import com.studentsapps.database.model.ScheduleEntity
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
import java.time.DayOfWeek
import java.time.LocalDate
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

    @Test
    fun dsfd() = runTest {
        val expectedScheduleList = mutableListOf(
            testScheduleDetails(id = 1, dayOfWeek = DayOfWeek.MONDAY),
            testScheduleDetails(id = 2, dayOfWeek = DayOfWeek.TUESDAY),
            testScheduleDetails(id = 3, dayOfWeek = DayOfWeek.WEDNESDAY),
            testScheduleDetails(id = 4, dayOfWeek = DayOfWeek.THURSDAY),
            testScheduleDetails(id = 5, dayOfWeek = DayOfWeek.FRIDAY),
            testScheduleDetails(id = 6, dayOfWeek = DayOfWeek.SATURDAY),
            testScheduleDetails(id = 7, dayOfWeek = DayOfWeek.SUNDAY),
            testScheduleDetails(
                id = 8,
                dayOfWeek = DayOfWeek.MONDAY,
                specificDate = LocalDate.of(2023, 11, 20)
            ),
            testScheduleDetails(
                id = 9,
                dayOfWeek = DayOfWeek.MONDAY,
                specificDate = LocalDate.of(2023, 11, 20)
            ),
            testScheduleDetails(
                id = 10,
                dayOfWeek = DayOfWeek.THURSDAY,
                specificDate = LocalDate.of(2023, 11, 23)
            ),
        )
        expectedScheduleList.forEach {
            scheduleDao.insert(it.toScheduleEntity())
        }
        val actualScheduleList =
            scheduleDao.sdfds(true, true, LocalDate.of(2023, 11, 20), LocalDate.of(2023, 11, 26))
                .first()
        assertThat(actualScheduleList, `is`(expectedScheduleList))
    }

    private fun testSchedule(
        id: Int,
    ) =
        ScheduleEntity(
            id = id,
            startTime = LocalTime.of(14, 5),
            endTime = LocalTime.of(15, 5),
            classPlace = null,
            dayOfWeek = DayOfWeek.SATURDAY,
            courseId = 1,
            specificDate = null
        )

    private fun testScheduleDetails(
        id: Int,
        dayOfWeek: DayOfWeek,
        specificDate: LocalDate? = null
    ) =
        ScheduleDetails(
            scheduleId = id,
            startTime = LocalTime.of(14, 5),
            endTime = LocalTime.of(15, 5),
            classPlace = null,
            dayOfWeek = dayOfWeek,
            courseId = 1,
            specificDate = specificDate,
            courseName = "Math"
        )

    private fun ScheduleDetails.toScheduleEntity() =
        ScheduleEntity(
            id = scheduleId,
            startTime = startTime,
            endTime = endTime,
            classPlace = classPlace,
            dayOfWeek = dayOfWeek,
            specificDate = specificDate,
            courseId = courseId
        )
}