package com.studentsapps.model

import java.time.LocalDateTime

data class PendingOperation(
    val id: Int,
    val operationType: String,
    val entityType: String,
    val payload: String,
    val status: String,
    val timestamp: LocalDateTime
)
