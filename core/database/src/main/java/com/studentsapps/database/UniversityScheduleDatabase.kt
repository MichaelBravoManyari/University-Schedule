package com.studentsapps.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.studentsapps.database.dao.CourseDao
import com.studentsapps.database.dao.ScheduleDao
import com.studentsapps.database.model.CourseEntity
import com.studentsapps.database.model.ScheduleDetails
import com.studentsapps.database.model.ScheduleEntity
import com.studentsapps.database.util.DayOfWeekConverter
import com.studentsapps.database.util.LocalDateConverter
import com.studentsapps.database.util.LocalTimeConverter

@Database(
    entities = [
        ScheduleEntity::class,
        CourseEntity::class,
    ],
    views = [ScheduleDetails::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(
    LocalTimeConverter::class,
    DayOfWeekConverter::class,
    LocalDateConverter::class,
)
abstract class UniversityScheduleDatabase : RoomDatabase() {

    abstract fun scheduleDao(): ScheduleDao
    abstract fun courseDao(): CourseDao
}