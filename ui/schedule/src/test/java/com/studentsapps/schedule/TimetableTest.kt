package com.studentsapps.schedule

import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.every
import io.mockk.spyk
import org.hamcrest.Description
import org.hamcrest.Matcher
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
        val attr = createAttributeSet(fontId, true)
        createTimetable(attr)
        verifyDaysOfWeekTypeface(expectedTypeface)
    }

    @Test
    fun setTypefaceDaysOfMonth_otherTypeface() {
        val fontId = R.font.roboto_medium
        val expectedTypeface = getTypeface(fontId)
        val attr = createAttributeSet(fontId, false)
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
        val attr = createAttributeSet(isMondayFirstOfWeek)
        createTimetable(attr)
        verifyDaysOfWeekTexts("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
    }

    @Test
    fun showDaysOfMonthCurrentWeek_startingMonday() {
        mockUtilGetDaysOfMonthOfWeek()
        val isMondayFirstOfWeek = true
        val attributeSet = createAttributeSet(isMondayFirstOfWeek)
        createTimetable(attributeSet)
        verifyDaysOfMonthCurrentWeekTexts("2", "3", "4", "5", "6", "7", "8")
    }

    @Test
    fun showDaysOfWeek_startingSunday() {
        mockUtilsGetDayOfWeekOrder()
        val isMondayFirstOfWeek = false
        val attr = createAttributeSet(isMondayFirstOfWeek)
        createTimetable(attr)
        verifyDaysOfWeekTexts("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
    }

    @Test
    fun showDaysOfMonthCurrentWeek_startingSunday() {
        mockUtilGetDaysOfMonthOfWeek()
        val isMondayFirstOfWeek = false
        val attributeSet = createAttributeSet(isMondayFirstOfWeek)
        createTimetable(attributeSet)
        verifyDaysOfMonthCurrentWeekTexts("1", "2", "3", "4", "5", "6", "7")
    }

    private fun createTimetable(attr: AttributeSet? = null) {
        launchFragmentInHiltContainer<TestFragment> {
            val attributeSet = attr ?: Robolectric.buildAttributeSet().build()
            val timetable = Timetable(this.requireContext(), attributeSet)
            val layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            timetable.layoutParams = layoutParams
            val testFragment = this as TestFragment
            testFragment.binding.root.addView(timetable)
        }
    }

    private fun mockUtilGetDaysOfMonthOfWeek() {
        every { utils.getDaysOfMonthOfWeek(any()) } answers { if (arg(0)) fakeDaysOfMonthCurrentWeekStartingMonday else fakeDaysOfMonthCurrentWeekStartingSunday }
    }

    private fun mockUtilsGetDayOfWeekOrder() {
        every { utils.getDaysOfWeekOrder(any()) } answers { if (arg(0)) fakeDaysOfWeekStartingMonday else fakeDaysOfWeekStartingSunday }
    }

    private fun getTypeface(@FontRes fontId: Int): Typeface {
        return ResourcesCompat.getFont(ApplicationProvider.getApplicationContext(), fontId)!!
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

    private fun createAttributeSet(isMondayFirstOfWeek: Boolean): AttributeSet {
        val attr = Robolectric.buildAttributeSet()
        attr.addAttribute(R.attr.is_monday_first_day_of_week, isMondayFirstOfWeek.toString())
        return attr.build()
    }

    private fun createAttributeSet(@FontRes fontId: Int, isDayOfWeek: Boolean): AttributeSet {
        val attrs = Robolectric.buildAttributeSet()
        val font =
            if (fontId == R.font.roboto_regular) "@font/roboto_regular" else "@font/roboto_medium"
        val attr = if (isDayOfWeek) R.attr.days_of_week_font else R.attr.days_of_month_font
        attrs.addAttribute(attr, font)
        return attrs.build()
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
        val fakeDaysOfWeekStartingMonday = listOf(
            R.string.monday_abbr_test,
            R.string.tuesday_abbr_test,
            R.string.wednesday_abbr_test,
            R.string.thursday_abbr_test,
            R.string.friday_abbr_test,
            R.string.saturday_abbr_test,
            R.string.sunday_abbr_test
        )

        val fakeDaysOfWeekStartingSunday = listOf(
            R.string.sunday_abbr_test,
            R.string.monday_abbr_test,
            R.string.tuesday_abbr_test,
            R.string.wednesday_abbr_test,
            R.string.thursday_abbr_test,
            R.string.friday_abbr_test,
            R.string.saturday_abbr_test
        )

        val fakeDaysOfMonthCurrentWeekStartingMonday = listOf("2", "3", "4", "5", "6", "7", "8")

        val fakeDaysOfMonthCurrentWeekStartingSunday = listOf("1", "2", "3", "4", "5", "6", "7")
    }
}
