package com.mbm.login.auth

import com.studentsapps.common.R
import com.studentsapps.domain.login.model.AuthError

/**
 * UI state for the authentication screen.
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: AuthUiError? = null,
    val shouldNavigateToSchedule: Boolean = false
)

/**
 * UI-friendly representation of authentication errors.
 */
sealed class AuthUiError {
    data class Message(val resId: Int) : AuthUiError()
    data class Text(val message: String) : AuthUiError()
}

/**
 * Maps domain auth errors to UI error messages.
 */
fun AuthError.toUiError(): AuthUiError {
    return when (this) {
        AuthError.NETWORK_ERROR -> AuthUiError.Message(R.string.network_error)
        AuthError.INVALID_CREDENTIALS -> AuthUiError.Message(com.studentsapps.login.R.string.invalid_credentials)
        AuthError.USER_NOT_FOUND -> AuthUiError.Message(com.studentsapps.login.R.string.user_not_found)
        AuthError.WEAK_PASSWORD -> AuthUiError.Message(com.studentsapps.login.R.string.weak_password)
        AuthError.EMAIL_ALREADY_IN_USE -> AuthUiError.Message(com.studentsapps.login.R.string.email_in_use)
        AuthError.CANCELLED_BY_USER -> AuthUiError.Message(com.studentsapps.login.R.string.login_cancelled)
        AuthError.UNKNOWN_ERROR -> AuthUiError.Message(com.studentsapps.login.R.string.login_error)
    }
}
