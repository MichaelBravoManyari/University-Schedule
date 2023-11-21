package com.studentsapps.database.util

import androidx.room.TypeConverter
import java.time.LocalDate

class LocalDateConverter {

    @TypeConverter
    fun localDateToString(localDate: LocalDate): String =
        with(localDate) {
            "$dayOfMonth/$monthValue/$year"
        }

    @TypeConverter
    fun stringToLocalDate(string: String): LocalDate {
        val (dayOfMonth, month, year) = string.split("/").map { it.toInt() }
        return LocalDate.of(year, month, dayOfMonth)
    }
}