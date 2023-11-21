package com.studentsapps.data.repository

import com.studentsapps.datastore.UserPreferencesDataSource
import javax.inject.Inject

class ScheduleRepositoryImp @Inject constructor(
    private val scheduleLocalDataSource: UserPreferencesDataSource
) : ScheduleRepository {
}