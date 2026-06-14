package com.mbm.login.email_login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbm.login.auth.toUiError
import com.studentsapps.domain.login.model.AuthResult
import com.studentsapps.domain.login.model.EmailSignInError
import com.studentsapps.domain.login.usecases.SignInWithEmailUseCase
import com.studentsapps.domain.sync.usecases.StartSyncIfNeededUseCase
import com.studentsapps.login.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the email login screen.
 *
 * Owns all form state (email, password, visibility toggle) and auth operation
 * state (loading, error, navigation trigger).
 *
 * Validation rules live exclusively in [SignInWithEmailUseCase]; the ViewModel
 * only maps [AuthResult.ValidationFailure] back to per-field UI errors.
 */
@HiltViewModel
class EmailLoginViewModel @Inject constructor(
    private val signInWithEmailUseCase: SignInWithEmailUseCase,
    private val startSyncIfNeededUseCase: StartSyncIfNeededUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailLoginUiState())
    val uiState: StateFlow<EmailLoginUiState> = _uiState.asStateFlow()

    // ── Form field events ─────────────────────────────────────────────────────

    fun onEmailChange(email: String) {
        // Clear the per-field error as soon as the user starts correcting it
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    // ── Sign-in action ────────────────────────────────────────────────────────

    /**
     * Starts the sign-in flow.
     *
     * Passes raw field values directly to [SignInWithEmailUseCase], which trims
     * whitespace and validates according to business rules before calling the
     * repository.
     */
    fun onLoginClick() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            signInWithEmailUseCase(state.email, state.password)
                .collect { result -> handleAuthResult(result) }
        }
    }

    // ── Post-auth lifecycle ───────────────────────────────────────────────────

    /**
     * Called by the Fragment after navigation has been performed so the flag
     * is reset and a second navigation is not triggered on recomposition.
     */
    fun onNavigationComplete() {
        _uiState.update { it.copy(shouldNavigateToSchedule = false) }
    }

    /** Clears the current transient error from the UI state. */
    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    // ── Result handling ───────────────────────────────────────────────────────

    private suspend fun handleAuthResult(result: AuthResult) {
        when (result) {
            is AuthResult.ValidationFailure -> handleValidationFailure(result.error as EmailSignInError)

            is AuthResult.Success -> {
                startSyncIfNeededUseCase()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        shouldNavigateToSchedule = true,
                        error = null,
                    )
                }
            }

            is AuthResult.Failure -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.error.toUiError(),
                    )
                }
            }
        }
    }

    /**
     * Maps [EmailSignInError] values to per-field string-resource error IDs.
     *
     * [EmailSignInError.BLANK_EMAIL] and [EmailSignInError.INVALID_EMAIL_FORMAT]
     * both surface on the email field (different messages); [EmailSignInError.BLANK_PASSWORD]
     * surfaces on the password field.
     */
    private fun handleValidationFailure(error: EmailSignInError) {
        val emailError: Int?
        val passwordError: Int?

        when (error) {
            EmailSignInError.BLANK_EMAIL -> {
                emailError = R.string.enter_your_email
                passwordError = null
            }
            EmailSignInError.INVALID_EMAIL_FORMAT -> {
                emailError = R.string.invalid_email
                passwordError = null
            }
            EmailSignInError.BLANK_PASSWORD -> {
                emailError = null
                passwordError = R.string.enter_your_password
            }
        }

        _uiState.update {
            it.copy(
                isLoading = false,
                emailError = emailError,
                passwordError = passwordError,
            )
        }
    }
}