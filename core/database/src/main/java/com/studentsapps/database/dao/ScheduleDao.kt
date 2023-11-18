package com.studentsapps.database.dao

import androidx.room.Dao
import com.studentsapps.database.model.ScheduleEntity

@Dao
abstract class ScheduleDao : BaseDao<ScheduleEntity>