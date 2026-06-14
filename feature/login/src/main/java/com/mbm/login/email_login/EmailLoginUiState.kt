package com.mbm.login.email_login

import androidx.annotation.StringRes
import com.mbm.login.auth.AuthUiError

/**
 * UI state for the email login screen.
 *
 * Holds both form field values (owned by the ViewModel so they survive
 * configuration changes) and the outcome of the sign-in operation.
 */
data class EmailLoginUiState(
    // ── Form inputs ──────────────────────────────────────────────────────────
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,

    // ── Validation errors (null = no error shown) ─────────────────────────────
    /** String resource ID for the email field error, or null if no error. */
    @param:StringRes val emailError: Int? = null,
    /** String resource ID for the password field error, or null if no error. */
    @param:StringRes val passwordError: Int? = null,

    // ── Operation state ───────────────────────────────────────────────────────
    /** True while the sign-in network request is in progress. */
    val isLoading: Boolean = false,

    /**
     * Non-null when a transient sign-in error (Toast / Snackbar) should be
     * shown. Reset to null after the UI consumes it via [EmailLoginViewModel.dismissError].
     */
    val error: AuthUiError? = null,

    /**
     * Flipped to true once sign-in succeeds and all post-auth work is done.
     * The Fragment observes this flag, performs navigation, and then calls
     * [EmailLoginViewModel.onNavigationComplete] to reset it.
     */
    val shouldNavigateToSchedule: Boolean = false,
)