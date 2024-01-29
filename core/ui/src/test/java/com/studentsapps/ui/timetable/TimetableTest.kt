package com.studentsapps.ui.timetable

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.marginStart
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.studentsapps.model.ScheduleView
import com.studentsapps.model.TimetableUserPreferences
import com.studentsapps.testing.getOrAwaitValue
import com.studentsapps.testing.launchFragmentInHiltContainer
import com.studentsapps.testing.util.withBackgroundTintList
import com.studentsapps.testing.util.withTextColor
import com.studentsapps.ui.R
import com.studentsapps.ui_test_hilt_manifest.FragmentTest
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

@Config(application = HiltTestApplication::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TimetableTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @BindValue
    val canvasRender = spyk<TimetableCanvasRender>()

    @BindValue
    val utils = spyk<TimetableUtils>()

    private val timetableContentDescription = "timetable"

    @Test
    fun setTypefaceDaysOfWeek_defaultTypeface() {
        val expectedTypeface = getTypeface(com.studentsapps.designsystem.R.font.roboto_medium)
        createTimetable()
        verifyDaysOfWeekTypeface(expectedTypeface)
    }

    @Test
    fun setTypefaceDaysOfMonth_defaultTypeface() {
        val expectedTypeface = getTypeface(com.studentsapps.designsystem.R.font.roboto_regular)
        createTimetable()
        verifyDaysOfMonthTypeface(expectedTypeface)
    }

    @Test
    fun setTypefaceDaysOfWeek_otherTypeface() {
        val fontId = com.studentsapps.designsystem.R.font.roboto_regular
        val expectedTypeface = getTypeface(fontId)
        val attrs = TimetableAttributeSet().addDaysFont(fontId, true).build()
        createTimetable(attrs)
        verifyDaysOfWeekTypeface(expectedTypeface)
    }

    @Test
    fun setTypefaceDaysOfMonth_otherTypeface() {
        val fontId = com.studentsapps.designsystem.R.font.roboto_medium
        val expectedTypeface = getTypeface(fontId)
        val attrs = TimetableAttributeSet().addDaysFont(fontId, false).build()
        createTimetable(attrs)
        verifyDaysOfMonthTypeface(expectedTypeface)
    }

    @Test
    fun setTextSizeDaysOfWeek_correctSize() {
        val daysOfWeekViewsIds = getDaysOfWeekViewsIds()
        createTimetable()
        verifyTextSize(daysOfWeekViewsIds, 4f)
    }

    @Test
    fun setTextSizeDaysOfMonth_correctSize() {
        val daysOfMonthViewsIds = getDaysOfMonthViewsIds()
        createTimetable()
        verifyTextSize(daysOfMonthViewsIds, 6f)
    }

    @Test
    fun showDaysOfWeek_defaultStartingDay() {
        mockUtilsGetDayOfWeekOrder()
        createTimetable()
        verifyDaysOfWeekTexts("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
    }

    @Test
    fun showDaysOfMonthCurrentWeek_defaultStartingMonday() {
        mockUtilsGetDaysOfMonthOfWeek()
        createTimetable()
        verifyDaysOfMonthCurrentWeekTexts("2", "3", "4", "5", "6", "7", "8")
    }

    @Test
    fun showDaysOfWeek_startingMonday() {
        mockUtilsGetDayOfWeekOrder()
        createTimetable().setTimetableUserPreferences(
            baseTimetableUserPreferences.copy(
                isMondayFirstDayOfWeek = true
            )
        )
        verifyDaysOfWeekTexts("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
    }

    @Test
    fun showDaysOfMonthCurrentWeek_startingMonday() {
        mockUtilsGetDaysOfMonthOfWeek()
        createTimetable().setTimetableUserPreferences(
            baseTimetableUserPreferences.copy(isMondayFirstDayOfWeek = true)
        )
        verifyDaysOfMonthCurrentWeekTexts("2", "3", "4", "5", "6", "7", "8")
    }

    @Test
    fun showDaysOfWeek_startingSunday() {
        mockUtilsGetDayOfWeekOrder()
        createTimetable().setTimetableUserPreferences(
            baseTimetableUserPreferences.copy(isMondayFirstDayOfWeek = false)
        )
        verifyDaysOfWeekTexts("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
    }

    @Test
    fun showDaysOfMonthCurrentWeek_startingSunday() {
        mockUtilsGetDaysOfMonthOfWeek()
        createTimetable().setTimetableUserPreferences(
            baseTimetableUserPreferences.copy(isMondayFirstDayOfWeek = false)
        )
        verifyDaysOfMonthCurrentWeekTexts("1", "2", "3", "4", "5", "6", "7")
    }

    @Test
    fun verifyGridIsDrawn() {
        mockUtilsLineCoordinates()
        val expectedVerticalLinesCoordinates = floatArrayOf(12f, 15f, 12f)
        val expectedHorizontalHourLinesCoordinates = floatArrayOf(15f, 30f, 45f)
        val expectedHalfHourHorizontalLinesCoordinates = floatArrayOf(20f, 15f, 45f)
        createTimetable()
        onView(withId(R.id.hour_drawing_container_and_grid)).check(matches(isDisplayed()))
        verify(exactly = 1) {
            canvasRender.drawGrid(
                any(),
                any(),
                any(),
                expectedVerticalLinesCoordinates,
                expectedHorizontalHourLinesCoordinates,
                expectedHalfHourHorizontalLinesCoordinates
            )
        }
    }

    @Test
    fun verifyHoursTextIsDrawn_24HourFormat() {
        val gridCellHeight = getDimensionPixelSizeById(R.dimen.timetable_grid_cell_height)
        val hourCellWidth = getDimensionPixelSizeById(R.dimen.timetable_hours_cell_width)
        val hoursIn24HourFormat = getStringArrayById(R.array.hours_in_24_hour_format).toList()
        val xAxis = hourCellWidth / 2f
        createTimetable().setTimetableUserPreferences(
            baseTimetableUserPreferences.copy(is12HoursFormat = false)
        )
        onView(withId(R.id.hour_drawing_container_and_grid)).check(matches(isDisplayed()))
        verify(exactly = 1) {
            canvasRender.drawHoursText24HourFormat(
                any(),
                hoursIn24HourFormat,
                gridCellHeight,
                any(),
                xAxis
            )
        }
    }

    @Test
    fun verifyHoursTextIsDrawn_12HourFormat() {
        val gridCellHeight = getDimensionPixelSizeById(R.dimen.timetable_grid_cell_height)
        val hourCellWidth = getDimensionPixelSizeById(R.dimen.timetable_hours_cell_width)
        val hoursIn12HourFormat = getStringArrayById(R.array.hours_in_12_hour_format).toList()
        val xAxis = hourCellWidth / 2f
        createTimetable()
        onView(withId(R.id.hour_drawing_container_and_grid)).check(matches(isDisplayed()))
        verify(exactly = 1) {
            canvasRender.drawHoursText12HourFormat(
                any(),
                hoursIn12HourFormat,
                gridCellHeight,
                any(),
                xAxis
            )
        }
    }

    @Test
    fun verifyCurrentMonthDayIsSelected() {
        mockUtilsGetCurrentDate()
        mockCanvasRenderGetCurrentMonthDayBackground()
        val expectedBackground = getExpectedBackgroundCurrentMonthDay()
        val expectedCurrentMonthDayTextColor =
            getColorById(R.color.timetable_current_month_day_text_color)
        createTimetable()
        onView(withId(R.id.sixth_day))
            .check(matches(withText("26")))
            .check(matches(isDisplayed()))
            .check(matches(withTextColor(expectedCurrentMonthDayTextColor)))
            .check(matches(withBackground(expectedBackground)))
    }

    @Test
    fun showAsGrid_true() {
        createTimetable()
        verifyTimetableGridViewIsDisplayed()
    }

    @Test
    fun showAsGrid_false() {
        createTimetable().setTimetableUserPreferences(
            baseTimetableUserPreferences.copy(showAsGrid = false)
        )
        verifyTimetableListViewIsDisplayed()
    }

    @Test
    fun verifyTimetableViewVisibilityChangeFromGridToList() {
        val timetable = createTimetable()
        verifyTimetableGridViewIsDisplayed()
        timetable.setTimetableUserPreferences(baseTimetableUserPreferences.copy(showAsGrid = false))
        verifyTimetableListViewIsDisplayed()
    }

    @Test
    fun verifyTimetableViewVisibilityChangeFromListToGrid() {
        val timetable = createTimetable()
        timetable.setTimetableUserPreferences(baseTimetableUserPreferences.copy(showAsGrid = false))
        verifyTimetableListViewIsDisplayed()
        timetable.setTimetableUserPreferences(baseTimetableUserPreferences.copy(showAsGrid = true))
        verifyTimetableGridViewIsDisplayed()
    }

    @Test
    fun showSaturdayAndStartingMonday() {
        createTimetable()
        onView(withId(R.id.sixth_day)).check(matches(isDisplayed()))
        onView(withId(R.id.sixth_day_of_week)).check(matches(isDisplayed()))
        verify { utils.getVerticalLinesCoordinates(6, any(), any(), any()) }
    }

    @Test
    fun showSaturdayAndStartingSunday() {
        createTimetable().setTimetableUserPreferences(
            baseTimetableUserPreferences.copy(
                isMondayFirstDayOfWeek = false
            )
        )
        onView(withId(R.id.seventh_day)).check(matches(isDisplayed()))
        onView(withId(R.id.seventh_day_of_week)).check(matches(isDisplayed()))
        verify { utils.getVerticalLinesCoordinates(6, any(), any(), any()) }
    }

    @Test
    fun notShowSaturdayAndStartingMonday() {
        createTimetable().setTimetableUserPreferences(
            baseTimetableUserPreferences.copy(showSaturday = false)
        )
        onView(withId(R.id.seventh_day_of_week)).check(matches(not(isDisplayed())))
        onView(withId(R.id.seventh_day)).check(matches(not(isDisplayed())))
        verify { utils.getVerticalLinesCoordinates(5, any(), any(), any()) }
    }

    @Test
    fun notShowSaturdayAndStartingSunday() {
        createTimetable().setTimetableUserPreferences(
            baseTimetableUserPreferences.copy(showSaturday = false, isMondayFirstDayOfWeek = false)
        )
        onView(withId(R.id.seventh_day_of_week)).check(matches(not(isDisplayed())))
        onView(withId(R.id.seventh_day)).check(matches(not(isDisplayed())))
        verify { utils.getVerticalLinesCoordinates(5, any(), any(), any()) }
    }

    @Test
    fun showSundayAndStartingMonday() {
        createTimetable()
        onView(withId(R.id.seventh_day_of_week)).check(matches(isDisplayed()))
        onView(withId(R.id.seventh_day)).check(matches(isDisplayed()))
        verify { utils.getVerticalLinesCoordinates(6, any(), any(), any()) }
    }

    @Test
    fun notShowSundayAndStartingMonday() {
        createTimetable().setTimetableUserPreferences(
            baseTimetableUserPreferences.copy(showSunday = false)
        )
        onView(withId(R.id.seventh_day_of_week)).check(matches(not(isDisplayed())))
        onView(withId(R.id.seventh_day)).check(matches(not(isDisplayed())))
        verify { utils.getVerticalLinesCoordinates(5, any(), any(), any()) }
    }

    @Test
    fun notShowSundayAndStartingSunday() {
        createTimetable().setTimetableUserPreferences(
            baseTimetableUserPreferences.copy(showSunday = false, isMondayFirstDayOfWeek = false)
        )
        onView(withId(R.id.seventh_day_of_week)).check(matches(not(isDisplayed())))
        onView(withId(R.id.seventh_day)).check(matches(not(isDisplayed())))
        verify { utils.getVerticalLinesCoordinates(5, any(), any(), any()) }
    }

    @Test
    fun notShowSundayNotShowSaturday() {
        createTimetable().setTimetableUserPreferences(
            baseTimetableUserPreferences.copy(showSunday = false, showSaturday = false)
        )
        onView(withId(R.id.seventh_day_of_week)).check(matches(not(isDisplayed())))
        onView(withId(R.id.seventh_day)).check(matches(not(isDisplayed())))
        onView(withId(R.id.sixth_day_of_week)).check(matches(not(isDisplayed())))
        onView(withId(R.id.sixth_day)).check(matches(not(isDisplayed())))
        verify { utils.getVerticalLinesCoordinates(4, any(), any(), any()) }
    }

    @Test
    @Config(qualifiers = "w360dp-h640dp")
    fun showSchedulesInGrid_uniqueSchedule() {
        val scheduleId = 1
        val schedules = listOf(uniqueSchedule.copy(id = scheduleId))
        val hourCellWidth = getDimensionPixelSizeById(R.dimen.timetable_hours_cell_width)
        val cellHeight = getDimensionPixelSizeById(R.dimen.timetable_grid_cell_height)
        val bottomMargin = getDimensionPixelSizeById(R.dimen.timetable_schedule_bottom_margin)
        val endMargin = getDimensionPixelSizeById(R.dimen.timetable_schedule_end_margin)
        val cellWidth = (360 - hourCellWidth) / 7

        val expectedTypeface = getTypeface(com.studentsapps.designsystem.R.font.roboto_medium)
        val expectedTextSize = 4f
        val expectedMath2XCoordinate = (hourCellWidth + cellWidth).toFloat()
        val expectedMath2YCoordinate = 13f * cellHeight
        val expectedMath2Width = cellWidth - endMargin
        val expectedMath2Height = cellHeight - bottomMargin

        val timetable = createTimetable()
        onView(withId(R.id.schedule_container_and_grid)).check(matches(isDisplayed()))
        timetable.showSchedules(schedules)

        onView(withContentDescription(scheduleId.toString()))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(withText("Math 2")))
            .check(matches(withXYCoordinates(expectedMath2XCoordinate, expectedMath2YCoordinate)))
            .check(matches(withMeasures(expectedMath2Width, expectedMath2Height)))
            .check(matches(withBackground(R.drawable.background_schedule_view)))
            .check(matches(withBackgroundTintList()))
            .check(matches(withTypeface(expectedTypeface)))
            .check(matches(withTextSize(expectedTextSize)))
    }

    @Test
    @Config(qualifiers = "w360dp-h640dp")
    fun showSchedulesInGrid_schedulesCrossing() {
        val scheduleId1 = 1
        val scheduleId2 = 2
        val schedules =
            listOf(uniqueSchedule.copy(id = scheduleId1), uniqueSchedule.copy(id = scheduleId2))
        val hourCellWidth = getDimensionPixelSizeById(R.dimen.timetable_hours_cell_width)
        val cellHeight = getDimensionPixelSizeById(R.dimen.timetable_grid_cell_height)
        val endMargin = getDimensionPixelSizeById(R.dimen.timetable_schedule_end_margin)
        val cellWidth = (360 - hourCellWidth) / 7

        val expectedScheduleId1XCoordinate = (hourCellWidth + cellWidth).toFloat()
        val expectedScheduleId1YCoordinate = 13f * cellHeight
        val expectedScheduleId1Width = (cellWidth / 2) - endMargin
        val expectedScheduleId2XCoordinate =
            (hourCellWidth + cellWidth + expectedScheduleId1Width + endMargin).toFloat()
        val expectedScheduleId2YCoordinate = 13f * cellHeight

        val timetable = createTimetable()
        onView(withId(R.id.schedule_container_and_grid)).check(matches(isDisplayed()))
        timetable.showSchedules(schedules)

        onView(withContentDescription(scheduleId1.toString()))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(
                matches(
                    withXYCoordinates(
                        expectedScheduleId1XCoordinate,
                        expectedScheduleId1YCoordinate
                    )
                )
            )

        onView(withContentDescription(scheduleId2.toString()))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(
                matches(
                    withXYCoordinates(
                        expectedScheduleId2XCoordinate,
                        expectedScheduleId2YCoordinate
                    )
                )
            )
    }

    @Test
    fun showSchedulesInGrid_courseColorDark() {
        val scheduleId = 1
        val schedules = listOf(uniqueSchedule.copy(id = scheduleId))
        val expectedColor = getColorById(R.color.timetable_schedule_view_light_text_color)
        val timetable = createTimetable()
        onView(withId(R.id.schedule_container_and_grid)).check(matches(isDisplayed()))
        timetable.showSchedules(schedules)
        onView(withContentDescription(scheduleId.toString())).check(
            matches(
                withTextColor(
                    expectedColor
                )
            )
        )
    }

    @Test
    fun showSchedulesInGrid_courseColorLight() {
        val scheduleId = 1
        val uniqueScheduleColorLight =
            listOf(uniqueSchedule.copy(id = scheduleId, color = Color.YELLOW))
        val expectedColor = getColorById(R.color.timetable_schedule_view_dark_text_color)
        val timetable = createTimetable()
        onView(withId(R.id.schedule_container_and_grid)).check(matches(isDisplayed()))
        timetable.showSchedules(uniqueScheduleColorLight)
        onView(withContentDescription(scheduleId.toString())).check(
            matches(
                withTextColor(
                    expectedColor
                )
            )
        )
    }

    @Test
    fun showSchedulesInGrid_notShowSunday() {
        val scheduleId = 1
        val schedule = listOf(uniqueSchedule.copy(id = scheduleId, dayOfWeek = DayOfWeek.SUNDAY))
        val timetable = createTimetable().apply {
            setTimetableUserPreferences(
                baseTimetableUserPreferences.copy(showSunday = false)
            )
        }
        onView(withId(R.id.schedule_container_and_grid)).check(matches(isDisplayed()))
        timetable.showSchedules(schedule)
        onView(withContentDescription(scheduleId.toString())).check(doesNotExist())
    }

    @Test
    fun showSchedulesInGrid_notShowSaturday() {
        val scheduleId = 1
        val schedule = listOf(uniqueSchedule.copy(id = scheduleId, dayOfWeek = DayOfWeek.SATURDAY))
        val timetable = createTimetable().apply {
            setTimetableUserPreferences(
                baseTimetableUserPreferences.copy(showSaturday = false)
            )
        }
        onView(withId(R.id.schedule_container_and_grid)).check(matches(isDisplayed()))
        timetable.showSchedules(schedule)
        onView(withContentDescription(scheduleId.toString())).check(doesNotExist())
    }

    @Test
    fun showSchedulesInGrid_notShowSaturday_showSunday() {
        val saturdayScheduleId = 1
        val sundayScheduleId = 2
        val schedules = listOf(
            uniqueSchedule.copy(id = saturdayScheduleId, dayOfWeek = DayOfWeek.SATURDAY),
            uniqueSchedule.copy(id = sundayScheduleId, dayOfWeek = DayOfWeek.SUNDAY)
        )
        val timetable = createTimetable().apply {
            setTimetableUserPreferences(
                baseTimetableUserPreferences.copy(showSaturday = false)
            )
        }
        onView(withId(R.id.schedule_container_and_grid)).check(matches(isDisplayed()))
        timetable.showSchedules(schedules)
        onView(withContentDescription(saturdayScheduleId.toString())).check(doesNotExist())
        onView(withContentDescription(sundayScheduleId.toString())).perform(scrollTo()).check(
            matches(isDisplayed())
        )
    }

    @Test
    @Config(qualifiers = "w360dp-h640dp")
    fun showSchedulesInGrid_startingSunday() {
        val mondayScheduleId = 1
        val sundayScheduleId = 2
        val schedules = listOf(
            uniqueSchedule.copy(id = mondayScheduleId, dayOfWeek = DayOfWeek.MONDAY),
            uniqueSchedule.copy(id = sundayScheduleId, dayOfWeek = DayOfWeek.SUNDAY)
        )

        val hourCellWidth = getDimensionPixelSizeById(R.dimen.timetable_hours_cell_width)
        val cellHeight = getDimensionPixelSizeById(R.dimen.timetable_grid_cell_height)
        val cellWidth = (360 - hourCellWidth) / 7

        val expectedSundayScheduleXCoordinate = hourCellWidth.toFloat()
        val expectedSundayScheduleYCoordinate = 13f * cellHeight
        val expectedMondayScheduleXCoordinate = (hourCellWidth + cellWidth).toFloat()
        val expectedMondayScheduleYCoordinate = 13f * cellHeight

        val timetable = createTimetable().apply {
            setTimetableUserPreferences(
                baseTimetableUserPreferences.copy(isMondayFirstDayOfWeek = false)
            )
        }
        onView(withId(R.id.schedule_container_and_grid)).check(matches(isDisplayed()))
        timetable.showSchedules(schedules)

        onView(withContentDescription(mondayScheduleId.toString()))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(
                matches(
                    withXYCoordinates(
                        expectedMondayScheduleXCoordinate,
                        expectedMondayScheduleYCoordinate
                    )
                )
            )

        onView(withContentDescription(sundayScheduleId.toString()))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(
                matches(
                    withXYCoordinates(
                        expectedSundayScheduleXCoordinate,
                        expectedSundayScheduleYCoordinate
                    )
                )
            )
    }

    @Test
    fun swipeToShowGridViewChangeTextOfDayOfMonthViews() {
        val daysOfMonthWhenScrollingLeft = listOf("9", "10", "11", "12", "13", "14", "15")
        mockUtilsGetDaysOfMonthOfWeek()
        createTimetable()
        verifyDaysOfMonthCurrentWeekTexts("2", "3", "4", "5", "6", "7", "8")
        mockUtilsGetDaysOfMonthOfWeek(daysOfMonthWhenScrollingLeft)
        onView(withContentDescription(timetableContentDescription))
            .perform(swipeLeft())
        verifyDaysOfMonthCurrentWeekTexts("9", "10", "11", "12", "13", "14", "15")
    }

    @Test
    fun testNotSelectCurrentMonthDayInNonCurrentWeekGridView() {
        mockCanvasRenderGetCurrentMonthDayBackground()
        val currentMonthDayBackground = getExpectedBackgroundCurrentMonthDay()
        createTimetable()
        onView(withContentDescription(timetableContentDescription))
            .perform(swipeRight())
        getDaysOfMonthViewsIds().forEach { viewId ->
            onView(withId(viewId)).check(matches(not(withBackground(currentMonthDayBackground))))
        }
    }

    @Test
    fun testSelectedWeekMonthDisplay() {
        val expectedMonth = "September"
        mockUtilsGetCurrentDate()
        val timetable = createTimetable()
        onView(withContentDescription(timetableContentDescription))
            .perform(swipeLeft())
        assertThat(timetable.currentMonth.getOrAwaitValue(), `is`(expectedMonth))
    }

    @Test
    fun setTimetableUserPreferences_showAsGridFalseIs12HoursFormatFalseShowSaturdayFalseIsMondayFalseShowSundayFalse() {
        createTimetable().setTimetableUserPreferences(
            baseTimetableUserPreferences.copy(
                showAsGrid = false,
                is12HoursFormat = false,
                showSaturday = false,
                isMondayFirstDayOfWeek = false,
                showSunday = false
            )
        )

        onView(withContentDescription(timetableContentDescription)).check(matches(isDisplayed()))
        verifyTimetableListViewIsDisplayed()
        verify { canvasRender.drawHoursText24HourFormat(any(), any(), any(), any(), any()) }
        onView(withId(R.id.seventh_day_of_week)).check(matches(not(isDisplayed())))
        onView(withId(R.id.seventh_day)).check(matches(not(isDisplayed())))
    }

    @Test
    fun verifyCorrectFirstDayOfWeekMarginInGridOrListMode() {
        val expectedMarginGrid = getDimensionPixelSizeById(R.dimen.timetable_hours_cell_width)
        val expectedMarginList = 0
        val timetable = createTimetable()
        onView(withId(R.id.start_day_of_week)).check(matches(withMarginStart(expectedMarginGrid)))
        timetable.setTimetableUserPreferences(baseTimetableUserPreferences.copy(showAsGrid = false))
        onView(withId(R.id.start_day_of_week)).check(matches(withMarginStart(expectedMarginList)))
    }

    @Test
    fun verifySwipeUpdatesDate() {
        val currentDate = LocalDate.of(2023, 8, 26)
        val swipeLeftDate = currentDate.plusWeeks(1)
        val swipeRightDate = currentDate.minusWeeks(1)
        mockUtilsGetCurrentDate()
        val timetable = createTimetable()
        onView(withContentDescription(timetableContentDescription)).perform(swipeLeft())
        assertThat(timetable.date.getOrAwaitValue(), `is`(swipeLeftDate))
        onView(withContentDescription(timetableContentDescription)).perform(swipeRight())
        assertThat(timetable.date.getOrAwaitValue(), `is`(currentDate))
        onView(withContentDescription(timetableContentDescription)).perform(swipeRight())
        assertThat(timetable.date.getOrAwaitValue(), `is`(swipeRightDate))
    }

    @Test
    fun verifyListTypeDayClickUpdatesDate() {
        mockUtilsGetCurrentDate()
        every { utils.getCurrentMonthDay() } returns "26"
        mockCanvasRenderGetCurrentMonthDayBackground()
        val expectedDate = LocalDate.of(2023, 8, 21)
        val expectedBackground = getExpectedBackgroundCurrentMonthDay()
        val expectedCurrentDayColor =
            getColorById(R.color.timetable_current_month_day_background_color)
        val expectedSelectedDayColor = getColorById(R.color.timetable_current_month_day_text_color)
        val timetable = createTimetable()
        timetable.setTimetableUserPreferences(baseTimetableUserPreferences.copy(showAsGrid = false))
        onView(withId(R.id.first_day)).perform(click())
        assertThat(timetable.date.getOrAwaitValue(), `is`(expectedDate))
        onView(withId(R.id.first_day)).check(matches(withBackground(expectedBackground)))
        onView(withId(R.id.first_day)).check(matches(withTextColor(expectedSelectedDayColor)))
        onView(withId(R.id.sixth_day)).check(matches(withTextColor(expectedCurrentDayColor)))
    }

    @Test
    fun verifyGridModeDayClickDoesNotActivate() {
        mockUtilsGetCurrentDate()
        val expectedDate = LocalDate.of(2023, 8, 26)
        val expectedColor = getColorById(R.color.timetable_month_day_text_color)
        val timetable = createTimetable()
        onView(withId(R.id.first_day)).perform(click())
        assertThat(timetable.date.getOrAwaitValue(), `is`(expectedDate))
        onView(withId(R.id.first_day)).check(matches(withTextColor(expectedColor)))
    }

    @Test
    fun verifySwitchToListChangesToCurrentDate() {
        mockUtilsGetCurrentDate()
        val expectedDate = LocalDate.of(2023, 8, 26)
        val expectedSelectedDayColor = getColorById(R.color.timetable_current_month_day_text_color)
        val timetable = createTimetable()
        timetable.setTimetableUserPreferences(baseTimetableUserPreferences.copy(showAsGrid = false))
        onView(withId(R.id.second_day)).perform(click())
        timetable.setTimetableUserPreferences(baseTimetableUserPreferences.copy(showAsGrid = true))
        assertThat(timetable.date.getOrAwaitValue(), `is`(expectedDate))
        onView(withId(R.id.sixth_day)).check(matches(withTextColor(expectedSelectedDayColor)))
    }

    @Test
    fun verifySwipeInGridModeSelectsOnlyCurrentDate() {
        mockCanvasRenderGetCurrentMonthDayBackground()
        val expectedTextColor = getColorById(R.color.timetable_month_day_text_color)
        createTimetable()
        onView(withContentDescription(timetableContentDescription)).perform(swipeLeft())
        getDaysOfMonthViewsIds().forEach { viewId ->
            onView(withId(viewId)).check(matches(withTextColor(expectedTextColor)))
        }
    }

    @Test
    fun verifySwipeInGridModeAffectsDateWithinWeek() {
        mockUtilsGetCurrentDate()
        val expectedCurrentDate = LocalDate.of(2023, 8, 26)
        val expectedSwipeLeftDate = LocalDate.of(2023, 9, 2)
        val expectedSwipeRightDate = LocalDate.of(2023, 8, 19)
        val timetable = createTimetable()
        assertThat(timetable.date.getOrAwaitValue(), `is`(expectedCurrentDate))
        onView(withContentDescription(timetableContentDescription)).perform(swipeLeft())
        assertThat(timetable.date.getOrAwaitValue(), `is`(expectedSwipeLeftDate))
        onView(withContentDescription(timetableContentDescription)).perform(swipeRight())
        assertThat(timetable.date.getOrAwaitValue(), `is`(expectedCurrentDate))
        onView(withContentDescription(timetableContentDescription)).perform(swipeRight())
        assertThat(timetable.date.getOrAwaitValue(), `is`(expectedSwipeRightDate))
    }

    @Test
    fun verifySwipeInListModeAffectsDateByOneDay() {
        mockUtilsGetCurrentDate()
        val expectedCurrentDate = LocalDate.of(2023, 8, 26)
        val expectedSwipeLeftDate = LocalDate.of(2023, 8, 27)
        val expectedSwipeRightDate = LocalDate.of(2023, 8, 25)
        val timetable = createTimetable()
        timetable.setTimetableUserPreferences(baseTimetableUserPreferences.copy(showAsGrid = false))
        assertThat(timetable.date.getOrAwaitValue(), `is`(expectedCurrentDate))
        onView(withContentDescription(timetableContentDescription)).perform(swipeLeft())
        assertThat(timetable.date.getOrAwaitValue(), `is`(expectedSwipeLeftDate))
        onView(withContentDescription(timetableContentDescription)).perform(swipeRight())
        assertThat(timetable.date.getOrAwaitValue(), `is`(expectedCurrentDate))
        onView(withContentDescription(timetableContentDescription)).perform(swipeRight())
        assertThat(timetable.date.getOrAwaitValue(), `is`(expectedSwipeRightDate))
    }

    @Test
    fun verifySwipeInListModeSelectsCorrectDayOfMonth() {
        mockUtilsGetCurrentDate()
        val expectedSelectedDayColor = getColorById(R.color.timetable_current_month_day_text_color)
        val expectedCurrentDayColor =
            getColorById(R.color.timetable_current_month_day_background_color)
        createTimetable().setTimetableUserPreferences(baseTimetableUserPreferences.copy(showAsGrid = false))
        onView(withId(R.id.sixth_day)).check(matches(withTextColor(expectedSelectedDayColor)))
        onView(withContentDescription(timetableContentDescription)).perform(swipeLeft())
        onView(withId(R.id.seventh_day)).check(matches(withTextColor(expectedSelectedDayColor)))
        onView(withId(R.id.sixth_day)).check(matches(withTextColor(expectedCurrentDayColor)))
    }

    @Test
    fun verifySwipeAndChangeFromCurrentWeekInListMode() {
        mockUtilsGetCurrentDate(LocalDate.of(2023, 11, 13))
        val expectedDate = LocalDate.of(2023, 11, 20)
        val expectedTextColor = getColorById(R.color.timetable_current_month_day_text_color)
        val timetable = createTimetable()
        timetable.setTimetableUserPreferences(
            baseTimetableUserPreferences.copy(
                showAsGrid = false,
                showSunday = false,
                showSaturday = false
            )
        )
        onView(withId(R.id.fifth_day)).perform(click())
        onView(withContentDescription(timetableContentDescription)).perform(swipeLeft())
        assertThat(timetable.date.getOrAwaitValue(), `is`(expectedDate))
        onView(withId(R.id.first_day)).check(matches(withTextColor(expectedTextColor)))
    }

    @Test
    fun verifyChangeWeekInListModeAndClickOnDayOfMonth() {
        mockUtilsGetCurrentDate(LocalDate.of(2023, 11, 13))
        val expectedDate = LocalDate.of(2023, 11, 21)
        val expectedTextColor = getColorById(R.color.timetable_current_month_day_text_color)
        val timetable = createTimetable()
        timetable.setTimetableUserPreferences(baseTimetableUserPreferences.copy(showAsGrid = false))
        onView(withId(R.id.seventh_day)).perform(click())
        onView(withContentDescription(timetableContentDescription)).perform(swipeLeft())
        onView(withId(R.id.second_day)).perform(click())
        assertThat(timetable.date.getOrAwaitValue(), `is`(expectedDate))
        onView(withId(R.id.second_day)).check(matches(withTextColor(expectedTextColor)))
    }

    @Test
    fun verifySwipeRightInListModeWithoutSatSunSelectsCorrectDay() {
        mockUtilsGetCurrentDate(LocalDate.of(2023, 11, 13))
        val expectedDate = LocalDate.of(2023, 11, 10)
        val expectedTextColor = getColorById(R.color.timetable_current_month_day_text_color)
        val timetable = createTimetable()
        timetable.setTimetableUserPreferences(
            baseTimetableUserPreferences.copy(
                showAsGrid = false,
                showSaturday = false,
                showSunday = false
            )
        )
        onView(withContentDescription(timetableContentDescription)).perform(swipeRight())
        assertThat(timetable.date.getOrAwaitValue(), `is`(expectedDate))
        onView(withId(R.id.fifth_day)).check(matches(withTextColor(expectedTextColor)))
    }

    @Test
    fun verifySwipeRightInListModeWithSundayAsFirstDaySelectsCorrectDay() {
        mockUtilsGetCurrentDate(LocalDate.of(2023, 11, 13))
        val expectedTextColor = getColorById(R.color.timetable_current_month_day_text_color)
        val expectedDate = LocalDate.of(2023, 11, 12)
        val timetable = createTimetable()
        timetable.setTimetableUserPreferences(
            baseTimetableUserPreferences.copy(
                isMondayFirstDayOfWeek = false,
                showAsGrid = false
            )
        )
        onView(withContentDescription(timetableContentDescription)).perform(swipeRight())
        assertThat(timetable.date.getOrAwaitValue(), `is`(expectedDate))
        onView(withId(R.id.first_day)).check(matches(withTextColor(expectedTextColor)))
    }

    @Test
    fun selectCurrentDay_inGridMode() {
        val expectedDate = LocalDate.of(2023, 11, 13)
        val expectedTextColor = getColorById(R.color.timetable_current_month_day_text_color)
        mockUtilsGetCurrentDate(expectedDate)
        val timetable = createTimetable()
        onView(withContentDescription(timetableContentDescription)).perform(swipeRight())
        timetable.selectCurrentDay()
        assertThat(timetable.date.getOrAwaitValue(), `is`(expectedDate))
        onView(withId(R.id.first_day)).check(matches(withTextColor(expectedTextColor)))
    }

    @Test
    fun selectCurrentDay_inListMode() {
        val expectedDate = LocalDate.of(2023, 11, 13)
        val expectedTextColor = getColorById(R.color.timetable_current_month_day_text_color)
        mockUtilsGetCurrentDate(expectedDate)
        val timetable = createTimetable()
        timetable.setTimetableUserPreferences(baseTimetableUserPreferences.copy(showAsGrid = false))
        onView(withContentDescription(timetableContentDescription)).perform(swipeRight())
        timetable.selectCurrentDay()
        assertThat(timetable.date.getOrAwaitValue(), `is`(expectedDate))
        onView(withId(R.id.first_day)).check(matches(withTextColor(expectedTextColor)))
    }

    @Test
    fun isDisplayedAsGrid_returnTrueAndFalse() {
        val timetable = createTimetable()
        assertThat(timetable.isDisplayedAsGrid(), `is`(true))
        timetable.setTimetableUserPreferences(baseTimetableUserPreferences.copy(showAsGrid = false))
        assertThat(timetable.isDisplayedAsGrid(), `is`(false))
    }

    @Test
    fun displaySaturday_returnTrueAndFalse() {
        val timetable = createTimetable()
        assertThat(timetable.displaySaturday(), `is`(true))
        timetable.setTimetableUserPreferences(baseTimetableUserPreferences.copy(showSaturday = false))
        assertThat(timetable.displaySaturday(), `is`(false))
    }

    @Test
    fun displaySunday_returnTrueAndFalse() {
        val timetable = createTimetable()
        assertThat(timetable.displaySunday(), `is`(true))
        timetable.setTimetableUserPreferences(baseTimetableUserPreferences.copy(showSunday = false))
        assertThat(timetable.displaySunday(), `is`(false))
    }

    @Test
    fun getStartDate_returnDate() {
        mockUtilsGetCurrentDate(LocalDate.of(2023, 11, 23))
        val expectedStartDate = LocalDate.of(2023, 11, 20)
        val timetable = createTimetable()
        assertThat(timetable.getStartDate(), `is`(expectedStartDate))
    }

    @Test
    fun getEndDate_returnDate() {
        mockUtilsGetCurrentDate(LocalDate.of(2023, 11, 23))
        val expectedEndDate = LocalDate.of(2023, 11, 26)
        val timetable = createTimetable()
        assertThat(timetable.getEndDate(), `is`(expectedEndDate))
    }

    @Test
    fun testSchedulesDisplayedInListModeForSpecificDate() {
        val scheduleViewList = listOf(uniqueSchedule)
        val timetable = createTimetable().apply {
            setTimetableUserPreferences(baseTimetableUserPreferences.copy(showAsGrid = false))
        }
        timetable.showSchedules(scheduleViewList)
        onView(
            allOf(
                withId(R.id.timetable_list_item_course_name),
                withParent(withTagValue(`is`(2)))
            )
        ).check(matches(withText("Math 2")))
        onView(
            allOf(
                withId(R.id.timetable_list_item_course_hour),
                withParent(withTagValue(`is`(2)))
            )
        ).check(matches(withText("1:00 PM -> 2:00 PM")))
        onView(
            allOf(
                withId(R.id.timetable_list_item_classroom),
                withParent(withTagValue(`is`(2)))
            )
        ).check(matches(withText("classroom 2")))
    }

    @Test
    fun testCheckTimeFormatInListModeIs24HourFormat() {
        val scheduleViewList = listOf(uniqueSchedule)
        val timetable = createTimetable().apply {
            setTimetableUserPreferences(
                baseTimetableUserPreferences.copy(
                    showAsGrid = false,
                    is12HoursFormat = false
                )
            )
        }
        timetable.showSchedules(scheduleViewList)
        onView(
            allOf(
                withId(R.id.timetable_list_item_course_hour),
                withParent(withTagValue(`is`(2)))
            )
        ).check(matches(withText("13:00 -> 14:00")))
    }

    private fun createTimetable(
        attr: AttributeSet? = null
    ): Timetable {
        var timetable: Timetable? = null
        launchFragmentInHiltContainer<FragmentTest> {
            val attributeSet = attr ?: Robolectric.buildAttributeSet().build()
            timetable = Timetable(this.requireContext(), attributeSet)
            val layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            timetable!!.apply {
                this.layoutParams = layoutParams
                contentDescription = timetableContentDescription
            }
            binding.root.addView(timetable)
            timetable!!.setTimetableUserPreferences(baseTimetableUserPreferences)
        }
        return timetable!!
    }

    private fun getExpectedBackgroundCurrentMonthDay(): Drawable {
        return Bitmap.createBitmap(
            10,
            10,
            Bitmap.Config.ARGB_8888
        ).toDrawable(ApplicationProvider.getApplicationContext<Context>().resources)
    }

    private fun mockUtilsGetCurrentDate(localDate: LocalDate? = null) {
        every { utils.getCurrentDate() } returns (localDate ?: LocalDate.of(2023, 8, 26))
    }

    private fun mockCanvasRenderGetCurrentMonthDayBackground() {
        every {
            canvasRender.getCurrentMonthDayBackground(
                any(),
                any(),
                any()
            )
        } returns Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
    }

    private fun mockUtilsLineCoordinates() {
        every {
            utils.getVerticalLinesCoordinates(
                any(),
                any(),
                any(),
                any()
            )
        } returns floatArrayOf(12f, 15f, 12f)
        every {
            utils.getHorizontalHourLinesCoordinates(
                any(),
                any(),
                any(),
                any()
            )
        } returns floatArrayOf(15f, 30f, 45f)
        every {
            utils.getHalfHourHorizontalLinesCoordinates(
                any(),
                any(),
                any(),
                any()
            )
        } returns floatArrayOf(20f, 15f, 45f)
    }

    private fun mockUtilsGetDaysOfMonthOfWeek(list: List<String>? = null) {
        every {
            utils.getDaysOfMonthOfWeek(any(), any(), any(), any())
        } answers {
            list?.map { LocalDate.of(2023, 7, it.toInt()) }
                ?: if (arg(0)) fakeDaysOfMonthCurrentWeekStartingMonday.map {
                    LocalDate.of(
                        2023,
                        7,
                        it.toInt()
                    )
                }
                else fakeDaysOfMonthCurrentWeekStartingSunday.map {
                    LocalDate.of(
                        2023,
                        7,
                        it.toInt()
                    )
                }
        }
    }

    private fun mockUtilsGetDayOfWeekOrder() {
        every {
            utils.getDaysOfWeekOrder(
                any(),
                any(),
                any()
            )
        } answers { if (arg(0)) fakeDaysOfWeekStartingMonday else fakeDaysOfWeekStartingSunday }
    }

    private fun getTypeface(@FontRes fontId: Int): Typeface {
        return ResourcesCompat.getFont(ApplicationProvider.getApplicationContext(), fontId)!!
    }

    private fun getDimensionPixelSizeById(@DimenRes dimenId: Int): Int {
        return ApplicationProvider.getApplicationContext<Application>().resources.getDimensionPixelSize(
            dimenId
        )
    }

    private fun getStringArrayById(@ArrayRes arrayId: Int): Array<String> {
        return ApplicationProvider.getApplicationContext<Context?>().resources.getStringArray(
            arrayId
        )
    }

    private fun getColorById(@ColorRes colorId: Int): Int {
        return ContextCompat.getColor(ApplicationProvider.getApplicationContext(), colorId)
    }

    private fun verifyTextSize(viewsIds: List<Int>, expectedTextSize: Float) {
        for (viewId in viewsIds) {
            onView(withId(viewId)).check(matches(withTextSize(expectedTextSize)))
        }
    }

    private fun verifyDaysOfMonthCurrentWeekTexts(vararg expectedDaysTexts: String) {
        verifyTexts(getDaysOfMonthViewsIds(), expectedDaysTexts.toList())
    }

    private fun verifyDaysOfWeekTexts(vararg expectedDaysTexts: String) {
        verifyTexts(getDaysOfWeekViewsIds(), expectedDaysTexts.toList())
    }


    private fun verifyTexts(daysViewsIds: List<Int>, expectedDayTexts: List<String>) {
        daysViewsIds.forEachIndexed { index, viewId ->
            onView(withId(viewId))
                .check(matches(withText(expectedDayTexts[index])))
                .check(matches(isDisplayed()))
        }
    }

    private fun verifyDaysOfWeekTypeface(expectedTypeface: Typeface) {
        getDaysOfWeekViewsIds().forEach { viewId ->
            verifyTypeface(viewId, expectedTypeface)
        }
    }

    private fun verifyDaysOfMonthTypeface(expectedTypeface: Typeface) {
        getDaysOfMonthViewsIds().forEach { viewId ->
            verifyTypeface(viewId, expectedTypeface)
        }
    }

    private fun verifyTypeface(viewId: Int, expectedTypeface: Typeface) {
        onView(withId(viewId)).check(matches(withTypeface(expectedTypeface)))
    }

    private fun getDaysOfWeekViewsIds(): List<Int> {
        return listOf(
            R.id.start_day_of_week, R.id.second_day_of_week, R.id.third_day_of_week,
            R.id.fourth_day_of_week, R.id.fifth_day_of_week, R.id.sixth_day_of_week,
            R.id.seventh_day_of_week
        )
    }

    private fun getDaysOfMonthViewsIds(): List<Int> {
        return listOf(
            R.id.first_day, R.id.second_day, R.id.third_day,
            R.id.fourth_day, R.id.fifth_day, R.id.sixth_day,
            R.id.seventh_day
        )
    }

    private fun withBackground(expectedBackground: Drawable): Matcher<View> {
        return object : BoundedMatcher<View, TextView>(TextView::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("with background: ")
                description?.appendValue(expectedBackground)
            }

            override fun matchesSafely(item: TextView): Boolean {
                if (item.background != null)
                    return item.background.toBitmap().sameAs(expectedBackground.toBitmap())
                return false
            }
        }
    }

    private fun withBackground(@DrawableRes drawableId: Int): Matcher<View> {
        return object : BoundedMatcher<View, TextView>(TextView::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("with background: ")
                description?.appendValue(drawableId)
            }

            override fun matchesSafely(item: TextView): Boolean {
                val expectedDrawable =
                    ContextCompat.getDrawable(item.context, drawableId) ?: return false
                val bitmap1 = item.background.toBitmap(1, 1)
                val bitmap = expectedDrawable.toBitmap(1, 1)
                return bitmap1.sameAs(bitmap)
            }
        }
    }

    private fun withTypeface(expectedTypeface: Typeface): Matcher<View> {
        return object : BoundedMatcher<View, TextView>(TextView::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("with typeface: ")
                description?.appendValue(expectedTypeface)
            }

            override fun matchesSafely(item: TextView?): Boolean {
                return item?.typeface == expectedTypeface
            }
        }
    }

    private fun withTextSize(expectedTextSize: Float): Matcher<View> {
        return object : BoundedMatcher<View, TextView>(TextView::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("with textSize: ")
                description?.appendValue(expectedTextSize)
            }

            override fun matchesSafely(item: TextView?): Boolean {
                return item?.textSize == expectedTextSize
            }
        }
    }

    private fun withXYCoordinates(x: Float, y: Float): Matcher<View> {
        return object : BoundedMatcher<View, TextView>(TextView::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("with X and Y coordinates: ")
                description?.appendValue("$x and $y")
            }

            override fun matchesSafely(item: TextView?): Boolean {
                return (item?.x == x && item.y == y)
            }

        }
    }

    private fun withMeasures(width: Int, height: Int): Matcher<View> {
        return object : BoundedMatcher<View, TextView>(TextView::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("with measures: ")
                description?.appendValue("$width and $height")
            }

            override fun matchesSafely(item: TextView?): Boolean {
                return (item?.width == width && item.height == height)
            }

        }
    }

    private fun withMarginStart(marginStart: Int): Matcher<View> {
        return object : BoundedMatcher<View, TextView>(TextView::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("with marginStart: ")
                description?.appendValue(marginStart)
            }

            override fun matchesSafely(item: TextView?): Boolean {
                return item?.marginStart == marginStart
            }

        }
    }

    private fun verifyTimetableGridViewIsDisplayed() {
        onView(withId(R.id.schedule_container_and_grid))
            .check(matches(isDisplayed())).check(
                matches(withEffectiveVisibility(Visibility.VISIBLE))
            )
        onView(withId(R.id.schedule_list_container))
            .check(matches(not(isDisplayed()))).check(
                matches(withEffectiveVisibility(Visibility.GONE))
            )
    }

    private fun verifyTimetableListViewIsDisplayed() {
        onView(withId(R.id.schedule_list_container)).check(
            matches(
                withEffectiveVisibility(Visibility.VISIBLE)
            )
        )
        onView(withId(R.id.schedule_container_and_grid))
            .check(matches(not(isDisplayed())))
            .check(
                matches(withEffectiveVisibility(Visibility.GONE))
            )
    }

    companion object {
        private val fakeDaysOfWeekStartingMonday = listOf(
            R.string.monday_abbr_test,
            R.string.tuesday_abbr_test,
            R.string.wednesday_abbr_test,
            R.string.thursday_abbr_test,
            R.string.friday_abbr_test,
            R.string.saturday_abbr_test,
            R.string.sunday_abbr_test
        )

        private val fakeDaysOfWeekStartingSunday = listOf(
            R.string.sunday_abbr_test,
            R.string.monday_abbr_test,
            R.string.tuesday_abbr_test,
            R.string.wednesday_abbr_test,
            R.string.thursday_abbr_test,
            R.string.friday_abbr_test,
            R.string.saturday_abbr_test
        )

        private val fakeDaysOfMonthCurrentWeekStartingMonday =
            listOf("2", "3", "4", "5", "6", "7", "8")

        private val fakeDaysOfMonthCurrentWeekStartingSunday =
            listOf("1", "2", "3", "4", "5", "6", "7")

        private val uniqueSchedule = ScheduleView(
            2,
            LocalTime.of(13, 0),
            LocalTime.of(14, 0),
            "classroom 2",
            DayOfWeek.TUESDAY,
            "Math 2",
            Color.BLUE
        )

        private val baseTimetableUserPreferences =
            TimetableUserPreferences(
                showAsGrid = true,
                is12HoursFormat = true,
                showSaturday = true,
                showSunday = true,
                isMondayFirstDayOfWeek = true
            )
    }
}
