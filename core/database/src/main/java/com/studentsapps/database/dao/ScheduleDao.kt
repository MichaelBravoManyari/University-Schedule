package com.studentsapps.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.studentsapps.database.model.ScheduleEntity

@Dao
abstract class ScheduleDao : BaseDao<ScheduleEntity> {

    @Query("SELECT * FROM schedules where id = :scheduleId")
    abstract suspend fun getScheduleById(scheduleId: Int): ScheduleEntity
}