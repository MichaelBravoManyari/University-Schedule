package com.studentsapps.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.studentsapps.datastore.UserPreferencesDataSource
import com.studentsapps.datastore_test.runTestAndClearPreferences
import com.studentsapps.model.TimetableUserPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TimetableUserPreferencesRepositoryImpTest {

    private val testContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val testScope = TestScope(UnconfinedTestDispatcher())
    private val testDataStore: DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { testContext.preferencesDataStoreFile("user_preferences_test.preferences_pb") }
        )
    private val userPreferencesDataSource = UserPreferencesDataSource(testDataStore)
    private val subject = TimetableUserPreferencesRepositoryImp(userPreferencesDataSource)

    @Test
    fun getTimetableUserPreferences_returnValues() =
        testScope.runTestAndClearPreferences(testDataStore) {
            val expectedTimetableUserPreferences = TimetableUserPreferences(
                showAsGrid = true,
                is12HoursFormat = false,
                showSaturday = true,
                showSunday = false,
                isMondayFirstDayOfWeek = true
            )

            subject.updateIs12HoursFormat()
            subject.updateShowSunday()
            val actualTimetableUserPreferences = subject.getTimetableUserPreferences().first()

            assertThat(
                actualTimetableUserPreferences,
                `is`(expectedTimetableUserPreferences)
            )
        }

    @Test
    fun updateShowAsGrid_falseTrue() = testScope.runTestAndClearPreferences(testDataStore) {
        subject.updateShowAsGrid()
        assertFalse(subject.getTimetableUserPreferences().first().showAsGrid)
        subject.updateShowAsGrid()
        assertTrue(subject.getTimetableUserPreferences().first().showAsGrid)
    }

    @Test
    fun updateIs12HoursFormat_falseTrue() = testScope.runTestAndClearPreferences(testDataStore) {
        subject.updateIs12HoursFormat()
        assertFalse(subject.getTimetableUserPreferences().first().is12HoursFormat)
        subject.updateIs12HoursFormat()
        assertTrue(subject.getTimetableUserPreferences().first().is12HoursFormat)
    }

    @Test
    fun updateShowSaturday_falseTrue() = testScope.runTestAndClearPreferences(testDataStore) {
        subject.updateShowSaturday()
        assertFalse(subject.getTimetableUserPreferences().first().showSaturday)
        subject.updateShowSaturday()
        assertTrue(subject.getTimetableUserPreferences().first().showSaturday)
    }

    @Test
    fun updateShowSunday_falseTrue() = testScope.runTestAndClearPreferences(testDataStore) {
        subject.updateShowSunday()
        assertFalse(subject.getTimetableUserPreferences().first().showSunday)
        subject.updateShowSunday()
        assertTrue(subject.getTimetableUserPreferences().first().showSunday)
    }

    @Test
    fun updateIsMondayFirstDayOfWeek_falseTrue() =
        testScope.runTestAndClearPreferences(testDataStore) {
            subject.updateIsMondayFirstDayOfWeek()
            assertFalse(subject.getTimetableUserPreferences().first().isMondayFirstDayOfWeek)
            subject.updateIsMondayFirstDayOfWeek()
            assertTrue(subject.getTimetableUserPreferences().first().isMondayFirstDayOfWeek)
        }
}