package com.studentsapps.schedule.fragments

import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openContextualActionModeOverflowMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.studentsapps.data.repository.fake.FakeTimetableUserPreferencesRepository
import com.studentsapps.schedule.R
import com.studentsapps.testing.launchFragmentInHiltContainer
import com.studentsapps.testing.util.MainDispatcherRule
import com.studentsapps.testing.util.withTextColor
import com.studentsapps.ui.timetable.TimetableListAdapter
import com.studentsapps.ui.timetable.TimetableUtils
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.every
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.time.LocalDate


@Config(application = HiltTestApplication::class)
@HiltAndroidTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ScheduleFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val mainDispatcherRule = MainDispatcherRule()

    @BindValue
    val timetableUtils = spyk<TimetableUtils>()

    @BindValue
    val fakeTimetableUserPreferencesRepository = FakeTimetableUserPreferencesRepository()

    private lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.schedule_navigation)
    }

    @Test
    fun verifyCurrentMonthDisplayedInAppBar() {
        every { timetableUtils.getMonth(any()) } returns "July"
        val expectedMonth = "July"
        createScheduleFragment()
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(expectedMonth))))
    }

    @Test
    fun verifyAppBarMenuOptionsDisplayed() {
        createScheduleFragment()
        onView(withId(R.id.change_timetable_view)).check(matches(isDisplayed()))
    }

    @Test
    fun verifyTimetableViewsChange() = runTest {
        createScheduleFragment()
        fakeTimetableUserPreferencesRepository.init()
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
        createScheduleFragment()
        onView(withId(R.id.timetable)).perform(swipeLeft())
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(expectedMonth))))
    }

    @Test
    fun verifyNavigationToTimetableSettingsOnActionClick() {
        createScheduleFragment()
        openContextualActionModeOverflowMenu()
        onView(withText(R.string.configuration)).perform(click())
        assertThat(navController.currentDestination?.id, `is`(R.id.scheduleConfigurationFragment))
    }

    @Test
    fun testClickOnTodayOptionSelectsCurrentDay() {
        mockUtilsGetCurrentDate(LocalDate.of(2023, 11, 13))
        val expectedTextColor = ContextCompat.getColor(
            ApplicationProvider.getApplicationContext(),
            com.studentsapps.ui.R.color.timetable_current_month_day_text_color
        )
        createScheduleFragment()
        onView(withId(R.id.timetable)).perform(swipeLeft())
        openContextualActionModeOverflowMenu()
        onView(withText(R.string.today)).perform(click())
        onView(withId(com.studentsapps.ui.R.id.first_day)).check(
            matches(
                withTextColor(
                    expectedTextColor
                )
            )
        )
    }

    @Test
    fun testRepeatedSchedulesDisplayedInGridMode() = runTest {
        mockUtilsGetCurrentDate(LocalDate.of(2023, 11, 20))
        fakeTimetableUserPreferencesRepository.init()
        createScheduleFragment()
        onView(withContentDescription("8")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("9")).perform(scrollTo()).check(matches(isDisplayed()))
    }

    @Test
    fun testSchedulesDisplayedInGridModeForSpecificDate() = runTest {
        mockUtilsGetCurrentDate(LocalDate.of(2023, 11, 20))
        fakeTimetableUserPreferencesRepository.init()
        createScheduleFragment()
        onView(withContentDescription("1")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("2")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("3")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("4")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("5")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("6")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("7")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("8")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("9")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("10")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("11")).check(doesNotExist())
    }

    @Test
    fun testChangeDateInGridModeDisplaysSchedulesForCurrentDate() = runTest {
        mockUtilsGetCurrentDate(LocalDate.of(2023, 11, 20))
        fakeTimetableUserPreferencesRepository.init()
        createScheduleFragment()
        onView(withId(R.id.timetable)).perform(swipeRight())
        onView(withContentDescription("1")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("2")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("3")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("4")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("5")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("6")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("7")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("8")).check(doesNotExist())
        onView(withContentDescription("9")).check(doesNotExist())
        onView(withContentDescription("10")).check(doesNotExist())
        onView(withContentDescription("11")).perform(scrollTo()).check(matches(isDisplayed()))
    }

    @Test
    fun testCheckSchedulesDisplayedInListModeForSpecificDate() = runTest {
        mockUtilsGetCurrentDate(LocalDate.of(2023, 11, 20))
        fakeTimetableUserPreferencesRepository.setShowAsGrid(false)
        fakeTimetableUserPreferencesRepository.init()
        createScheduleFragment()
        verifyTimetableListViewIsDisplayed()
        onView(withId(com.studentsapps.ui.R.id.schedule_list_container)).perform(
            RecyclerViewActions.scrollTo<TimetableListAdapter.TimetableListViewHolder>(
                hasDescendant(withText("Math"))
            )
        )
        onView(withText("Math")).check(matches(isDisplayed()))
        onView(withId(com.studentsapps.ui.R.id.schedule_list_container)).perform(
            RecyclerViewActions.scrollTo<TimetableListAdapter.TimetableListViewHolder>(
                hasDescendant(withText("Math7"))
            )
        )
        onView(withText("Math7")).check(matches(isDisplayed()))
        onView(withId(com.studentsapps.ui.R.id.schedule_list_container)).perform(
            RecyclerViewActions.scrollTo<TimetableListAdapter.TimetableListViewHolder>(
                hasDescendant(withText("Math8"))
            )
        )
        onView(withText("Math8")).check(matches(isDisplayed()))
    }

    private fun createScheduleFragment() {
        launchFragmentInHiltContainer<ScheduleFragment>(navigation = {
            Navigation.setViewNavController(requireView(), navController)
        })
    }

    private fun mockUtilsGetCurrentDate(date: LocalDate) {
        every { timetableUtils.getCurrentDate() } returns date
    }

    private fun verifyTimetableGridViewIsDisplayed() {
        onView(withId(com.studentsapps.ui.R.id.schedule_container_and_grid))
            .check(matches(isDisplayed())).check(
                matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))
            )
        onView(withId(com.studentsapps.ui.R.id.schedule_list_container))
            .check(matches(not(isDisplayed()))).check(
                matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE))
            )
    }

    private fun verifyTimetableListViewIsDisplayed() {
        onView(withId(com.studentsapps.ui.R.id.schedule_list_container))
            .check(
                matches(
                    ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
                )
            )
        onView(withId(com.studentsapps.ui.R.id.schedule_container_and_grid))
            .check(matches(not(isDisplayed())))
            .check(
                matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE))
            )
    }
}