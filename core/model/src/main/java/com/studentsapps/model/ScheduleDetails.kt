package com.studentsapps.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

data class ScheduleDetails(
    val scheduleId: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val classPlace: String?,
    val dayOfWeek: DayOfWeek,
    val specificDate: LocalDate?,
    val courseId: String,
    val courseName: String,
    val courseColor: Int,
)
