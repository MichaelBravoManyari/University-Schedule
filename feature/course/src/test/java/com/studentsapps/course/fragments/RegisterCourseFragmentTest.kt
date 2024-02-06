package com.studentsapps.course.fragments

import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.textfield.TextInputLayout
import com.studentsapps.course.R
import com.studentsapps.data.repository.fake.FakeCourseRepository
import com.studentsapps.testing.launchFragmentInHiltContainer
import com.studentsapps.testing.util.MainDispatcherRule
import com.studentsapps.testing.util.withBackgroundTintList
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.After
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
class RegisterCourseFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.course_navigation)
        navController.setCurrentDestination(R.id.registerCourseFragment)
    }

    @After
    fun clear() {
        FakeCourseRepository().restoreDatabase()
    }

    @Test
    fun testFormIsEmptyIfCourseIdIsZero() {
        createRegisterCourseFragment(0)
        onView(withId(R.id.edit_text_course_name)).check(matches(withText("")))
        onView(withId(R.id.color_course)).check(matches(withBackgroundTintList(0xffffff00.toInt())))
        onView(withId(R.id.edit_text_teacher_course)).check(matches(withText("")))
        onView(withId(com.studentsapps.ui.R.id.menu_add)).check(matches(withText(com.studentsapps.ui.R.string.add)))
    }

    @Test
    fun testFormDisplaysCourseDataForGivenCourseId() {
        createRegisterCourseFragment(1)
        onView(withId(R.id.edit_text_course_name)).check(matches(withText("Math")))
        onView(withId(R.id.color_course)).check(matches(withBackgroundTintList(1234)))
        onView(withId(R.id.edit_text_teacher_course)).check(matches(withText("")))
        onView(withId(com.studentsapps.ui.R.id.menu_add)).check(matches(withText(com.studentsapps.ui.R.string.update)))
    }

    @Test
    fun testSuccessfulCourseRegistrationNavigatesToCourseFragment() {
        createRegisterCourseFragment(0)
        onView(withId(R.id.edit_text_course_name)).perform(typeText("courseTest"))
        onView(withId(R.id.edit_text_teacher_course)).perform(typeText("teacherTest"))
        onView(withId(R.id.toolbar)).perform(click())
        onView(withId(com.studentsapps.ui.R.id.menu_add)).perform(click())
        assertEquals(R.id.courseFragment, navController.currentDestination?.id)
    }

    @Test
    fun testSuccessfulCourseUpdateNavigatesToCourseFragment() {
        createRegisterCourseFragment(1)
        onView(withId(R.id.edit_text_course_name)).perform(typeText("Math 1"))
        onView(withId(R.id.edit_text_teacher_course)).perform(typeText("Professor 1"))
        onView(withId(R.id.toolbar)).perform(click())
        onView(withId(com.studentsapps.ui.R.id.menu_add)).perform(click())
        assertEquals(R.id.courseFragment, navController.currentDestination?.id)
    }

    @Test
    fun testErrorIconDisplayedForInvalidCourseName() {
        createRegisterCourseFragment(0)
        onView(withId(R.id.edit_text_course_name)).perform(typeText(""))
        onView(withId(R.id.toolbar)).perform(click())
        onView(withId(com.studentsapps.ui.R.id.menu_add)).perform(click())
        onView(withId(R.id.text_input_layout_course_name)).check(matches(isEndIconDrawableNotNull()))
    }

    private fun createRegisterCourseFragment(courseId: Int) {
        val fragmentArgs = bundleOf("courseId" to courseId)
        launchFragmentInHiltContainer<RegisterCourseFragment>(
            fragmentArgs = fragmentArgs,
            navigation = {
                Navigation.setViewNavController(requireView(), navController)
            })
    }

    private fun isEndIconDrawableNotNull(): Matcher<View> {
        return object : BoundedMatcher<View, TextInputLayout>(TextInputLayout::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("with endIconDrawable is: ")
            }

            override fun matchesSafely(item: TextInputLayout): Boolean {
                return item.endIconDrawable != null
            }
        }
    }
}