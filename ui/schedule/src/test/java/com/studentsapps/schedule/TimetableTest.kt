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
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.impl.annotations.SpyK
import io.mockk.verify
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Before
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
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @MockK
    lateinit var dateUtils: TimetableDateUtils

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every { dateUtils.getDaysOfWeekOrder(any()) } returns daysOfWeek
        every { dateUtils.getDaysOfMonthCurrentWeek(any()) } returns daysOfMonthCurrentWeek
    }

    @Test
    fun setTypefaceDaysOfWeek_defaultTypeface() {
        val daysOfMonthViewsIds = getDaysOfWeekViewsIds()
        val expectedTypeface = getTypeface(R.font.roboto_medium)
        createTimetable()
        verifyTypeface(daysOfMonthViewsIds, expectedTypeface)
    }

    @Test
    fun setTypefaceDaysOfWeek_otherTypeface() {
        val fontId = R.font.roboto_regular
        val expectedTypeface = getTypeface(fontId)
        val daysOfMonthViewsIds = getDaysOfWeekViewsIds()
        val attr = createAttributeSet(fontId)
        createTimetable(attr)
        verifyTypeface(daysOfMonthViewsIds, expectedTypeface)
    }

    @Test
    fun setTextSizeDaysOfWeek_correctSize() {
        val daysOfWeekViewsIds = getDaysOfWeekViewsIds()
        createTimetable()
        verifyTextSize(daysOfWeekViewsIds, 4f)
    }

    @Test
    @Config(qualifiers = "en")
    fun showDaysOfWeek_enDefaultStartingDay() {
        val isMondayFirstOfWeek = true
        createTimetable()
        verifyDaysOfWeekTexts("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
        verify { dateUtils.getDaysOfWeekOrder(isMondayFirstOfWeek) }
    }

    @Test
    @Config(qualifiers = "es")
    fun showDaysOfWeek_esDefaultStartingDay() {
        val isMondayFirstOfWeek = true
        createTimetable()
        verifyDaysOfWeekTexts("Lu", "Ma", "Mi", "Ju", "Vi", "Sa", "Do")
        verify { dateUtils.getDaysOfWeekOrder(isMondayFirstOfWeek) }
    }

    @Test
    @Config(qualifiers = "en")
    fun showDaysOfWeek_enStartingMonday() {
        val isMondayFirstOfWeek = true
        val attr = createAttributeSet(isMondayFirstOfWeek)
        createTimetable(attr)
        verifyDaysOfWeekTexts("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
        verify { dateUtils.getDaysOfWeekOrder(isMondayFirstOfWeek) }
    }

    @Test
    @Config(qualifiers = "es")
    fun showDaysOfWeek_esStartingMonday() {
        val isMondayFirstOfWeek = true
        val attr = createAttributeSet(isMondayFirstOfWeek)
        createTimetable(attr)
        verifyDaysOfWeekTexts("Lu", "Ma", "Mi", "Ju", "Vi", "Sa", "Do")
        verify { dateUtils.getDaysOfWeekOrder(isMondayFirstOfWeek) }
    }

    @Test
    @Config(qualifiers = "en")
    fun showDaysOfWeek_enStartingSunday() {
        val isMondayFirstOfWeek = false
        val attr = createAttributeSet(isMondayFirstOfWeek)
        createTimetable(attr)
        verifyDaysOfWeekTexts("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
    }

    @Test
    @Config(qualifiers = "es")
    fun showDaysOfWeek_esStartingSunday() {
        val isMondayFirstOfWeek = false
        val attr = createAttributeSet(isMondayFirstOfWeek)
        createTimetable(attr)
        verifyDaysOfWeekTexts("Do", "Lu", "Ma", "Mi", "Ju", "Vi", "Sa")
    }

    @Test
    fun setTextSizeDaysOfMonth_correctSize() {
        val daysOfMonthViewsIds = getDaysOfMonthViewsIds()
        createTimetable()
        verifyTextSize(daysOfMonthViewsIds, 6f)
    }

    @Test
    fun setTypefaceDaysOfMonth_defaultTypeface() {
        val daysOfMonthViewsIds = getDaysOfMonthViewsIds()
        val expectedTypeface = getTypeface(R.font.roboto_regular)
        createTimetable()
        verifyTypeface(daysOfMonthViewsIds, expectedTypeface)
    }

    @Test
    fun setTypefaceDaysOfMonth_otherTypeface() {
        val fontId = R.font.roboto_medium
        val expectedTypeface = getTypeface(fontId)
        val daysOfMonthViewsIds = getDaysOfWeekViewsIds()
        val attr = createAttributeSet(fontId)
        createTimetable(attr)
        verifyTypeface(daysOfMonthViewsIds, expectedTypeface)
    }

    // Mal formulado
    @Test
    fun showDaysOfMonthCurrentWeek_defaultStartingMonday() {
        val expectedDaysOfMonthCurrentWeek = listOf("1", "2", "3", "4", "5", "6", "7")
        val isMondayFirstOfWeek = true
        val daysOfMonthViewsIds = getDaysOfMonthViewsIds()
        describeGetDaysOfMonthCurrentWeekBehavior(
            isMondayFirstOfWeek,
            expectedDaysOfMonthCurrentWeek
        )
        createTimetable()
        verifyDaysOfMonthCurrentWeek(daysOfMonthViewsIds, expectedDaysOfMonthCurrentWeek)
        verify {
            dateUtils.getDaysOfMonthCurrentWeek(isMondayFirstOfWeek)
        }
    }

    private fun describeGetDaysOfMonthCurrentWeekBehavior(
        isMondayFirstOfWeek: Boolean,
        daysOfMonthCurrentWeek: List<String>
    ) {
        MockKAnnotations.init(this)
        every { dateUtils.getDaysOfMonthCurrentWeek(isMondayFirstOfWeek) } returns daysOfMonthCurrentWeek
        every { dateUtils.getDaysOfWeekOrder(any()) } returns listOf(
            R.string.monday_abbr,
            R.string.tuesday_abbr,
            R.string.wednesday_abbr,
            R.string.thursday_abbr,
            R.string.friday_abbr,
            R.string.saturday_abbr,
            R.string.sunday_abbr
        )
    }

    @Test
    fun showDaysOfMonthCurrentWeek_startingSunday() {
        /*val expectedDaysOfMonthCurrentWeek = getDaysOfMonthCurrentWeek(false)
        val daysOfMonthViewsIds = getDaysOfMonthViewsIds()
        val attr = createAttributeSet(false)
        createTimetable(attr)
        verifyDaysOfMonthCurrentWeek(daysOfMonthViewsIds, expectedDaysOfMonthCurrentWeek)*/
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

    private fun getTypeface(@FontRes fontId: Int): Typeface {
        return ResourcesCompat.getFont(ApplicationProvider.getApplicationContext(), fontId)!!
    }

    private fun verifyTextSize(viewsIds: List<Int>, expectedTextSize: Float) {
        for (viewId in viewsIds) {
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

    private fun verifyTypeface(viewsIds: List<Int>, expectedTypeface: Typeface) {
        for (viewId in viewsIds) {
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

    private fun createAttributeSet(@FontRes fontId: Int): AttributeSet {
        val attr = Robolectric.buildAttributeSet()
        val font =
            if (fontId == R.font.roboto_regular) "@font/roboto_regular" else "@font/roboto_medium"
        attr.addAttribute(R.attr.days_of_week_font, font)
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

    companion object {
        val daysOfWeek = listOf(
            R.string.monday_abbr,
            R.string.tuesday_abbr,
            R.string.wednesday_abbr,
            R.string.thursday_abbr,
            R.string.friday_abbr,
            R.string.saturday_abbr,
            R.string.sunday_abbr
        )

        val daysOfMonthCurrentWeek = listOf("1", "2", "3", "4", "5", "6", "7")
    }
}
