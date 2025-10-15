package com.studentsapps.network.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class NetworkCourse(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("nameProfessor") val nameProfessor: String?,
    @SerialName("color") val color: Int,
    @Contextual
    @SerialName("lastModified") val lastModified: LocalDateTime = LocalDateTime.now(),
    @SerialName("userId") val userId: String = "",
)
