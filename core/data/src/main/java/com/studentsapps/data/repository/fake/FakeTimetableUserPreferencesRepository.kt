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

class FakeTimetableUserPreferencesRepository @Inject constructor() :
    TimetableUserPreferencesRepository {

    private var showAsGrid = true

    private val _userData = MutableSharedFlow<TimetableUserPreferences>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val currentUserData
        get() = _userData.replayCache.firstOrNull() ?: baseTimetableUserPreferencesData

    override val userData: Flow<TimetableUserPreferences> = _userData.filterNotNull()

    fun init() {
        _userData.tryEmit(
            if (showAsGrid)
                baseTimetableUserPreferencesData
            else
                baseTimetableUserPreferencesData.copy(showAsGrid = false)
        )
    }

    fun setShowAsGrid(value: Boolean) {
        showAsGrid = value
    }

    override suspend fun updateShowAsGrid() {
        _userData.tryEmit(currentUserData.copy(showAsGrid = !currentUserData.showAsGrid))
    }

    override suspend fun updateIs12HoursFormat() {
        _userData.tryEmit(currentUserData.copy(is12HoursFormat = !currentUserData.is12HoursFormat))
    }

    override suspend fun updateShowSaturday() {
        _userData.tryEmit(currentUserData.copy(showSaturday = !currentUserData.showSaturday))
    }

    override suspend fun updateShowSunday() {
        _userData.tryEmit(currentUserData.copy(showSunday = !currentUserData.showSunday))
    }

    override suspend fun updateIsMondayFirstDayOfWeek() {
        _userData.tryEmit(currentUserData.copy(isMondayFirstDayOfWeek = !currentUserData.isMondayFirstDayOfWeek))
    }
}