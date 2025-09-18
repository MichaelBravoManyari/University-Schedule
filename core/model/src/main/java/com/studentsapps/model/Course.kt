package com.studentsapps.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Course(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("nameProfessor") val nameProfessor: String?,
    @SerialName("color") val color: Int
)
