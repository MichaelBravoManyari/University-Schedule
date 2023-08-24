package com.studentsapps.schedule

import android.app.Application
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
class TimetableTest {

    @Test
    @Config(qualifiers = "en")
    fun showDaysOfWeek_enDefaultStartingDay() {
        createTimetable(null)
        verifyDaysOfWeekTexts("M", "T", "W", "T", "F", "S", "S")
    }

    @Test
    @Config(qualifiers = "es")
    fun showDaysOfWeek_esDefaultStartingDay() {
        createTimetable(null)
        verifyDaysOfWeekTexts("L", "Ma", "Mi", "J", "V", "S", "D")
    }

    @Test
    @Config(qualifiers = "en")
    fun showDaysOfWeek_enStartingMonday() {
        val isMondayFirstOfWeek = true
        val attr = createAttributeSet(isMondayFirstOfWeek)
        createTimetable(attr)
        verifyDaysOfWeekTexts("M", "T", "W", "T", "F", "S", "S")
    }

    @Test
    @Config(qualifiers = "es")
    fun showDaysOfWeek_esStartingMonday() {
        val isMondayFirstOfWeek = true
        val attr = createAttributeSet(isMondayFirstOfWeek)
        createTimetable(attr)
        verifyDaysOfWeekTexts("L", "Ma", "Mi", "J", "V", "S", "D")
    }

    @Test
    @Config(qualifiers = "en")
    fun showDaysOfWeek_enStartingSunday() {
        val isMondayFirstOfWeek = false
        val attr = createAttributeSet(isMondayFirstOfWeek)
        createTimetable(attr)
        verifyDaysOfWeekTexts("S", "M", "T", "W", "T", "F", "S")
    }

    @Test
    @Config(qualifiers = "es")
    fun showDaysOfWeek_esStartingSunday() {
        val isMondayFirstOfWeek = false
        val attr = createAttributeSet(isMondayFirstOfWeek)
        createTimetable(attr)
        verifyDaysOfWeekTexts("D", "L", "Ma", "Mi", "J", "V", "S")
    }

    @Test
    fun setTypefaceDaysOfWeek_defaultTypeface() {
        createTimetable(null)
        val expectedTypeface = createTypeface(R.font.roboto_medium)
        verifyTypeface(expectedTypeface)
    }

    @Test
    fun setTypefaceDaysOfWeek_otherTypeface() {
        val typeface = createTypeface(R.font.roboto_regular)
        val attr = createAttributeSet(typeface)
        createTimetable(attr)
        verifyTypeface(typeface)
    }

    @Test
    fun setTextSizeDaysOfWeek_correctSize() {
        val daysOfWeekIds = getDaysOfWeekViewsIds()
        createTimetable(null)
        verifyDaysOfWeekTextSize(daysOfWeekIds)
    }

    // Mal formulado
    @Test
    fun showDaysOfMonthCurrentWeek_defaultStartingMonday() {
        val expectedDaysOfMonthCurrentWeek = getDaysOfMonthCurrentWeek()
        val daysOfMonthViewsIds = getDaysOfMonthViewsIds()
        createTimetable(null)
        verifyDaysOfMonthCurrentWeek(daysOfMonthViewsIds, expectedDaysOfMonthCurrentWeek)
    }

    @Test
    fun showDaysOfMonthCurrentWeek_startingSunday() {
        val expectedDaysOfMonthCurrentWeek = getDaysOfMonthCurrentWeek(false)
        val daysOfMonthViewsIds = getDaysOfMonthViewsIds()
        val attr = createAttributeSet(false)
        createTimetable(attr)
        verifyDaysOfMonthCurrentWeek(daysOfMonthViewsIds, expectedDaysOfMonthCurrentWeek)
    }

    private fun createTimetable(attr: AttributeSet?) {
        launchFragmentInContainer<TestFragment>().onFragment { fragment ->
            val attributeSet = attr ?: Robolectric.buildAttributeSet().build()
            val timetable = Timetable(fragment.requireContext(), attributeSet)
            val layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            timetable.layoutParams = layoutParams
            fragment.binding.root.addView(timetable)
        }
    }

    private fun createTypeface(@FontRes fontId: Int): Typeface {
        val context = ApplicationProvider.getApplicationContext<Application>()
        return ResourcesCompat.getFont(context, fontId)!!
    }

    private fun verifyDaysOfWeekTextSize(daysOfWeekIds: List<Int>, expectedTextSize: Float = 4f) {
        for (viewId in daysOfWeekIds) {
            onView(withId(viewId)).check(matches(withTextSize(expectedTextSize)))
        }
    }

    private fun verifyDaysOfWeekTexts(vararg expectedDayTexts: String) {
        val dayOfWeekIds = getDaysOfWeekViewsIds()

        dayOfWeekIds.forEachIndexed { index, viewId ->
            onView(withId(viewId))
                .check(matches(withText(expectedDayTexts[index])))
                .check(matches(isDisplayed()))
        }
    }

    private fun verifyDaysOfMonthCurrentWeek(
        daysOfMonthViewsIds: List<Int>,
        daysOfMonthCurrentWeek: List<String>
    ) {
        daysOfMonthViewsIds.forEachIndexed { index, viewId ->
            onView(withId(viewId))
                .check(matches(withText(daysOfMonthCurrentWeek[index])))
                .check(matches(isDisplayed()))
        }
    }

    private fun verifyTypeface(expectedTypeface: Typeface) {
        val dayOfWeekIds = getDaysOfWeekViewsIds()
        for (viewId in dayOfWeekIds) {
            onView(withId(viewId)).check(matches(withTypeface(expectedTypeface)))
        }
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

    private fun createAttributeSet(typeface: Typeface): AttributeSet {
        val attr = Robolectric.buildAttributeSet()
        attr.addAttribute(R.attr.days_of_week_font, typeface.toString())
        return attr.build()
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

    private fun getDaysOfMonthCurrentWeek(isMondayFirstDayOfWeek: Boolean = true): List<String> {
        val formatter = DateTimeFormatter.ofPattern("d")
        val date: LocalDate = LocalDate.now()
        val startOfWeek =
            if (isMondayFirstDayOfWeek) date.with(DayOfWeek.MONDAY) else date.with(DayOfWeek.MONDAY)
                .minusDays(1)

        return (0 until 7).map { startOfWeek.plusDays(it.toLong()).format(formatter).toString() }
    }
}
