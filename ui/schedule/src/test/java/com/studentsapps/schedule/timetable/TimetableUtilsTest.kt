package com.studentsapps.schedule.timetable

import com.studentsapps.schedule.R
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertThrows
import java.time.LocalDate

class TimetableUtilsTest {

    private val utils = TimetableUtils()

    @Test
    fun getDaysOfMonthOfWeek_startingMondayShowSaturdayShowSundayRandomDate_returnDaysOfMonthOfWeek() {
        val isMondayFirstDayOfWeek = true
        val showSaturday = true
        val showSunday = true
        val date = LocalDate.of(2023, 7, 18)
        val expectedDays = listOf("17", "18", "19", "20", "21", "22", "23")
        val daysOfWeekOfMonth =
            utils.getDaysOfMonthOfWeek(isMondayFirstDayOfWeek, showSaturday, showSunday, date)
        assertThat(daysOfWeekOfMonth, `is`(expectedDays))
    }

    @Test
    fun getDaysOfMonthOfWeek_startingMondayShowSaturdayNoShowSundayRandomDate_returnDaysOfMonthOfWeek() {
        val isMondayFirstDayOfWeek = true
        val showSaturday = true
        val showSunday = false
        val date = LocalDate.of(2023, 7, 18)
        val expectedDays = listOf("17", "18", "19", "20", "21", "22")
        val daysOfWeekOfMonth =
            utils.getDaysOfMonthOfWeek(isMondayFirstDayOfWeek, showSaturday, showSunday, date)
        assertThat(daysOfWeekOfMonth, `is`(expectedDays))
    }

    @Test
    fun getDaysOfMonthOfWeek_startingMondayNoShowSaturdayShowSundayRandomDate_returnDaysOfMonthOfWeek() {
        val isMondayFirstDayOfWeek = true
        val showSaturday = false
        val showSunday = true
        val date = LocalDate.of(2023, 7, 18)
        val expectedDays = listOf("17", "18", "19", "20", "21", "23")
        val daysOfWeekOfMonth =
            utils.getDaysOfMonthOfWeek(isMondayFirstDayOfWeek, showSaturday, showSunday, date)
        assertThat(daysOfWeekOfMonth, `is`(expectedDays))
    }

    @Test
    fun getDaysOfMonthOfWeek_startingMondayNoShowSaturdayNoShowSundayRandomDate_returnDaysOfMonthOfWeek() {
        val isMondayFirstDayOfWeek = true
        val showSaturday = false
        val showSunday = false
        val date = LocalDate.of(2023, 7, 18)
        val expectedDays = listOf("17", "18", "19", "20", "21")
        val daysOfWeekOfMonth =
            utils.getDaysOfMonthOfWeek(isMondayFirstDayOfWeek, showSaturday, showSunday, date)
        assertThat(daysOfWeekOfMonth, `is`(expectedDays))
    }

    @Test
    fun getDaysOfMonthOfWeek_startingSundayShowSaturdayShowSundayRandomDate_returnDaysOfMonthOfWeek() {
        val isMondayFirstDayOfWeek = false
        val showSaturday = true
        val showSunday = true
        val date = LocalDate.of(2023, 10, 30)
        val expectedDays = listOf("29", "30", "31", "1", "2", "3", "4")
        val daysOfWeekOfMonth =
            utils.getDaysOfMonthOfWeek(isMondayFirstDayOfWeek, showSaturday, showSunday, date)
        assertThat(daysOfWeekOfMonth, `is`(expectedDays))
    }

    @Test
    fun getDaysOfMonthOfWeek_startingSundayShowSaturdayNotShowSundayRandomDate_returnDaysOfMonthOfWeek() {
        val isMondayFirstDayOfWeek = false
        val showSaturday = true
        val showSunday = false
        val date = LocalDate.of(2023, 10, 30)
        val expectedDays = listOf("30", "31", "1", "2", "3", "4")
        val daysOfWeekOfMonth =
            utils.getDaysOfMonthOfWeek(isMondayFirstDayOfWeek, showSaturday, showSunday, date)
        assertThat(daysOfWeekOfMonth, `is`(expectedDays))
    }

