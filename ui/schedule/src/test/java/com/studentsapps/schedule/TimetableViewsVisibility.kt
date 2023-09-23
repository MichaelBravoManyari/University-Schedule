package com.studentsapps.schedule

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matchers

fun verifyTimetableGridViewIsDisplayed() {
    Espresso.onView(ViewMatchers.withId(R.id.hour_drawing_container_and_grid))
        .check(ViewAssertions.matches(ViewMatchers.isDisplayed())).check(
            ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))
        )
    Espresso.onView(ViewMatchers.withId(R.id.schedule_list_container))
        .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed()))).check(
            ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE))
        )
}

fun verifyTimetableListViewIsDisplayed() {
    Espresso.onView(ViewMatchers.withId(R.id.schedule_list_container))
        .check(ViewAssertions.matches(ViewMatchers.isDisplayed())).check(
            ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
            )
        )
    Espresso.onView(ViewMatchers.withId(R.id.hour_drawing_container_and_grid))
        .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
        .check(
            ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE))
        )
}