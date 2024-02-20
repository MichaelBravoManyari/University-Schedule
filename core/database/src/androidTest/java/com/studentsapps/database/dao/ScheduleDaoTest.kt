package com.studentsapps.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.studentsapps.database.UniversityScheduleDatabase
import com.studentsapps.database.model.CourseEntity
import com.studentsapps.database.model.ScheduleDetailsView
import com.studentsapps.database.model.ScheduleEntity
import com.studentsapps.database.test.data.scheduleDetailsList
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
        insertCourses()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insert_schedule_returnsScheduleId() = runTest {
        val schedule = createTestScheduleEntity(id = 1)
        val scheduleId = scheduleDao.insert(schedule)
        val actualSchedule = scheduleDao.getScheduleById(scheduleId.toInt())
        assertThat(1L, `is`(scheduleId))
        assertThat(actualSchedule, `is`(schedule))
    }

    @Test
    fun update_schedule() = runTest {
        val schedule = createTestScheduleEntity(id = 2)
        scheduleDao.insert(schedule)
        val updateSchedule = schedule.copy(classPlace = "408 Ed. A")
        scheduleDao.update(updateSchedule)
        val actualSchedule = scheduleDao.getScheduleById(2)
        assertThat(actualSchedule, `is`(updateSchedule))
    }

    @Test
    fun delete_schedule() = runTest {
        val schedule = createTestScheduleEntity(id = 1)
        val scheduleId = scheduleDao.insert(schedule)
        assertThat(1L, `is`(scheduleId))
        scheduleDao.delete(schedule)
        val actualSchedule = scheduleDao.getScheduleById(scheduleId.toInt())
        assertThat(actualSchedule, `is`(nullValue()))
    }

    @Test
    fun get_schedule_by_id() = runTest {
        val expectedSchedule = createTestScheduleEntity(id = 3)
        scheduleDao.insert(expectedSchedule)
        val actualSchedule = scheduleDao.getScheduleById(3)
        assertThat(actualSchedule, `is`(expectedSchedule))
    }

    @Test
    fun getSchedulesForTimetableInGridMode_showSundayAndSaturday() = runTest {
        val expectedScheduleList = scheduleDetailsList.toMutableList().apply {
            removeLast()
        }
        insertSchedules()
        val actualScheduleList =
            scheduleDao.getSchedulesForTimetableInGridMode(
                showSaturday = true,
                showSunday = true,
                startDate = LocalDate.of(2023, 11, 20),
                endDate = LocalDate.of(2023, 11, 26)
            )
        assertThat(actualScheduleList, `is`(expectedScheduleList))
    }

    @Test
    fun getSchedulesForTimetableInGridMode_showSundayNotShowSaturday() = runTest {
        val expectedScheduleList = scheduleDetailsList.toMutableList().apply {
            removeIf { it.scheduleId == 6 || it.scheduleId == 11 }
        }
        insertSchedules()
        val actualScheduleList =
            scheduleDao.getSchedulesForTimetableInGridMode(
                showSaturday = false,
                showSunday = true,
                startDate = LocalDate.of(2023, 11, 20),
                endDate = LocalDate.of(2023, 11, 26)
            )
        assertThat(actualScheduleList, `is`(expectedScheduleList))
    }

    @Test
    fun getSchedulesForTimetableInGridMode_notShowSundayShowSaturday() = runTest {
        val expectedScheduleList = scheduleDetailsList.toMutableList().apply {
            removeIf { it.scheduleId == 7 || it.scheduleId == 11 }
        }
        insertSchedules()
        val actualScheduleList =
            scheduleDao.getSchedulesForTimetableInGridMode(
                showSaturday = true,
                showSunday = false,
                startDate = LocalDate.of(2023, 11, 20),
                endDate = LocalDate.of(2023, 11, 26)
            )
        assertThat(actualScheduleList, `is`(expectedScheduleList))
    }

    @Test
    fun getSchedulesForTimetableInGridMode_notShowSundayNotShowSaturday() = runTest {
        val expectedScheduleList = scheduleDetailsList.toMutableList().apply {
            removeIf { it.scheduleId == 7 || it.scheduleId == 11 || it.scheduleId == 6 }
        }
        insertSchedules()
        val actualScheduleList =
            scheduleDao.getSchedulesForTimetableInGridMode(
                showSaturday = false,
                showSunday = false,
                startDate = LocalDate.of(2023, 11, 20),
                endDate = LocalDate.of(2023, 11, 26)
            )
        assertThat(actualScheduleList, `is`(expectedScheduleList))
    }

    @Test
    fun getSchedulesForTimetableInListMode_dayOfWeekMonday() = runTest {
        val specificDate = LocalDate.of(2023, 11, 20)
        val expectedScheduleList = scheduleDetailsList.toMutableList().apply {
            removeIf { it.dayOfWeek != DayOfWeek.MONDAY || it.scheduleId == 11 }
        }
        insertSchedules()
        val actualScheduleList =
            scheduleDao.getSchedulesForTimetableInListMode(DayOfWeek.MONDAY, specificDate)
        assertThat(actualScheduleList, `is`(expectedScheduleList))
    }

    @Test
    fun getSchedulesForTimetableInListMode_dayOfWeekTuesday() = runTest {
        val specificDate = LocalDate.of(2023, 11, 21)
        val expectedScheduleList = scheduleDetailsList.toMutableList().apply {
            removeIf { (it.dayOfWeek != DayOfWeek.TUESDAY || it.specificDate != null) && it.specificDate != specificDate }
        }
        insertSchedules()
        val actualScheduleList =
            scheduleDao.getSchedulesForTimetableInListMode(DayOfWeek.TUESDAY, specificDate)
        assertThat(actualScheduleList, `is`(expectedScheduleList))
    }

    @Test
    fun get_scheduleDetailsView_by_scheduleId_returns_scheduleDetailsView() = runTest {
        val scheduleDetailsViewId = 1
        val expectedScheduleDetailsView = scheduleDetailsList.find { it.scheduleId == 1 }
        insertSchedules()
        val actualScheduleDetailsView = scheduleDao.getScheduleDetailsById(scheduleDetailsViewId)
        assertThat(actualScheduleDetailsView, `is`(expectedScheduleDetailsView))
    }

    private suspend fun insertSchedules() {
        scheduleDetailsList.forEach {
            scheduleDao.insert(it.toScheduleEntity())
        }
    }

    private suspend fun insertCourses() {
        for (i in 1..12) {
            courseDao.insert(
                CourseEntity(
                    id = i,
                    name = "Math${if (i == 1) "" else i-1}",
                    nameProfessor = "",
                    1234
                )
            )
        }
    }

    private fun createTestScheduleEntity(
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

    private fun ScheduleDetailsView.toScheduleEntity() =
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