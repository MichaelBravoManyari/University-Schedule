package com.studentsapps.database.util

import androidx.room.TypeConverter
import java.time.LocalTime

class LocalTimeConverter {

    @TypeConverter
    fun localTimeToString(localTime: LocalTime): String =
        with(localTime) {
            "$hour:$minute"
        }

    @TypeConverter
    fun stringToLocalTime(string: String): LocalTime {
        val (hour, minute) = string.split(":").map { it.toInt() }
        return LocalTime.of(hour, minute)
    }
}