package com.studentsapps.course.fragments

import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.studentsapps.course.R
import com.studentsapps.testing.launchFragmentInHiltContainer
import com.studentsapps.testing.util.MainDispatcherRule
import com.studentsapps.testing.util.withBackgroundTintList
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(application = HiltTestApplication::class, sdk = [33])
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

    @Test
    fun testFormIsEmptyIfCourseIdIsZero() {
        createRegisterCourseFragment(0)
        onView(withId(R.id.edit_text_course_name)).check(matches(withText("")))
        onView(withId(R.id.color_course)).check(matches(withBackgroundTintList(0)))
        onView(withId(R.id.edit_text_teacher_course)).check(matches(withText("")))
    }

    private fun createRegisterCourseFragment(courseId: Int) {
        val fragmentArgs = bundleOf("courseId" to courseId)
        launchFragmentInHiltContainer<RegisterCourseFragment>(fragmentArgs = fragmentArgs ,navigation = {
            Navigation.setViewNavController(requireView(), navController)
        })
    }
}