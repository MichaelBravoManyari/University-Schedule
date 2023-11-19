package com.studentsapps.database.util

import androidx.room.TypeConverter
import java.time.DayOfWeek

class DayOfWeekConverter {

    @TypeConverter
    fun dayOfWeekToInt(dayOfWeek: DayOfWeek): Int = dayOfWeek.value

    @TypeConverter
    fun intToDayOfWeek(day: Int): DayOfWeek = DayOfWeek.of(day)
}