package com.studentsapps.schedule

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.studentsapps.schedule.timetable.TimetableUtils
import com.studentsapps.schedule.timetable.verifyTimetableGridViewIsDisplayed
import com.studentsapps.schedule.timetable.verifyTimetableListViewIsDisplayed
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.every
import io.mockk.spyk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.time.LocalDate


@Config(application = HiltTestApplication::class, sdk = [33])
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ScheduleFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    internal val timetableUtils = spyk<TimetableUtils>()

    @Test
    fun verifyCurrentMonthDisplayedInAppBar() {
        every { timetableUtils.getMonth(any()) } returns "July"
        val expectedMonth = "July"
        launchFragmentInHiltContainer<ScheduleFragment>()
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(expectedMonth))))
    }

    @Test
    fun verifyAppBarMenuOptionsDisplayed() {
        launchFragmentInHiltContainer<ScheduleFragment>()
        onView(withId(R.id.change_timetable_view)).check(matches(isDisplayed()))
    }

    @Test
    fun verifyTimetableViewsChange() {
        launchFragmentInHiltContainer<ScheduleFragment>()
        verifyTimetableGridViewIsDisplayed()
        onView(withId(R.id.change_timetable_view)).perform(click())
        verifyTimetableListViewIsDisplayed()
        onView(withId(R.id.change_timetable_view)).perform(click())
        verifyTimetableGridViewIsDisplayed()
    }

    @Test
    fun testCorrectMonthDisplayedOnCurrentWeekChange() {
        val expectedMonth = "September"
        every { timetableUtils.getCurrentDate() } returns LocalDate.of(2023, 8, 26)
        launchFragmentInHiltContainer<ScheduleFragment>()
        onView(withId(R.id.timetable)).perform(swipeLeft())
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(expectedMonth))))
    }
}