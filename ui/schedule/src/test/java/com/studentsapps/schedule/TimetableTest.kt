package com.studentsapps.schedule

import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
class TimetableTest {

    @Test
    @Config(qualifiers = "en")
    fun showDaysOfWeek_enStartingMonday() {
        createTimetable(null)
        verifyDayOfWeekTexts("M", "T", "W", "T", "F", "S", "S")
    }

    @Test
    @Config(qualifiers = "es")
    fun showDaysOfWeek_esStartingMonday() {
        createTimetable(null)
        verifyDayOfWeekTexts("L", "Ma", "Mi", "J", "V", "S", "D")
    }

    @Test
    @Config(qualifiers = "en")
    fun showDaysOfWeek_enStartingSunday() {
        val isMondayFirstOfWeek = false
        val attr = with(Robolectric.buildAttributeSet()) {
            addAttribute(R.attr.is_monday_first_day_of_week, isMondayFirstOfWeek.toString())
            build()
        }
        createTimetable(attr)
        verifyDayOfWeekTexts("S", "M", "T", "W", "T", "F", "S")
    }

    @Test
    @Config(qualifiers = "es")
    fun showDaysOfWeek_esStartingSunday() {
        val isMondayFirstOfWeek = false
        val attr = with(Robolectric.buildAttributeSet()) {
            addAttribute(R.attr.is_monday_first_day_of_week, isMondayFirstOfWeek.toString())
            build()
        }
        createTimetable(attr)
        verifyDayOfWeekTexts("D", "L", "Ma", "Mi", "J", "V", "S")
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

    private fun verifyDayOfWeekTexts(vararg dayTexts: String) {
        val dayOfWeekIds = listOf(
            R.id.start_day_of_week, R.id.second_day_of_week, R.id.third_day_of_week,
            R.id.fourth_day_of_week, R.id.fifth_day_of_week, R.id.sixth_day_of_week,
            R.id.seventh_day_of_week
        )

        dayOfWeekIds.forEachIndexed { index, viewId ->
            onView(withId(viewId))
                .check(matches(withText(dayTexts[index])))
                .check(matches(isDisplayed()))
        }
    }
}
