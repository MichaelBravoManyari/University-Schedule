package com.studentsapps.schedule

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

internal class TimetableDateUtils @Inject constructor() {

    fun getDaysOfMonthCurrentWeek(isMondayFirstDayOfWeek: Boolean): List<String> {
        val formatter = DateTimeFormatter.ofPattern("d")
        val date: LocalDate = LocalDate.now()
        val startOfWeek =
            if (isMondayFirstDayOfWeek) date.with(DayOfWeek.MONDAY) else date.with(DayOfWeek.MONDAY)
                .minusDays(1)
        return (0 until 7).map { startOfWeek.plusDays(it.toLong()).format(formatter).toString() }
    }
}