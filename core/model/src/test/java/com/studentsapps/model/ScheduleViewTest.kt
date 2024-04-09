package com.studentsapps.model

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalTime

class ScheduleViewTest {

    @Test
    fun asScheduleView_scheduleDetails() {
        val scheduleDetails = ScheduleDetails(
            scheduleId = 1,
            startTime = LocalTime.of(20, 5),
            endTime = LocalTime.of(21, 5),
            classPlace = null,
            dayOfWeek = DayOfWeek.SUNDAY,
            specificDate = null,
            courseId = 1,
            courseName = "Math",
            courseColor = 1234,
        )
        val expectedScheduleView = createTestScheduleView(id = 1, dayOfWeek = DayOfWeek.SUNDAY)
        val actualScheduleView = scheduleDetails.asScheduleView()
        assertThat(actualScheduleView, `is`(expectedScheduleView))
    }

    @Test
    fun groupByDayOfWeek_returnsMapDayOfWeekScheduleView() {
        val scheduleList = listOf(
            createTestScheduleView(id = 1, dayOfWeek = DayOfWeek.MONDAY),
            createTestScheduleView(id = 2, dayOfWeek = DayOfWeek.TUESDAY),
            createTestScheduleView(id = 3, dayOfWeek = DayOfWeek.WEDNESDAY),
            createTestScheduleView(id = 4, dayOfWeek = DayOfWeek.THURSDAY),
            createTestScheduleView(id = 5, dayOfWeek = DayOfWeek.FRIDAY),
            createTestScheduleView(id = 6, dayOfWeek = DayOfWeek.SATURDAY),
            createTestScheduleView(id = 7, dayOfWeek = DayOfWeek.SUNDAY),
        )
        val expectedMap = mapOf(
            Pair(
                DayOfWeek.MONDAY,
                listOf(createTestScheduleView(id = 1, dayOfWeek = DayOfWeek.MONDAY))
            ),
            Pair(
                DayOfWeek.TUESDAY,
                listOf(createTestScheduleView(id = 2, dayOfWeek = DayOfWeek.TUESDAY))
            ),
            Pair(
                DayOfWeek.WEDNESDAY,
                listOf(createTestScheduleView(id = 3, dayOfWeek = DayOfWeek.WEDNESDAY))
            ),
            Pair(
                DayOfWeek.THURSDAY,
                listOf(createTestScheduleView(id = 4, dayOfWeek = DayOfWeek.THURSDAY))
            ),
            Pair(
                DayOfWeek.FRIDAY,
                listOf(createTestScheduleView(id = 5, dayOfWeek = DayOfWeek.FRIDAY))
            ),
            Pair(
                DayOfWeek.SATURDAY,
                listOf(createTestScheduleView(id = 6, dayOfWeek = DayOfWeek.SATURDAY))
            ),
            Pair(
                DayOfWeek.SUNDAY,
                listOf(createTestScheduleView(id = 7, dayOfWeek = DayOfWeek.SUNDAY))
            )
        )

        val actualMap = scheduleList.groupByDayOfWeek()
        assertThat(actualMap, `is`(expectedMap))
    }

    @Test
    fun getUniqueSchedules_returnSchedulesViewList() {
        val scheduleViewList = listOf(
            createTestScheduleView(id = 1, dayOfWeek = DayOfWeek.MONDAY),
            createTestScheduleView(
                id = 2,
                dayOfWeek = DayOfWeek.MONDAY,
                startTime = LocalTime.of(20, 30),
                endTime = LocalTime.of(21, 0)
            ),
            createTestScheduleView(
                id = 3,
                dayOfWeek = DayOfWeek.MONDAY,
                startTime = LocalTime.of(22, 0),
                endTime = LocalTime.of(23, 0)
            ),
            createTestScheduleView(
                id = 4,
                dayOfWeek = DayOfWeek.MONDAY,
                startTime = LocalTime.of(8, 0),
                endTime = LocalTime.of(9, 0)
            )
        )
        val expectedScheduleViewList = listOf(
            createTestScheduleView(
                id = 3,
                dayOfWeek = DayOfWeek.MONDAY,
                startTime = LocalTime.of(22, 0),
                endTime = LocalTime.of(23, 0)
            )
        )

        val actualScheduleViewList = scheduleViewList.getUniqueSchedules()

        assertThat(actualScheduleViewList, `is`(expectedScheduleViewList))
    }

