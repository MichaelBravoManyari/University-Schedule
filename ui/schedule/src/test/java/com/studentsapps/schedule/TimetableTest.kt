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

@RunWith(AndroidJUnit4::class)
class TimetableTest {

    @Test
    @Config(qualifiers = "en")
    fun showDaysOfWeek_enDefaultStartingDay() {
        createTimetable(null)
        verifyDayOfWeekTexts("M", "T", "W", "T", "F", "S", "S")
    }

    @Test
    @Config(qualifiers = "es")
    fun showDaysOfWeek_esDefaultStartingDay() {
        createTimetable(null)
        verifyDayOfWeekTexts("L", "Ma", "Mi", "J", "V", "S", "D")
    }

    @Test
    @Config(qualifiers = "en")
    fun showDaysOfWeek_enStartingMonday() {
        val isMondayFirstOfWeek = true
        val attr = createAttributeSet(isMondayFirstOfWeek)
        createTimetable(attr)
        verifyDayOfWeekTexts("M", "T", "W", "T", "F", "S", "S")
    }

    @Test
    @Config(qualifiers = "es")
    fun showDaysOfWeek_esStartingMonday() {
        val isMondayFirstOfWeek = true
        val attr = createAttributeSet(isMondayFirstOfWeek)
        createTimetable(attr)
        verifyDayOfWeekTexts("L", "Ma", "Mi", "J", "V", "S", "D")
    }

    @Test
    @Config(qualifiers = "en")
    fun showDaysOfWeek_enStartingSunday() {
        val isMondayFirstOfWeek = false
        val attr = createAttributeSet(isMondayFirstOfWeek)
        createTimetable(attr)
        verifyDayOfWeekTexts("S", "M", "T", "W", "T", "F", "S")
    }

    @Test
    @Config(qualifiers = "es")
    fun showDaysOfWeek_esStartingSunday() {
        val isMondayFirstOfWeek = false
        val attr = createAttributeSet(isMondayFirstOfWeek)
        createTimetable(attr)
        verifyDayOfWeekTexts("D", "L", "Ma", "Mi", "J", "V", "S")
    }

    @Test
    fun setTypefaceDaysOfWeek_defaultTypeface() {
        createTimetable(null)
        val typeface = createTypeface(R.font.roboto_medium)
        verifyTypeface(typeface)
    }

    @Test
    fun setTypefaceDaysOfWeek_otherTypeface() {
        val typeface = createTypeface(R.font.roboto_regular)
        val attr = createAttributeSet(typeface)
        createTimetable(attr)
        verifyTypeface(typeface)
    }

    @Test
    fun verifyDaysOfWeekTextSize_correctSize() {
        val daysOfWeekIds = getDaysOfWeekViewsIds()
        createTimetable(null)
        for (viewId in daysOfWeekIds) {
            onView(withId(viewId)).check(matches(withTextSize(4f)))
        }
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

    private fun verifyDayOfWeekTexts(vararg dayTexts: String) {
        val dayOfWeekIds = getDaysOfWeekViewsIds()

        dayOfWeekIds.forEachIndexed { index, viewId ->
            onView(withId(viewId))
                .check(matches(withText(dayTexts[index])))
                .check(matches(isDisplayed()))
        }
    }

    private fun verifyTypeface(typeface: Typeface) {
        val dayOfWeekIds = getDaysOfWeekViewsIds()
        for (viewId in dayOfWeekIds) {
            onView(withId(viewId)).check(matches(withTypeface(typeface)))
        }
    }

    private fun getDaysOfWeekViewsIds(): List<Int> {
        return listOf(
            R.id.start_day_of_week, R.id.second_day_of_week, R.id.third_day_of_week,
            R.id.fourth_day_of_week, R.id.fifth_day_of_week, R.id.sixth_day_of_week,
            R.id.seventh_day_of_week
        )
    }

    private fun withTypeface(typeface: Typeface): Matcher<View> {
        return TypefaceMatcher(typeface)
    }

    private fun withTextSize(expectedTextSize: Float): Matcher<View> {
        return TextSizeMatcher(expectedTextSize)
    }

    private fun texts(expectedTextSize: Float): Matcher<View> {
        return object : BoundedMatcher<View, TextView>(TextView::class.java){
            override fun describeTo(description: Description?) {
                TODO("Not yet implemented")
            }

            override fun matchesSafely(item: TextView?): Boolean {
                TODO("Not yet implemented")
            }

        }
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
}

class TypefaceMatcher(private val expectedTypeface: Typeface) :
    BoundedMatcher<View, TextView>(TextView::class.java) {
    override fun describeTo(description: Description?) {
        description?.appendText("with typeface: ")
        description?.appendValue(expectedTypeface)
    }

    override fun matchesSafely(item: TextView?): Boolean {
        return item?.typeface == expectedTypeface
    }
}

class TextSizeMatcher(private val expectedTextSize: Float) :
    BoundedMatcher<View, TextView>(TextView::class.java) {
    override fun describeTo(description: Description?) {
        description?.appendText("with textSize: ")
        description?.appendValue(expectedTextSize)
    }

    override fun matchesSafely(item: TextView?): Boolean {
        return item?.textSize == expectedTextSize
    }

}
