package com.studentsapps.network.model

import com.studentsapps.network.serializer.LocalDateSerializer
import com.studentsapps.network.serializer.LocalTimeSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Serializable
data class NetworkSchedule(
    val id: String,
    @Serializable(with = LocalTimeSerializer::class) val startTime: LocalTime,
    @Serializable(with = LocalTimeSerializer::class) val endTime: LocalTime,
    val classPlace: String?,
    val dayOfWeek: DayOfWeek,
    @Serializable(with = LocalDateSerializer::class) val specificDate: LocalDate? = null,
    @Contextual val lastModified: LocalDateTime = LocalDateTime.now(),
    val courseId: String,
    val userId: String
)
