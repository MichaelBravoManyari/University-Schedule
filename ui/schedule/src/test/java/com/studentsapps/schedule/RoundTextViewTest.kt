package com.studentsapps.schedule

import android.widget.FrameLayout
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.spyk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config

@Config(application = HiltTestApplication::class, sdk = [33])
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class RoundTextViewTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    val canvasRender = spyk<RoundTextViewCanvasRender>()

    @Test
    fun verifyCircleIsDrawnWithoutColorInitially() {
        createRoundTextView()
        onView(withContentDescription("roundTextView")).check(matches(isDisplayed())).check { view, noViewFoundException ->
            (view as RoundTextView).setCircleBackgroundColor(0)
        }
        verify { canvasRender.drawCircleInMiddle(100, 100, any(), 0) }
    }

    private fun createRoundTextView() {
        launchFragmentInHiltContainer<TestFragment> {
            val attrs = Robolectric.buildAttributeSet().build()
            val roundTextView = RoundTextView(this.requireContext(), attrs)
            val layoutParams = FrameLayout.LayoutParams(100, 100)
            roundTextView.apply {
                this.layoutParams = layoutParams
                contentDescription = "roundTextView"
                text = "1"
            }
            (this as TestFragment).binding.root.addView(roundTextView)
        }
    }
}