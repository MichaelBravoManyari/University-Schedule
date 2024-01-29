package com.studentsapps.course.fragments

import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.studentsapps.course.R
import com.studentsapps.testing.launchFragmentInHiltContainer
import com.studentsapps.testing.util.MainDispatcherRule
import com.studentsapps.ui.CourseAdapter
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@Config(application = HiltTestApplication::class)
@ExperimentalCoroutinesApi
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CourseFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.course_navigation)
    }

    @Test
    fun testShowCourses() {
        createCourseFragment()

        onView(withId(R.id.recycler_view_course)).perform(
            RecyclerViewActions.scrollTo<CourseAdapter.CourseViewHolder>(hasDescendant(withText("Math")))
        )
        onView(withText("Math")).check(matches(isDisplayed()))

        onView(withId(R.id.recycler_view_course)).perform(
            RecyclerViewActions.scrollTo<CourseAdapter.CourseViewHolder>(hasDescendant(withText("History")))
        )
        onView(withText("History")).check(matches(isDisplayed()))

        onView(withId(R.id.recycler_view_course)).perform(
            RecyclerViewActions.scrollTo<CourseAdapter.CourseViewHolder>(hasDescendant(withText("Sciences")))
        )
        onView(withText("Sciences")).check(matches(isDisplayed()))

        onView(withId(R.id.recycler_view_course)).perform(
            RecyclerViewActions.scrollTo<CourseAdapter.CourseViewHolder>(hasDescendant(withText("Statistics")))
        )
        onView(withText("Statistics")).check(matches(isDisplayed()))
    }

    @Test
    fun testNavigateToCourseRegistrationScreen() {
        createCourseFragment()
        onView(withId(R.id.fab_add_course)).perform(click())
        assertEquals(R.id.registerCourseFragment, navController.currentDestination?.id)
    }

    @Test
    fun testNavigateToCourseRegistrationScreenWithCorrectCourseIdOnClick() {
        createCourseFragment()
        onView(withId(R.id.recycler_view_course)).perform(
            RecyclerViewActions.scrollTo<CourseAdapter.CourseViewHolder>(hasDescendant(withText("Math")))
        )
        onView(withText("Math")).perform(click())
        assertEquals(R.id.registerCourseFragment, navController.currentDestination?.id)
    }

    private fun createCourseFragment() {
        launchFragmentInHiltContainer<CourseFragment>(navigation = {
            Navigation.setViewNavController(requireView(), navController)
        })
    }
}