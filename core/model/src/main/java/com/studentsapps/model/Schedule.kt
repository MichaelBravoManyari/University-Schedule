package com.studentsapps.model

import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class Schedule(
    val id: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val classPlace: String?,
    val dayOfWeek: DayOfWeek,
    val specificDate: LocalDate?,
    val courseId: String
)
