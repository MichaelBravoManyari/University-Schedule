package com.studentsapps.testing.util

import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

fun withTextColor(@ColorInt expectedTextColor: Int): Matcher<View> {
    return object : BoundedMatcher<View, TextView>(TextView::class.java) {
        override fun describeTo(description: Description?) {
            description?.appendText("with textColor: ")
            description?.appendValue(expectedTextColor)
        }

        override fun matchesSafely(item: TextView?): Boolean {
            return item?.currentTextColor == expectedTextColor
        }
    }
}