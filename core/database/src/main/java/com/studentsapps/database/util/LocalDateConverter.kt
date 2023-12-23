package com.studentsapps.database.util

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateConverter {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @TypeConverter
    fun localDateToString(localDate: LocalDate): String = localDate.format(formatter)

    @TypeConverter
    fun stringToLocalDate(string: String): LocalDate = LocalDate.parse(string, formatter)
}