package com.studentsapps.model

import com.studentsapps.common.serializer.DayOfWeekSerializer
import com.studentsapps.common.serializer.LocalDateSerializer
import com.studentsapps.common.serializer.LocalTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class Schedule(
    @SerialName("id") val id: String,
    @Serializable(with = LocalTimeSerializer::class)
    @SerialName("startTime") val startTime: LocalTime,
    @Serializable(with = LocalTimeSerializer::class)
    @SerialName("endTime") val endTime: LocalTime,
    @SerialName("classPlace") val classPlace: String?,
    @Serializable(with = DayOfWeekSerializer::class)
    @SerialName("dayOfWeek") val dayOfWeek: DayOfWeek,
    @Serializable(with = LocalDateSerializer::class)
    @SerialName("specificDate") val specificDate: LocalDate?,
    @SerialName("courseId") val courseId: String,
)
