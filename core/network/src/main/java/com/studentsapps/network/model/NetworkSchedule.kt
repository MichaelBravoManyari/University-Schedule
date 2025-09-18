package com.studentsapps.network.model

import com.studentsapps.common.serializer.LocalDateSerializer
import com.studentsapps.common.serializer.LocalTimeSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Serializable
data class NetworkSchedule(
    @SerialName("id") val id: String,
    @Serializable(with = LocalTimeSerializer::class)
    @SerialName("startTime") val startTime: LocalTime,
    @Serializable(with = LocalTimeSerializer::class)
    @SerialName("endTime") val endTime: LocalTime,
    @SerialName("classPlace") val classPlace: String?,
    @SerialName("dayOfWeek") val dayOfWeek: DayOfWeek,
    @Serializable(with = LocalDateSerializer::class)
    @SerialName("specificDate") val specificDate: LocalDate? = null,
    @Contextual
    @SerialName("lastModified") val lastModified: LocalDateTime = LocalDateTime.now(),
    @SerialName("courseId") val courseId: String,
    @SerialName("userId") val userId: String = ""
)
