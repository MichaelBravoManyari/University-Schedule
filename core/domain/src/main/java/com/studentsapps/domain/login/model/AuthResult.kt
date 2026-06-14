package com.studentsapps.domain.login.model

/**
 * Sealed class representing the result of an authentication operation.
 */
sealed class AuthResult {
    /**
     * Authentication was successful.
     * @param userId The unique identifier of the authenticated user.
     */
    data class Success(val userId: String) : AuthResult()

    /**
     * Authentication failed.
     * @param error The error that occurred during authentication.
     */
    data class Failure(val error: AuthError) : AuthResult()

    /**
     * Authentication was not attempted because one or more input fields
     * failed validation.
     *
     * Emitted by use cases before any network call is made, so the ViewModel
     * can forward per-field errors to the UI without touching [AuthError] or
     * network-error strings.
     *
     * @param error The specific validation rule that was violated.
     */
    data class ValidationFailure(val error: AuthValidationError) : AuthResult()
}

/**
 * Enum representing possible authentication errors.
 */
enum class AuthError {
    NETWORK_ERROR,
    INVALID_CREDENTIALS,
    USER_NOT_FOUND,
    WEAK_PASSWORD,
    EMAIL_ALREADY_IN_USE,
    CANCELLED_BY_USER,
    UNKNOWN_ERROR
}