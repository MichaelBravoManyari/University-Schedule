package com.mbm.login.email_signup

import androidx.annotation.StringRes
import com.mbm.login.auth.AuthUiError

/**
 * UI state for the email sign-up screen.
 *
 * Holds all form field values (owned by the ViewModel so they survive
 * configuration changes) and the outcome of the registration operation.
 */
data class EmailSignUpUiState(
    // ── Form inputs ──────────────────────────────────────────────────────────
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,

    // ── Validation errors (null = no error shown) ─────────────────────────────
    /** String resource ID for the email field error, or null if no error. */
    @param:StringRes val emailError: Int? = null,
    /** String resource ID for the password field error, or null if no error. */
    @param:StringRes val passwordError: Int? = null,
    /** String resource ID for the confirm-password field error, or null if no error. */
    @param:StringRes val confirmPasswordError: Int? = null,

    // ── Operation state ───────────────────────────────────────────────────────
    /** True while the account-creation network request is in progress. */
    val isLoading: Boolean = false,

    /**
     * Non-null when a transient error (Toast) should be shown to the user.
     * Reset to null after the UI consumes it via [EmailSignUpViewModel.dismissError].
     */
    val error: AuthUiError? = null,

    /**
     * Flipped to true once registration succeeds and all post-auth work is done.
     * The Fragment observes this flag, performs navigation, and then calls
     * [EmailSignUpViewModel.onNavigationComplete] to reset it.
     */
    val shouldNavigateToSchedule: Boolean = false,
)
