package com.studentsapps.database.util

import androidx.room.TypeConverter
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class LocalTimeConverter {

    private val formatter = DateTimeFormatter.ofPattern("HH:mm")

    @TypeConverter
    fun localTimeToString(localTime: LocalTime): String = localTime.format(formatter)

    @TypeConverter
    fun stringToLocalTime(string: String): LocalTime = LocalTime.parse(string, formatter)
}