package com.studentsapps.domain.login.model

/**
 * Domain model representing an authenticated user.
 */
data class AuthenticatedUser(
    val userId: String,
    val email: String
)
