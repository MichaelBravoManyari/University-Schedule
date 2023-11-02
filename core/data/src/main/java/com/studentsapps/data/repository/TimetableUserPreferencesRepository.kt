package com.studentsapps.data.repository

import com.studentsapps.model.TimetableUserPreferences
import kotlinx.coroutines.flow.Flow

interface TimetableUserPreferencesRepository {

    val userData: Flow<TimetableUserPreferences>

    suspend fun updateShowAsGrid()

    suspend fun updateIs12HoursFormat()

    suspend fun updateShowSaturday()

    suspend fun updateShowSunday()

    suspend fun updateIsMondayFirstDayOfWeek()
}