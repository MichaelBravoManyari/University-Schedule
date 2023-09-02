package com.studentsapps.schedule

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test
import java.time.LocalDate

class TimetableDateUtilsTest {

    private val timetableDateUtils = TimetableDateUtils()

    @Test
    fun getDaysOfMonthOfWeek_isMondayFirstDaysOfWeekRandomDate_returnDaysOfMonthOfWeek() {
        val isMondayFirstDayOfWeek = true
        val date = LocalDate.of(2023, 7, 18)
        val expectedDays = listOf("17", "18", "19", "20", "21", "22", "23")

        val daysOfWeekOfMonth = timetableDateUtils.getDaysOfMonthOfWeek(isMondayFirstDayOfWeek, date)

        assertThat(daysOfWeekOfMonth, `is`(expectedDays))
    }

    @Test
    fun getDaysOfMonthOfWeek_isSundayFirstDaysOfWeekRandomDate_returnDaysOfMonthOfWeek() {
        val isMondayFirstDayOfWeek = false
        val date = LocalDate.of(2023, 10, 30)
        val expectedDays = listOf("29", "30", "31", "1", "2", "3", "4")

        val daysOfWeekOfMonth = timetableDateUtils.getDaysOfMonthOfWeek(isMondayFirstDayOfWeek, date)

        assertThat(daysOfWeekOfMonth, `is`(expectedDays))
    }

    @Test
    fun getDaysOfWeekOrder_isMondayFirstDaysOfWeek_returnsDaysOfWeek() {
        val isMondayFirstDayOfWeek = true
        val expectedDaysOfWeekOrder = listOf(
            R.string.monday_abbr,
            R.string.tuesday_abbr,
            R.string.wednesday_abbr,
            R.string.thursday_abbr,
            R.string.friday_abbr,
            R.string.saturday_abbr,
            R.string.sunday_abbr
        )

        val daysOfWeekOrder = timetableDateUtils.getDaysOfWeekOrder(isMondayFirstDayOfWeek)

        assertThat(daysOfWeekOrder, `is`(expectedDaysOfWeekOrder))
    }

    @Test
    fun getDaysOfWeekOrder_isSundayFirstDaysOfWeek_returnsDaysOfWeek() {
        val isMondayFirstDayOfWeek = false
        val expectedDaysOfWeekOrder = listOf(
            R.string.sunday_abbr,
            R.string.monday_abbr,
            R.string.tuesday_abbr,
            R.string.wednesday_abbr,
            R.string.thursday_abbr,
            R.string.friday_abbr,
            R.string.saturday_abbr
        )

        val daysOfWeekOrder = timetableDateUtils.getDaysOfWeekOrder(isMondayFirstDayOfWeek)

        assertThat(daysOfWeekOrder, `is`(expectedDaysOfWeekOrder))
    }
}