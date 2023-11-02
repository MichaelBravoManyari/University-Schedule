package com.studentsapps.data.repository.fake

import com.studentsapps.data.repository.TimetableUserPreferencesRepository
import com.studentsapps.model.TimetableUserPreferences
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

val baseTimetableUserPreferencesData = TimetableUserPreferences(
    showAsGrid = true,
    is12HoursFormat = true,
    showSaturday = true,
    showSunday = true,
    isMondayFirstDayOfWeek = true
)

class FakeTimetableUserPreferencesRepository @Inject constructor(): TimetableUserPreferencesRepository {

    private val _userData = MutableSharedFlow<TimetableUserPreferences>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val currentUserData
        get() = _userData.replayCache.firstOrNull() ?: baseTimetableUserPreferencesData

    override val userData: Flow<TimetableUserPreferences> = _userData.filterNotNull()

    init {
        _userData.tryEmit(baseTimetableUserPreferencesData)
    }

    override suspend fun updateShowAsGrid() {
        _userData.tryEmit(currentUserData.copy(showAsGrid = !currentUserData.showAsGrid))
    }

    override suspend fun updateIs12HoursFormat() {
        // Todo
    }

    override suspend fun updateShowSaturday() {
        // Todo
    }

    override suspend fun updateShowSunday() {
        // Todo
    }

    override suspend fun updateIsMondayFirstDayOfWeek() {
        // Todo
    }
}