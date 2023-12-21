package com.studentsapps.schedule.fragments

import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.studentsapps.schedule.R
import com.studentsapps.testing.launchFragmentInHiltContainer
import com.studentsapps.testing.util.MainDispatcherRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(application = HiltTestApplication::class, sdk = [33])
@HiltAndroidTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RegisterScheduleFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.schedule_navigation)
    }

    @Test
    fun testInitialStateOfFragment() {
        launchFragmentInHiltContainer<RegisterScheduleFragment>(navigation = {
            Navigation.setViewNavController(requireView(), navController)
            navController.setCurrentDestination(R.id.registerScheduleFragment)
        })
        onView(withId(R.id.btn_day)).check(matches(withText(R.string.monday)))
        onView(withId(R.id.btn_start_hour)).check(matches(withText("9:00 AM")))
        onView(withId(R.id.btn_end_hour)).check(matches(withText("10:00 AM")))
        onView(withId(R.id.icon_hour_error)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.btn_repetition)).check(matches(withText(R.string.every_week)))
        onView(withId(R.id.icon_course_error)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.relative_layout_color)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.switch_existing_courses)).check(matches(isChecked()))
    }
}