    @Test
    fun getCrossSchedules_dayOfWeekMonday() {
        val scheduleViewList = listOf(
            createTestScheduleView(id = 1, dayOfWeek = DayOfWeek.MONDAY),
            createTestScheduleView(
                id = 2,
                dayOfWeek = DayOfWeek.MONDAY,
                startTime = LocalTime.of(20, 30),
                endTime = LocalTime.of(21, 0)
            ),
            createTestScheduleView(
                id = 3,
                dayOfWeek = DayOfWeek.MONDAY,
                startTime = LocalTime.of(20, 6),
                endTime = LocalTime.of(21, 4)
            ),
            createTestScheduleView(
                id = 4,
                dayOfWeek = DayOfWeek.MONDAY,
                startTime = LocalTime.of(22, 0),
                endTime = LocalTime.of(23, 0)
            ),
            createTestScheduleView(
                id = 5,
                dayOfWeek = DayOfWeek.MONDAY,
                startTime = LocalTime.of(22, 0),
                endTime = LocalTime.of(23, 0)
            )
        )
        val expectedScheduleViewList = listOf(
            listOf(
                createTestScheduleView(id = 1, dayOfWeek = DayOfWeek.MONDAY),
                createTestScheduleView(
                    id = 3,
                    dayOfWeek = DayOfWeek.MONDAY,
                    startTime = LocalTime.of(20, 6),
                    endTime = LocalTime.of(21, 4)
                ),
                createTestScheduleView(
                    id = 2,
                    dayOfWeek = DayOfWeek.MONDAY,
                    startTime = LocalTime.of(20, 30),
                    endTime = LocalTime.of(21, 0)
                ),
            ),
            listOf(
                createTestScheduleView(
                    id = 4,
                    dayOfWeek = DayOfWeek.MONDAY,
                    startTime = LocalTime.of(22, 0),
                    endTime = LocalTime.of(23, 0)
                ),
                createTestScheduleView(
                    id = 5,
                    dayOfWeek = DayOfWeek.MONDAY,
                    startTime = LocalTime.of(22, 0),
                    endTime = LocalTime.of(23, 0)
                )
            )
        )
        val actualScheduleViewList = scheduleViewList.getCrossSchedules()
        assertThat(actualScheduleViewList, `is`(expectedScheduleViewList))
    }

    @Test
    fun isCrossingSchedules_returnTrueAndFalse() {
        val scheduleView = createTestScheduleView(id = 1, dayOfWeek = DayOfWeek.SUNDAY)
        val scheduleViewToCompare1 = createTestScheduleView(id = 2, dayOfWeek = DayOfWeek.SUNDAY)
        val scheduleViewToCompare2 = createTestScheduleView(
            id = 3,
            dayOfWeek = DayOfWeek.SUNDAY,
            endTime = LocalTime.of(8, 0),
            startTime = LocalTime.of(9, 0)
        )
        val actualCrossingSchedule1 = scheduleView.isCrossingSchedules(scheduleViewToCompare1)
        val actualCrossingSchedule2 = scheduleView.isCrossingSchedules(scheduleViewToCompare2)
        assertThat(actualCrossingSchedule1, `is`(true))
        assertThat(actualCrossingSchedule2, `is`(false))
    }

    private fun createTestScheduleView(
        id: Int,
        dayOfWeek: DayOfWeek,
        startTime: LocalTime = LocalTime.of(20, 5),
        endTime: LocalTime = LocalTime.of(21, 5)
    ) =
        ScheduleView(
            id = id,
            startTime = startTime,
            endTime = endTime,
            classPlace = null,
            dayOfWeek = dayOfWeek,
            courseName = "Math",
            color = 1234,
        )
}