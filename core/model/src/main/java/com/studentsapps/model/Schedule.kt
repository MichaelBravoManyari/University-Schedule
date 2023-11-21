package com.studentsapps.model

import java.time.DayOfWeek
import java.time.LocalTime

data class Schedule(
    val id: Int,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val classPlace: String?,
    val dayOfWeek: DayOfWeek,
    val courseId: Int
)
