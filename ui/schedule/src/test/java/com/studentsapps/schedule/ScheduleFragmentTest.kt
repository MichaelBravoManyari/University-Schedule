package com.studentsapps.schedule

import androidx.appcompat.app.AppCompatActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.studentsapps.schedule.timetable.TimetableUtils
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.every
import io.mockk.spyk
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(application = HiltTestApplication::class, sdk = [33])
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ScheduleFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    val timetableUtils = spyk<TimetableUtils>()

    @Test
    fun verifyCurrentMonthDisplayedInAppBar() {
        every { timetableUtils.getMonth(any()) } returns "July"
        launchFragmentInHiltContainer<ScheduleFragment> {
            this as ScheduleFragment
            val expectedMonth = "July"
            val realMonth = (requireActivity() as AppCompatActivity).supportActionBar?.title
            assertThat(realMonth, `is`(expectedMonth))
        }
    }
}