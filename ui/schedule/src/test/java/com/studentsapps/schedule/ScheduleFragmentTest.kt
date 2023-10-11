package com.studentsapps.schedule

import androidx.appcompat.app.AppCompatActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
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
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

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
        launchFragmentInHiltContainer<ScheduleFragment> {
            val expectedMonth = "July"
            val realMonth = (requireActivity() as AppCompatActivity).supportActionBar?.title
            assertThat(realMonth, `is`(expectedMonth))
        }
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
}