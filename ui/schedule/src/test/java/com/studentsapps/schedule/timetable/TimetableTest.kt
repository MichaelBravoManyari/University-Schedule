package com.studentsapps.schedule.timetable

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ArrayRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.FontRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.studentsapps.schedule.R
import com.studentsapps.schedule.TestFragment
import com.studentsapps.schedule.launchFragmentInHiltContainer
import com.studentsapps.schedule.verifyTimetableGridViewIsDisplayed
import com.studentsapps.schedule.verifyTimetableListViewIsDisplayed
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config

@Config(application = HiltTestApplication::class, sdk = [33])
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TimetableTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    val canvasRender = spyk<TimetableCanvasRender>()

    @BindValue
    val utils = spyk<TimetableUtils>()

    @Test
    fun setTypefaceDaysOfWeek_defaultTypeface() {
        val expectedTypeface = getTypeface(R.font.roboto_medium)
        createTimetable()
        verifyDaysOfWeekTypeface(expectedTypeface)
    }

    @Test
    fun setTypefaceDaysOfMonth_defaultTypeface() {
        val expectedTypeface = getTypeface(R.font.roboto_regular)
        createTimetable()
        verifyDaysOfMonthTypeface(expectedTypeface)
    }

    @Test
    fun setTypefaceDaysOfWeek_otherTypeface() {
        val fontId = R.font.roboto_regular
        val expectedTypeface = getTypeface(fontId)
        val attr = createTimetableAttributeSetWithDaysFont(fontId, true)
        createTimetable(attr)
        verifyDaysOfWeekTypeface(expectedTypeface)
    }

    @Test
    fun setTypefaceDaysOfMonth_otherTypeface() {
        val fontId = R.font.roboto_medium
        val expectedTypeface = getTypeface(fontId)
        val attr = createTimetableAttributeSetWithDaysFont(fontId, false)
        createTimetable(attr)
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
        mockUtilGetDaysOfMonthOfWeek()
        createTimetable()
        verifyDaysOfMonthCurrentWeekTexts("2", "3", "4", "5", "6", "7", "8")
    }

    @Test
    fun showDaysOfWeek_startingMonday() {
        mockUtilsGetDayOfWeekOrder()
        val isMondayFirstOfWeek = true
        val attr = createTimetableAttributeSetMondayFirst(isMondayFirstOfWeek)
        createTimetable(attr)
        verifyDaysOfWeekTexts("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
    }

    @Test
    fun showDaysOfMonthCurrentWeek_startingMonday() {
        mockUtilGetDaysOfMonthOfWeek()
        val isMondayFirstOfWeek = true
        val attributeSet = createTimetableAttributeSetMondayFirst(isMondayFirstOfWeek)
        createTimetable(attributeSet)
        verifyDaysOfMonthCurrentWeekTexts("2", "3", "4", "5", "6", "7", "8")
    }

    @Test
    fun showDaysOfWeek_startingSunday() {
        mockUtilsGetDayOfWeekOrder()
        val isMondayFirstOfWeek = false
        val attr = createTimetableAttributeSetMondayFirst(isMondayFirstOfWeek)
        createTimetable(attr)
        verifyDaysOfWeekTexts("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
    }

    @Test
    fun showDaysOfMonthCurrentWeek_startingSunday() {
        mockUtilGetDaysOfMonthOfWeek()
        val isMondayFirstOfWeek = false
        val attributeSet = createTimetableAttributeSetMondayFirst(isMondayFirstOfWeek)
        createTimetable(attributeSet)
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
        val is12HoursFormat = false
        val attrs = createTimetableAttributeSetWithTimeFormat(is12HoursFormat)
        val gridCellHeight = getDimensionPixelSizeById(R.dimen.timetable_grid_cell_height)
        val hourCellWidth = getDimensionPixelSizeById(R.dimen.timetable_hours_cell_width)
        val hoursIn24HourFormat = getStringArrayById(R.array.hours_in_24_hour_format).toList()
        val xAxis = hourCellWidth / 2f
        createTimetable(attrs)
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
        val is12HoursFormat = true
        val attrs = createTimetableAttributeSetWithTimeFormat(is12HoursFormat)
        val gridCellHeight = getDimensionPixelSizeById(R.dimen.timetable_grid_cell_height)
        val hourCellWidth = getDimensionPixelSizeById(R.dimen.timetable_hours_cell_width)
        val hoursIn12HourFormat = getStringArrayById(R.array.hours_in_12_hour_format).toList()
        val xAxis = hourCellWidth / 2f
        createTimetable(attrs)
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
        mockUtilsGetCurrentMonthDayString()
        mockUtilGetDaysOfMonthOfWeek()
        mockCanvasRenderGetCurrentMonthDayBackground()
        val expectedBackground = getExpectedBackgroundCurrentMonthDay()
        val expectedCurrentMonthDayTextColor =
            getColorById(R.color.timetable_current_month_day_text_color)
        createTimetable()
        onView(withId(R.id.first_day))
            .check(matches(withText("2")))
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
        createTimetable(showAsGrid = false)
        verifyTimetableListViewIsDisplayed()
    }

    @Test
    fun verifyTimetableViewVisibilityChangeFromGridToList() {
        val timetable = createTimetable()
        verifyTimetableGridViewIsDisplayed()
        timetable.setShowAsGrid(false)
        verifyTimetableListViewIsDisplayed()
    }

    @Test
    fun verifyTimetableViewVisibilityChangeFromListToGrid() {
        val timetable = createTimetable(showAsGrid = false)
        verifyTimetableListViewIsDisplayed()
        timetable.setShowAsGrid(true)
        verifyTimetableGridViewIsDisplayed()
    }

    @Test
    fun showSaturdayAndStartingMonday() {
        val attrs = createTimetableAttributeSetWithShowSaturday(
            showSaturday = true,
            isMondayFirstOfWeek = true
        )
        createTimetable(attrs)
        onView(withId(R.id.sixth_day)).check(matches(isDisplayed()))
        onView(withId(R.id.sixth_day_of_week)).check(matches(isDisplayed()))
        verify { utils.getVerticalLinesCoordinates(6, any(), any(), any()) }
    }

    @Test
    fun showSaturdayAndStartingSunday() {
        val attrs = createTimetableAttributeSetWithShowSaturday(
            showSaturday = true,
            isMondayFirstOfWeek = false
        )
        createTimetable(attrs)
        onView(withId(R.id.seventh_day)).check(matches(isDisplayed()))
        onView(withId(R.id.seventh_day_of_week)).check(matches(isDisplayed()))
        verify { utils.getVerticalLinesCoordinates(6, any(), any(), any()) }
    }

    @Test
    fun notShowSaturdayAndStartingMonday() {
        val attrs = createTimetableAttributeSetWithShowSaturday(
            showSaturday = false,
            isMondayFirstOfWeek = true
        )
        createTimetable(attrs)
        onView(withId(R.id.seventh_day_of_week)).check(matches(not(isDisplayed())))
        onView(withId(R.id.seventh_day)).check(matches(not(isDisplayed())))
        verify { utils.getVerticalLinesCoordinates(5, any(), any(), any()) }
    }

    @Test
    fun notShowSaturdayAndStartingSunday() {
        val attrs = createTimetableAttributeSetWithShowSaturday(
            showSaturday = false,
            isMondayFirstOfWeek = false
        )
        createTimetable(attrs)
        onView(withId(R.id.seventh_day_of_week)).check(matches(not(isDisplayed())))
        onView(withId(R.id.seventh_day)).check(matches(not(isDisplayed())))
        verify { utils.getVerticalLinesCoordinates(5, any(), any(), any()) }
    }

    @Test
    fun showSundayAndStartingMonday() {
        val attrs = createTimetableAttributeSetWithShowSunday(
            showSunday = true,
            isMondayFirstOfWeek = true
        )
        createTimetable(attrs)
        onView(withId(R.id.seventh_day_of_week)).check(matches(isDisplayed()))
        onView(withId(R.id.seventh_day)).check(matches(isDisplayed()))
        verify { utils.getVerticalLinesCoordinates(6, any(), any(), any()) }
    }

    @Test
    fun notShowSundayAndStartingMonday() {
        val attrs = createTimetableAttributeSetWithShowSunday(
            showSunday = false,
            isMondayFirstOfWeek = true
        )
        createTimetable(attrs)
        onView(withId(R.id.seventh_day_of_week)).check(matches(not(isDisplayed())))
        onView(withId(R.id.seventh_day)).check(matches(not(isDisplayed())))
        verify { utils.getVerticalLinesCoordinates(5, any(), any(), any()) }
    }

    private fun createTimetable(attr: AttributeSet? = null, showAsGrid: Boolean = true): Timetable {
        var timetable: Timetable? = null
        launchFragmentInHiltContainer<TestFragment> {
            val attributeSet = attr ?: Robolectric.buildAttributeSet().build()
            timetable = Timetable(this.requireContext(), attributeSet)
            val layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            timetable!!.layoutParams = layoutParams
            binding.fragmentTestContainer.addView(timetable)
            timetable!!.setShowAsGrid(showAsGrid)
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

    private fun mockCanvasRenderGetCurrentMonthDayBackground() {
        every {
            canvasRender.getCurrentMonthDayBackground(
                any(),
                any(),
                any()
            )
        } returns Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
    }

    private fun mockUtilsGetCurrentMonthDayString() {
        every { utils.getCurrentMonthDay() } returns "2"
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

    private fun mockUtilGetDaysOfMonthOfWeek() {
        every {
            utils.getDaysOfMonthOfWeek(
                any(),
                any()
            )
        } answers { if (arg(0)) fakeDaysOfMonthCurrentWeekStartingMonday else fakeDaysOfMonthCurrentWeekStartingSunday }
    }

    private fun mockUtilsGetDayOfWeekOrder() {
        every {
            utils.getDaysOfWeekOrder(
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

    private fun createTimetableAttributeSetMondayFirst(isMondayFirstOfWeek: Boolean): AttributeSet {
        val attr = Robolectric.buildAttributeSet()
        attr.addAttribute(R.attr.is_monday_first_day_of_week, isMondayFirstOfWeek.toString())
        return attr.build()
    }

    private fun createTimetableAttributeSetWithShowSaturday(
        showSaturday: Boolean,
        isMondayFirstOfWeek: Boolean
    ): AttributeSet {
        val attr = Robolectric.buildAttributeSet()
        attr.addAttribute(R.attr.show_saturday, showSaturday.toString())
        attr.addAttribute(R.attr.is_monday_first_day_of_week, isMondayFirstOfWeek.toString())
        return attr.build()
    }

    private fun createTimetableAttributeSetWithShowSunday(
        showSunday: Boolean,
        isMondayFirstOfWeek: Boolean
    ): AttributeSet {
        val attr = Robolectric.buildAttributeSet()
        attr.addAttribute(R.attr.show_sunday, showSunday.toString())
        attr.addAttribute(R.attr.is_monday_first_day_of_week, isMondayFirstOfWeek.toString())
        return attr.build()
    }

    private fun createTimetableAttributeSetWithDaysFont(
        @FontRes fontId: Int,
        isDayOfWeek: Boolean
    ): AttributeSet {
        val attrs = Robolectric.buildAttributeSet()
        val font =
            if (fontId == R.font.roboto_regular) "@font/roboto_regular" else "@font/roboto_medium"
        val attr = if (isDayOfWeek) R.attr.days_of_week_font else R.attr.days_of_month_font
        attrs.addAttribute(attr, font)
        return attrs.build()
    }

    private fun createTimetableAttributeSetWithTimeFormat(is12HoursFormat: Boolean): AttributeSet {
        val attrs = Robolectric.buildAttributeSet()
        attrs.addAttribute(R.attr.is_12_hours_format, is12HoursFormat.toString())
        return attrs.build()
    }

    private fun withTextColor(@ColorInt expectedTextColor: Int): Matcher<View> {
        return object : BoundedMatcher<View, TextView>(TextView::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("with textColor: ")
                description?.appendValue(expectedTextColor)
            }

            override fun matchesSafely(item: TextView?): Boolean {
                return item?.currentTextColor == expectedTextColor
            }
        }
    }

    private fun withBackground(@ColorInt expectedBackground: Drawable): Matcher<View> {
        return object : BoundedMatcher<View, TextView>(TextView::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("with background: ")
                description?.appendValue(expectedBackground)
            }

            override fun matchesSafely(item: TextView): Boolean {
                return item.background.toBitmap().sameAs(expectedBackground.toBitmap())
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
    }
}
