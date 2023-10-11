package com.studentsapps.schedule.timetable

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.studentsapps.schedule.R
import org.hamcrest.Matchers.not

fun verifyTimetableGridViewIsDisplayed() {
    onView(withId(R.id.schedule_container_and_grid))
        .check(matches(isDisplayed())).check(
            matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))
        )
    onView(withId(R.id.schedule_list_container))
        .check(matches(not(isDisplayed()))).check(
            matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE))
        )
}

fun verifyTimetableListViewIsDisplayed() {
    onView(withId(R.id.schedule_list_container))
        .check(matches(isDisplayed())).check(
            matches(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
            )
        )
    onView(withId(R.id.schedule_container_and_grid))
        .check(matches(not(isDisplayed())))
        .check(
            matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE))
        )
}