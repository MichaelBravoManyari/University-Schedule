package com.studentsapps.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.studentsapps.model.TimetableUserPreferences
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

    val userData = dataStore.data.map { preferences ->
        TimetableUserPreferences(
            showAsGrid = preferences[PreferencesKey.SHOW_AS_GRID] ?: true,
            is12HoursFormat = preferences[PreferencesKey.IS_12_HOURS_FORMAT] ?: true,
            showSaturday = preferences[PreferencesKey.SHOW_SATURDAY] ?: true,
            showSunday = preferences[PreferencesKey.SHOW_SUNDAY] ?: true,
            isMondayFirstDayOfWeek = preferences[PreferencesKey.IS_MONDAY_FIRST_DAY_OF_WEEK]
                ?: true
        )
    }

    suspend fun updateShowAsGrid() {
        dataStore.edit { preferences ->
            val currentShowAsGrid = preferences[PreferencesKey.SHOW_AS_GRID] ?: true
            preferences[PreferencesKey.SHOW_AS_GRID] = !currentShowAsGrid
        }
    }

    suspend fun updateIs12HoursFormat() {
        dataStore.edit { preferences ->
            val currentIs12HoursFormat = preferences[PreferencesKey.IS_12_HOURS_FORMAT] ?: true
            preferences[PreferencesKey.IS_12_HOURS_FORMAT] = !currentIs12HoursFormat
        }
    }

    suspend fun updateShowSaturday() {
        dataStore.edit { preferences ->
            val currentShowSaturday = preferences[PreferencesKey.SHOW_SATURDAY] ?: true
            preferences[PreferencesKey.SHOW_SATURDAY] = !currentShowSaturday
        }
    }

    suspend fun updateShowSunday() {
        dataStore.edit { preferences ->
            val currentShowSunday = preferences[PreferencesKey.SHOW_SUNDAY] ?: true
            preferences[PreferencesKey.SHOW_SUNDAY] = !currentShowSunday
        }
    }

    suspend fun updateIsMondayFirstDayOfWeek() {
        dataStore.edit { preferences ->
            val currentIsMondayFirstDayOfWeek =
                preferences[PreferencesKey.IS_MONDAY_FIRST_DAY_OF_WEEK] ?: true
            preferences[PreferencesKey.IS_MONDAY_FIRST_DAY_OF_WEEK] = !currentIsMondayFirstDayOfWeek
        }
    }
}