    @Test
    fun getDaysOfMonthOfWeek_startingSundayNoShowSaturdayShowSundayRandomDate_returnDaysOfMonthOfWeek() {
        val isMondayFirstDayOfWeek = false
        val showSaturday = false
        val showSunday = true
        val date = LocalDate.of(2023, 10, 30)
        val expectedDays = listOf("29", "30", "31", "1", "2", "3")
        val daysOfWeekOfMonth =
            utils.getDaysOfMonthOfWeek(isMondayFirstDayOfWeek, showSaturday, showSunday, date)
        assertThat(daysOfWeekOfMonth, `is`(expectedDays))
    }

    @Test
    fun getDaysOfMonthOfWeek_startingSundayNoShowSaturdayNoShowSundayRandomDate_returnDaysOfMonthOfWeek() {
        val isMondayFirstDayOfWeek = false
        val showSaturday = false
        val showSunday = false
        val date = LocalDate.of(2023, 10, 30)
        val expectedDays = listOf("30", "31", "1", "2", "3")
        val daysOfWeekOfMonth =
            utils.getDaysOfMonthOfWeek(isMondayFirstDayOfWeek, showSaturday, showSunday, date)
        assertThat(daysOfWeekOfMonth, `is`(expectedDays))
    }

    @Test
    fun getDaysOfWeekOrder_startingMondayShowSaturdayShowSunday_returnsDaysOfWeek() {
        val isMondayFirstDayOfWeek = true
        val showSaturday = true
        val showSunday = true
        val expectedDaysOfWeekOrder = listOf(
            R.string.monday_abbr,
            R.string.tuesday_abbr,
            R.string.wednesday_abbr,
            R.string.thursday_abbr,
            R.string.friday_abbr,
            R.string.saturday_abbr,
            R.string.sunday_abbr
        )
        val daysOfWeekOrder =
            utils.getDaysOfWeekOrder(isMondayFirstDayOfWeek, showSaturday, showSunday)
        assertThat(daysOfWeekOrder, `is`(expectedDaysOfWeekOrder))
    }

    @Test
    fun getDaysOfWeekOrder_startingMondayShowSaturdayNoShowSunday_returnsDaysOfWeek() {
        val isMondayFirstDayOfWeek = true
        val showSaturday = true
        val showSunday = false
        val expectedDaysOfWeekOrder = listOf(
            R.string.monday_abbr,
            R.string.tuesday_abbr,
            R.string.wednesday_abbr,
            R.string.thursday_abbr,
            R.string.friday_abbr,
            R.string.saturday_abbr
        )
        val daysOfWeekOrder =
            utils.getDaysOfWeekOrder(isMondayFirstDayOfWeek, showSaturday, showSunday)
        assertThat(daysOfWeekOrder, `is`(expectedDaysOfWeekOrder))
    }

    @Test
    fun getDaysOfWeekOrder_startingMondayNotShowSaturdayShowSunday_returnsDaysOfWeek() {
        val isMondayFirstDayOfWeek = true
        val showSaturday = false
        val showSunday = true
        val expectedDaysOfWeekOrder = listOf(
            R.string.monday_abbr,
            R.string.tuesday_abbr,
            R.string.wednesday_abbr,
            R.string.thursday_abbr,
            R.string.friday_abbr,
            R.string.sunday_abbr
        )
        val daysOfWeekOrder =
            utils.getDaysOfWeekOrder(isMondayFirstDayOfWeek, showSaturday, showSunday)
        assertThat(daysOfWeekOrder, `is`(expectedDaysOfWeekOrder))
    }

    @Test
    fun getDaysOfWeekOrder_startingMondayNotShowSaturdayNoShowSunday_returnsDaysOfWeek() {
        val isMondayFirstDayOfWeek = true
        val showSaturday = false
        val showSunday = false
        val expectedDaysOfWeekOrder = listOf(
            R.string.monday_abbr,
            R.string.tuesday_abbr,
            R.string.wednesday_abbr,
            R.string.thursday_abbr,
            R.string.friday_abbr
        )
        val daysOfWeekOrder =
            utils.getDaysOfWeekOrder(isMondayFirstDayOfWeek, showSaturday, showSunday)
        assertThat(daysOfWeekOrder, `is`(expectedDaysOfWeekOrder))
    }

