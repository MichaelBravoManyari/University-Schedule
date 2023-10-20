package com.studentsapps.model

data class TimetableUserPreferences(
    val showAsGrid: Boolean,
    val is12HoursFormat: Boolean,
    val showSaturday: Boolean,
    val showSunday: Boolean,
    val isMondayFirstDayOfWeek: Boolean
)
