package com.studentsapps.data.repository

import com.studentsapps.datastore.UserPreferencesDataSource
import com.studentsapps.model.TimetableUserPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TimetableUserPreferencesRepositoryImp @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource
) : TimetableUserPreferencesRepository {

    override val userData: Flow<TimetableUserPreferences> = userPreferencesDataSource.userData


    override suspend fun updateShowAsGrid() {
        userPreferencesDataSource.updateShowAsGrid()
    }

    override suspend fun updateIs12HoursFormat() {
        userPreferencesDataSource.updateIs12HoursFormat()
    }

    override suspend fun updateShowSaturday() {
        userPreferencesDataSource.updateShowSaturday()
    }

    override suspend fun updateShowSunday() {
        userPreferencesDataSource.updateShowSunday()
    }

    override suspend fun updateIsMondayFirstDayOfWeek() {
        userPreferencesDataSource.updateIsMondayFirstDayOfWeek()
    }
}