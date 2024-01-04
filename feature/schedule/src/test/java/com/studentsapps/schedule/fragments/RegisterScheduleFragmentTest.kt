package com.studentsapps.schedule.fragments

import androidx.lifecycle.ViewModelStore
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.textview.MaterialTextView
import com.studentsapps.schedule.R
import com.studentsapps.testing.launchFragmentInHiltContainer
import com.studentsapps.testing.util.MainDispatcherRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog

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
        navController.setViewModelStore(ViewModelStore())
        navController.setGraph(R.navigation.schedule_navigation)
        launchFragmentInHiltContainer<RegisterScheduleFragment>(navigation = {
            Navigation.setViewNavController(requireView(), navController)
            navController.setLifecycleOwner(this)
            navController.setCurrentDestination(R.id.registerScheduleFragment)
        })
    }

    @Test
    fun testInitialStateOfFragment() {
        onView(withId(R.id.btn_day)).check(matches(withText(R.string.monday)))
        onView(withId(R.id.btn_start_hour)).check(matches(withText("9:00 AM")))
        onView(withId(R.id.btn_end_hour)).check(matches(withText("10:00 AM")))
        onView(withId(R.id.icon_hour_error)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.btn_repetition)).check(matches(withText(R.string.every_week)))
        onView(withId(R.id.icon_course_error)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.relative_layout_color)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.switch_existing_courses)).check(matches(isChecked()))
    }

    @Test
    fun testClickDayButtonNavigatesToWeekDaysDestination() {
        onView(withId(R.id.btn_day)).perform(click())
        assertEquals(R.id.modalBottomSheetDay, navController.currentDestination?.id)
    }

    @Test
    fun testStartTimeButtonShowsTimePicker() {
        onView(withId(R.id.btn_start_hour)).perform(click())
        val dialog = ShadowDialog.getLatestDialog()
        assertTrue(dialog.isShowing)
    }

    @Test
    fun testEndTimeButtonShowsTimePicker() {
        onView(withId(R.id.btn_end_hour)).perform(click())
        onView(withText(R.string.select_hour)).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withText("OK")).inRoot(isDialog()).perform(click())
    }

    @Test
    fun testClickRepeatButtonNavigatesToRepeatDestination() {
        onView(withId(R.id.btn_repetition)).perform(click())
        assertEquals(R.id.modalBottomSheetRepetition, navController.currentDestination?.id)
    }

    @Test
    fun testClickCourseSectionNavigatesToCourseSelectionDestination() {
        onView(withId(R.id.selected_course_section)).perform(click())
        assertEquals(R.id.modalBottomSheetCourse, navController.currentDestination?.id)
    }

    @Test
    fun testDisableExistingResourcesShowsChooseColorOption() {
        onView(withId(R.id.text_input_layout_course)).check(
            matches(
                withEffectiveVisibility(
                    Visibility.INVISIBLE
                )
            )
        )
        onView(withId(R.id.selected_course_section)).check(
            matches(
                withEffectiveVisibility(
                    Visibility.VISIBLE
                )
            )
        )
        onView(withId(R.id.switch_existing_courses)).perform(click())
        onView(withId(R.id.relative_layout_color)).check(matches(isDisplayed()))
        onView(withId(R.id.text_input_layout_course)).check(
            matches(
                withEffectiveVisibility(
                    Visibility.VISIBLE
                )
            )
        )
        onView(withId(R.id.selected_course_section)).check(
            matches(
                withEffectiveVisibility(
                    Visibility.INVISIBLE
                )
            )
        )
    }

    @Test
    fun testShowErrorIconForTimeError() {
        onView(withId(R.id.btn_end_hour)).perform(click())
        // In the timepicker
        onView(allOf(withText("8"))).inRoot(isDialog()).perform(click())
        onView(
            allOf(
                withText("00"), withClassName(equalTo(MaterialTextView::class.java.name))
            )
        ).inRoot(isDialog()).perform(
            click()
        )
        onView(withText("AM")).inRoot(isDialog()).perform(click())
        onView(withText("OK")).inRoot(isDialog()).perform(click())
        // In this fragment
        onView(withId(R.id.toolbar)).perform(click())
        onView(withId(com.studentsapps.ui.R.id.menu_add)).perform(click())
        onView(withId(R.id.icon_hour_error)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun testClickAddScheduleButtonNavigatesToScheduleFragment() {
        onView(withId(R.id.switch_existing_courses)).perform(click())
        onView(withId(R.id.edit_text_course)).perform(typeText("Math"))
        onView(withId(com.studentsapps.ui.R.id.menu_add)).perform(click())
        assertEquals(R.id.scheduleFragment, navController.currentDestination?.id)
    }
}