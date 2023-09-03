package com.studentsapps.schedule

import org.hamcrest.CoreMatchers.`is`
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Assert.assertThrows
import java.time.LocalDate

class TimetableUtilsTest {

    private val utils = TimetableUtils()

    @Test
    fun getDaysOfMonthOfWeek_isMondayFirstDaysOfWeekRandomDate_returnDaysOfMonthOfWeek() {
        val isMondayFirstDayOfWeek = true
        val date = LocalDate.of(2023, 7, 18)
        val expectedDays = listOf("17", "18", "19", "20", "21", "22", "23")
        val daysOfWeekOfMonth = utils.getDaysOfMonthOfWeek(isMondayFirstDayOfWeek, date)
        assertThat(daysOfWeekOfMonth, Matchers.`is`(expectedDays))
    }

    @Test
    fun getDaysOfMonthOfWeek_isSundayFirstDaysOfWeekRandomDate_returnDaysOfMonthOfWeek() {
        val isMondayFirstDayOfWeek = false
        val date = LocalDate.of(2023, 10, 30)
        val expectedDays = listOf("29", "30", "31", "1", "2", "3", "4")
        val daysOfWeekOfMonth = utils.getDaysOfMonthOfWeek(isMondayFirstDayOfWeek, date)
        assertThat(daysOfWeekOfMonth, Matchers.`is`(expectedDays))
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
        val daysOfWeekOrder = utils.getDaysOfWeekOrder(isMondayFirstDayOfWeek)
        assertThat(daysOfWeekOrder, Matchers.`is`(expectedDaysOfWeekOrder))
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
        val daysOfWeekOrder = utils.getDaysOfWeekOrder(isMondayFirstDayOfWeek)
        assertThat(daysOfWeekOrder, Matchers.`is`(expectedDaysOfWeekOrder))
    }

    @Test
    fun calculateRealRootViewWidth_500dp0pl0pr_500() {
        val expectedRealRootViewWidth = 500
        val realRootViewWidth = utils.calculateRealRootViewWidth(500, 0, 0)
        assertThat(realRootViewWidth, `is`(expectedRealRootViewWidth))
    }

    @Test
    fun calculateRealRootViewWidth_700dp50pl50pr_600() {
        val expectedRealRootViewWidth = 600
        val realRootViewWidth = utils.calculateRealRootViewWidth(700, 50, 50)
        assertThat(realRootViewWidth, `is`(expectedRealRootViewWidth))
    }

    @Test
    fun calculateRealRootViewWidth_0dp10pl0pr_exception() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            utils.calculateRealRootViewWidth(0, 10, 0)
        }
        val expectedMessage = "The root view cannot be 0 width."
        val actualMessage = exception.message
        assertThat(actualMessage, `is`(expectedMessage))
    }

    @Test
    fun calculateGridCellWidth_500dp100dp8cn1chn_57() {
        val expectedGridCellWidth = 57
        val realGridCellWidth = utils.calculateGridCellWidth(500, 100, 8, 1)
        assertThat(realGridCellWidth, `is`(expectedGridCellWidth))
    }

    @Test
    fun calculateGridCellWidth_700dp150dp8cn1chn_57() {
        val expectedGridCellWidth = 78
        val realGridCellWidth = utils.calculateGridCellWidth(700, 150, 8, 1)
        assertThat(realGridCellWidth, `is`(expectedGridCellWidth))
    }

    @Test
    fun calculateTimetableBitmapHeight_8C50dp_400() {
        val expectedTimetableBitmapHeight = 400
        val realTimetableBitmapHeight = utils.calculateTimetableBitmapHeight(8, 50)
        assertThat(realTimetableBitmapHeight, `is`(expectedTimetableBitmapHeight))
    }

    @Test
    fun calculateTimetableBitmapHeight_8C80dp_640() {
        val expectedTimetableBitmapHeight = 640
        val realTimetableBitmapHeight = utils.calculateTimetableBitmapHeight(8, 80)
        assertThat(realTimetableBitmapHeight, `is`(expectedTimetableBitmapHeight))
    }
}
