package com.studentsapps.schedule.fragments

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.studentsapps.data.repository.fake.FakeTimetableUserPreferencesRepository
import com.studentsapps.testing.launchFragmentInHiltContainer
import com.studentsapps.testing.util.MainDispatcherRule
import com.studentsapps.schedule.R
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(application = HiltTestApplication::class, sdk = [33])
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ScheduleConfigurationFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @ExperimentalCoroutinesApi
    @get:Rule(order = 1)
    val mainDispatcherRule = MainDispatcherRule()

    @BindValue
    val fakeTimetableUserPreferencesRepository = FakeTimetableUserPreferencesRepository()

    @Before
    fun setup() {
        fakeTimetableUserPreferencesRepository.init()
    }

    @Test
    fun checkInitialStateOfScheduleConfigurationScreen() {
        launchFragmentInHiltContainer<ScheduleConfigurationFragment>()
        onView(withId(R.id.first_day_of_week_value)).check(matches(withText("Monday")))
        onView(withId(R.id.hour_format_value)).check(matches(withText("12 hours")))
        onView(withId(R.id.saturday_display_switch)).check(matches(isChecked()))
        onView(withId(R.id.sunday_display_switch)).check(matches(isChecked()))
    }

    @Test
    fun verifyFirstDayOfWeekClickUpdates() {
        launchFragmentInHiltContainer<ScheduleConfigurationFragment>()
        onView(withId(R.id.container_for_first_day_of_the_week)).perform(click())
        onView(withId(R.id.first_day_of_week_value)).check(matches(withText("Sunday")))
        onView(withId(R.id.container_for_first_day_of_the_week)).perform(click())
        onView(withId(R.id.first_day_of_week_value)).check(matches(withText("Monday")))
    }

    @Test
    fun verifyTimeFormatClickUpdates() {
        launchFragmentInHiltContainer<ScheduleConfigurationFragment>()
        onView(withId(R.id.time_format_container)).perform(click())
        onView(withId(R.id.hour_format_value)).check(matches(withText("24 hours")))
        onView(withId(R.id.time_format_container)).perform(click())
        onView(withId(R.id.hour_format_value)).check(matches(withText("12 hours")))
    }

    @Test
    fun verifySaturdayDisplaySwitchClickUpdates() {
        launchFragmentInHiltContainer<ScheduleConfigurationFragment>()
        onView(withId(R.id.saturday_display_switch)).perform(click())
        onView(withId(R.id.saturday_display_switch)).check(matches(not(isChecked())))
        onView(withId(R.id.saturday_display_switch)).perform(click())
        onView(withId(R.id.saturday_display_switch)).check(matches(isChecked()))
    }

    @Test
    fun verifySundayDisplaySwitchClickUpdates() {
        launchFragmentInHiltContainer<ScheduleConfigurationFragment>()
        onView(withId(R.id.sunday_display_switch)).perform(click())
        onView(withId(R.id.sunday_display_switch)).check(matches(not(isChecked())))
        onView(withId(R.id.sunday_display_switch)).perform(click())
        onView(withId(R.id.sunday_display_switch)).check(matches(isChecked()))
    }
}