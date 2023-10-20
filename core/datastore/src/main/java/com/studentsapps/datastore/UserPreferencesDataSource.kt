package com.studentsapps.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.studentsapps.model.TimetableUserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesDataSource @Inject constructor(private val dataStore: DataStore<Preferences>) {

    private object PreferencesKey {
        val SHOW_AS_GRID = booleanPreferencesKey("show_as_grid")
        val IS_12_HOURS_FORMAT = booleanPreferencesKey("is_12_hours_format")
        val SHOW_SATURDAY = booleanPreferencesKey("show_saturday")
        val SHOW_SUNDAY = booleanPreferencesKey("show_sunday")
        val IS_MONDAY_FIRST_DAY_OF_WEEK = booleanPreferencesKey("is_monday_first_day_of_week")
    }

    fun getUserPreferencesFromTimetable(): Flow<TimetableUserPreferences> {
        return dataStore.data.map { preferences ->
            TimetableUserPreferences(
                showAsGrid = preferences[PreferencesKey.SHOW_AS_GRID] ?: true,
                is12HoursFormat = preferences[PreferencesKey.IS_12_HOURS_FORMAT] ?: true,
                showSaturday = preferences[PreferencesKey.SHOW_SATURDAY] ?: true,
                showSunday = preferences[PreferencesKey.SHOW_SUNDAY] ?: true,
                isMondayFirstDayOfWeek = preferences[PreferencesKey.IS_MONDAY_FIRST_DAY_OF_WEEK]
                    ?: true
            )
        }
    }

    suspend fun updateShowAsGrid(showAsGrid: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.SHOW_AS_GRID] = showAsGrid
        }
    }

    suspend fun updateIs12HoursFormat(is12HoursFormat: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.IS_12_HOURS_FORMAT] = is12HoursFormat
        }
    }

    suspend fun updateShowSaturday(showSaturday: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.SHOW_SATURDAY] = showSaturday
        }
    }

    suspend fun updateShowSunday(showSunday: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.SHOW_SUNDAY] = showSunday
        }
    }

    suspend fun updateIsMondayFirstDayOfWeek(isMondayFirstDayOfWeek: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.IS_MONDAY_FIRST_DAY_OF_WEEK] = isMondayFirstDayOfWeek
        }
    }
}