    @Test
    fun getDaysOfWeekOrder_startingSundayShowSaturdayShowSunday_returnsDaysOfWeek() {
        val isMondayFirstDayOfWeek = false
        val showSaturday = true
        val showSunday = true
        val expectedDaysOfWeekOrder = listOf(
            R.string.sunday_abbr,
            R.string.monday_abbr,
            R.string.tuesday_abbr,
            R.string.wednesday_abbr,
            R.string.thursday_abbr,
            R.string.friday_abbr,
            R.string.saturday_abbr
        )
        val daysOfWeekOrder =
            utils.getDaysOfWeekOrder(isMondayFirstDayOfWeek, showSaturday, showSunday)
        assertThat(daysOfWeekOrder, `is`(expectedDaysOfWeekOrder))
    }

    @Test
    fun getDaysOfWeekOrder_startingSundayShowSaturdayNoShowSunday_returnsDaysOfWeek() {
        val isMondayFirstDayOfWeek = false
        val showSaturday = true
        val showSunday = false
        val expectedDaysOfWeekOrder = listOf(
            R.string.monday_abbr,
            R.string.tuesday_abbr,
            R.string.wednesday_abbr,
            R.string.thursday_abbr,
            R.string.friday_abbr,
            R.string.saturday_abbr
        )
        val daysOfWeekOrder =
            utils.getDaysOfWeekOrder(isMondayFirstDayOfWeek, showSaturday, showSunday)
        assertThat(daysOfWeekOrder, `is`(expectedDaysOfWeekOrder))
    }

    @Test
    fun getDaysOfWeekOrder_startingSundayNotShowSaturdayShowSunday_returnsDaysOfWeek() {
        val isMondayFirstDayOfWeek = false
        val showSaturday = false
        val showSunday = true
        val expectedDaysOfWeekOrder = listOf(
            R.string.sunday_abbr,
            R.string.monday_abbr,
            R.string.tuesday_abbr,
            R.string.wednesday_abbr,
            R.string.thursday_abbr,
            R.string.friday_abbr
        )
        val daysOfWeekOrder =
            utils.getDaysOfWeekOrder(isMondayFirstDayOfWeek, showSaturday, showSunday)
        assertThat(daysOfWeekOrder, `is`(expectedDaysOfWeekOrder))
    }

