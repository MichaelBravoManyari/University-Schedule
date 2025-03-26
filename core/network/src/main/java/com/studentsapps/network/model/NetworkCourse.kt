package com.studentsapps.network.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class NetworkCourse(
    val id: String,
    val name: String,
    val nameProfessor: String?,
    val color: Int,
    @Contextual val lastModified: LocalDateTime = LocalDateTime.now(),
    val userId: String = ""
)
