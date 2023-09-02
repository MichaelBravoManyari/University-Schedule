package com.studentsapps.schedule

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class TimetableDateUtils @Inject constructor() {

    fun getDaysOfMonthOfWeek(
        isMondayFirstDayOfWeek: Boolean,
        date: LocalDate = LocalDate.now()
    ): List<String> {
        val formatter = DateTimeFormatter.ofPattern("d")
        val startOfWeek =
            if (isMondayFirstDayOfWeek) date.with(DayOfWeek.MONDAY) else date.with(DayOfWeek.MONDAY)
                .minusDays(1)
        return (0 until 7).map { startOfWeek.plusDays(it.toLong()).format(formatter).toString() }
    }

    fun getDaysOfWeekOrder(isMondayFirstDayOfWeek: Boolean): List<Int> {
        return if (isMondayFirstDayOfWeek) {
            listOf(
                R.string.monday_abbr,
                R.string.tuesday_abbr,
                R.string.wednesday_abbr,
                R.string.thursday_abbr,
                R.string.friday_abbr,
                R.string.saturday_abbr,
                R.string.sunday_abbr
            )
        } else {
            listOf(
                R.string.sunday_abbr,
                R.string.monday_abbr,
                R.string.tuesday_abbr,
                R.string.wednesday_abbr,
                R.string.thursday_abbr,
                R.string.friday_abbr,
                R.string.saturday_abbr
            )
        }
    }
}