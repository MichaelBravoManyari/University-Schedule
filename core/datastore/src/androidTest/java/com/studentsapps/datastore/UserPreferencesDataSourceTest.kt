package com.studentsapps.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.studentsapps.model.TimetableUserPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class UserPreferencesDataSourceTest {

    private val testContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    private val testScope = TestScope(UnconfinedTestDispatcher())

    private val testDataStore: DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { testContext.preferencesDataStoreFile("user_preferences_test.preferences_pb") }
        )

    private val subject = UserPreferencesDataSource(testDataStore)

    @Test
    fun getUserPreferencesFromTimetable_returnDefaultsValues() = runTestAndCleanup {
        val expectedTimetableUserPreferences = TimetableUserPreferences(
            showAsGrid = true,
            is12HoursFormat = true,
            showSaturday = true,
            showSunday = true,
            isMondayFirstDayOfWeek = true
        )

        val actualTimetableUserPreferences = subject.getUserPreferencesFromTimetable().first()

        assertThat(actualTimetableUserPreferences, `is`(expectedTimetableUserPreferences))
    }

    @Test
    fun updateShowAsGrid_falseTrue() = runTestAndCleanup {
        subject.updateShowAsGrid(false)
        assertFalse(subject.getUserPreferencesFromTimetable().first().showAsGrid)
        subject.updateShowAsGrid(true)
        assertTrue(subject.getUserPreferencesFromTimetable().first().showAsGrid)
    }

    @Test
    fun updateIs12HoursFormat_falseTrue() = runTestAndCleanup {
        subject.updateIs12HoursFormat(false)
        assertFalse(subject.getUserPreferencesFromTimetable().first().is12HoursFormat)
        subject.updateIs12HoursFormat(true)
        assertTrue(subject.getUserPreferencesFromTimetable().first().is12HoursFormat)
    }

    @Test
    fun updateShowSaturday_falseTrue() = runTestAndCleanup {
        subject.updateShowSaturday(false)
        assertFalse(subject.getUserPreferencesFromTimetable().first().showSaturday)
        subject.updateShowSaturday(true)
        assertTrue(subject.getUserPreferencesFromTimetable().first().showSaturday)
    }

    @Test
    fun updateShowSunday_falseTrue() = runTestAndCleanup {
        subject.updateShowSunday(false)
        assertFalse(subject.getUserPreferencesFromTimetable().first().showSunday)
        subject.updateShowSunday(true)
        assertTrue(subject.getUserPreferencesFromTimetable().first().showSunday)
    }

    @Test
    fun updateIsMondayFirstDayOfWeek_falseTrue() = runTestAndCleanup {
        subject.updateIsMondayFirstDayOfWeek(false)
        assertFalse(subject.getUserPreferencesFromTimetable().first().isMondayFirstDayOfWeek)
        subject.updateIsMondayFirstDayOfWeek(true)
        assertTrue(subject.getUserPreferencesFromTimetable().first().isMondayFirstDayOfWeek)
    }

    private fun runTestAndCleanup(
        block: suspend () -> Unit,
    ) = testScope.runTest {
        testDataStore.edit {
            it.clear()
        }
        block()
    }
}