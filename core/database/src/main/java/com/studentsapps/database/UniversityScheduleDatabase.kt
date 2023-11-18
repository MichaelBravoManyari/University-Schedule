package com.studentsapps.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.studentsapps.database.dao.CourseDao
import com.studentsapps.database.dao.ScheduleDao
import com.studentsapps.database.model.CourseEntity
import com.studentsapps.database.model.ScheduleEntity

@Database(
    entities = [
        ScheduleEntity::class,
        CourseEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class UniversityScheduleDatabase : RoomDatabase() {

    abstract fun scheduleDao(): ScheduleDao
    abstract fun courseDao(): CourseDao
}