    @Test
    fun getDaysOfWeekOrder_startingSundayNoShowSaturdayNoShowSunday_returnsDaysOfWeek() {
        val isMondayFirstDayOfWeek = false
        val showSaturday = false
        val showSunday = false
        val expectedDaysOfWeekOrder = listOf(
            R.string.monday_abbr,
            R.string.tuesday_abbr,
            R.string.wednesday_abbr,
            R.string.thursday_abbr,
            R.string.friday_abbr
        )
        val daysOfWeekOrder =
            utils.getDaysOfWeekOrder(isMondayFirstDayOfWeek, showSaturday, showSunday)
        assertThat(daysOfWeekOrder, `is`(expectedDaysOfWeekOrder))
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

    @Test
    fun getVerticalLinesCoordinates_numLines6HourCellW20GridCellWidth50lineHeight200_coordinates() {
        val expectedVerticalLinesCoordinates = floatArrayOf(
            70f, 0f, 70f, 200f,
            120f, 0f, 120f, 200f,
            170f, 0f, 170f, 200f,
            220f, 0f, 220f, 200f,
            270f, 0f, 270f, 200f,
            320f, 0f, 320f, 200f
        )
        val realVerticalLinesCoordinates =
            utils.getVerticalLinesCoordinates(6, 20, 50, 200f)
        assertThat(realVerticalLinesCoordinates, `is`(expectedVerticalLinesCoordinates))
    }

    @Test
    fun getHorizontalHourLinesCoordinates_numLines6HourCellW30GridCellH50LineLength300_coordinates() {
        val expectedHorizontalHourLinesCoordinates = floatArrayOf(
            30f, 50f, 300f, 50f,
            30f, 100f, 300f, 100f,
            30f, 150f, 300f, 150f,
            30f, 200f, 300f, 200f,
            30f, 250f, 300f, 250f,
            30f, 300f, 300f, 300f,
        )
        val realHorizontalHourLinesCoordinates =
            utils.getHorizontalHourLinesCoordinates(6, 30, 50, 300f)
        assertThat(realHorizontalHourLinesCoordinates, `is`(expectedHorizontalHourLinesCoordinates))
    }

    @Test
    fun getHalfHourHorizontalLinesCoordinates_numLines6HourCellW25GridCellH60LineLength300() {
        val expectedHalfHourHorizontalLinesCoordinates = floatArrayOf(
            25f, 30f, 300f, 30f,
            25f, 90f, 300f, 90f,
            25f, 150f, 300f, 150f,
            25f, 210f, 300f, 210f,
            25f, 270f, 300f, 270f,
            25f, 330f, 300f, 330f,
        )
        val realHalfHourHorizontalLinesCoordinates =
            utils.getHalfHourHorizontalLinesCoordinates(6, 25, 60, 300f)
        assertThat(
            realHalfHourHorizontalLinesCoordinates,
            `is`(expectedHalfHourHorizontalLinesCoordinates)
        )
    }

    @Test
    fun verifyGetCurrentMonthDayString() {
        val expectedDay = LocalDate.now().dayOfMonth.toString()
        val realDay = utils.getCurrentMonthDay()
        assertThat(realDay, `is`(expectedDay))
    }

    @Test
    fun getMonth_randomDate() {
        val date = LocalDate.of(2023, 7, 18)
        val expectedMonth = "July"
        val realMonth = utils.getMonth(date)
        assertThat(realMonth, `is`(expectedMonth))
    }

    @Test
    fun getColumnsNumber_showSaturdayShowSunday_return8() {
        val expectedColumnsNumber = 8
        val realColumnsNumber = utils.getColumnsNumber(showSaturday = true, showSunday = true)
        assertThat(realColumnsNumber, `is`(expectedColumnsNumber))
    }

    @Test
    fun getColumnsNumber_showSaturdayNoShowSunday_return7() {
        val expectedColumnsNumber = 7
        val realColumnsNumber = utils.getColumnsNumber(showSaturday = true, showSunday = false)
        assertThat(realColumnsNumber, `is`(expectedColumnsNumber))
    }

    @Test
    fun getColumnsNumber_notShowSaturdayShowSunday_return7() {
        val expectedColumnsNumber = 7
        val realColumnsNumber = utils.getColumnsNumber(showSaturday = false, showSunday = true)
        assertThat(realColumnsNumber, `is`(expectedColumnsNumber))
    }

    @Test
    fun getColumnsNumber_notShowSaturdayNotShowSunday_return6() {
        val expectedColumnsNumber = 6
        val realColumnsNumber = utils.getColumnsNumber(showSaturday = false, showSunday = false)
        assertThat(realColumnsNumber, `is`(expectedColumnsNumber))
    }

    @Test
    fun getNumHorizontalGridLines_showSaturdayShowSunday_return6() {
        val expectedNumHorizontalGridLines = 6
        val realNumHorizontalGridLines =
            utils.getNumHorizontalGridLines(showSaturday = true, showSunday = true)
        assertThat(realNumHorizontalGridLines, `is`(expectedNumHorizontalGridLines))
    }

    @Test
    fun getNumHorizontalGridLines_showSaturdayNoShowSunday_return5() {
        val expectedNumHorizontalGridLines = 5
        val realNumHorizontalGridLines =
            utils.getNumHorizontalGridLines(showSaturday = true, showSunday = false)
        assertThat(realNumHorizontalGridLines, `is`(expectedNumHorizontalGridLines))
    }

    @Test
    fun getNumHorizontalGridLines_notShowSaturdayShowSunday_return5() {
        val expectedNumHorizontalGridLines = 5
        val realNumHorizontalGridLines =
            utils.getNumHorizontalGridLines(showSaturday = false, showSunday = true)
        assertThat(realNumHorizontalGridLines, `is`(expectedNumHorizontalGridLines))
    }

    @Test
    fun getNumHorizontalGridLines_notShowSaturdayNoShowSunday_return4() {
        val expectedNumHorizontalGridLines = 4
        val realNumHorizontalGridLines =
            utils.getNumHorizontalGridLines(showSaturday = false, showSunday = false)
        assertThat(realNumHorizontalGridLines, `is`(expectedNumHorizontalGridLines))
    }
}
