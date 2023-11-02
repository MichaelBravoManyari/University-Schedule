package com.studentsapps.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
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
    fun getUserPreferencesFromTimetable_returnDefaultsValues() =
        testScope.runTestAndClearPreferences(testDataStore) {
            val expectedTimetableUserPreferences = TimetableUserPreferences(
                showAsGrid = true,
                is12HoursFormat = true,
                showSaturday = true,
                showSunday = true,
                isMondayFirstDayOfWeek = true
            )

            val actualTimetableUserPreferences = subject.userData.first()

            assertThat(actualTimetableUserPreferences, `is`(expectedTimetableUserPreferences))
        }

    @Test
    fun updateShowAsGrid_falseTrue() = testScope.runTestAndClearPreferences(testDataStore) {
        subject.updateShowAsGrid()
        assertFalse(subject.userData.first().showAsGrid)
        subject.updateShowAsGrid()
        assertTrue(subject.userData.first().showAsGrid)
    }

    @Test
    fun updateIs12HoursFormat_falseTrue() = testScope.runTestAndClearPreferences(testDataStore) {
        subject.updateIs12HoursFormat()
        assertFalse(subject.userData.first().is12HoursFormat)
        subject.updateIs12HoursFormat()
        assertTrue(subject.userData.first().is12HoursFormat)
    }

    @Test
    fun updateShowSaturday_falseTrue() = testScope.runTestAndClearPreferences(testDataStore) {
        subject.updateShowSaturday()
        assertFalse(subject.userData.first().showSaturday)
        subject.updateShowSaturday()
        assertTrue(subject.userData.first().showSaturday)
    }

    @Test
    fun updateShowSunday_falseTrue() = testScope.runTestAndClearPreferences(testDataStore) {
        subject.updateShowSunday()
        assertFalse(subject.userData.first().showSunday)
        subject.updateShowSunday()
        assertTrue(subject.userData.first().showSunday)
    }

    @Test
    fun updateIsMondayFirstDayOfWeek_falseTrue() =
        testScope.runTestAndClearPreferences(testDataStore) {
            subject.updateIsMondayFirstDayOfWeek()
            assertFalse(subject.userData.first().isMondayFirstDayOfWeek)
            subject.updateIsMondayFirstDayOfWeek()
            assertTrue(subject.userData.first().isMondayFirstDayOfWeek)
        }
}