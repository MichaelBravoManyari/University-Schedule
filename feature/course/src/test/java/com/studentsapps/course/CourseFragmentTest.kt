package com.studentsapps.course

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.studentsapps.testing.launchFragmentInHiltContainer
import com.studentsapps.testing.util.MainDispatcherRule
import com.studentsapps.ui.CourseAdapter
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(application = HiltTestApplication::class, sdk = [33])
@ExperimentalCoroutinesApi
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CourseFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun testShowCourses() {
        launchFragmentInHiltContainer<CourseFragment>()
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
}