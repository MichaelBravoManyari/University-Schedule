package com.mbm.login.email_signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbm.login.auth.AuthUiError
import com.mbm.login.auth.toUiError
import com.studentsapps.domain.login.model.AuthResult
import com.studentsapps.domain.login.model.EmailSignUpError
import com.studentsapps.domain.login.usecases.RegisterUserIfNeededUseCase
import com.studentsapps.domain.login.usecases.SignUpWithEmailUseCase
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
 * ViewModel for the email sign-up screen.
 *
 * Owns all form state (email, password, confirmPassword, visibility toggles)
 * and the registration operation state (loading, error, navigation trigger).
 *
 * All validation rules live exclusively in [SignUpWithEmailUseCase]; this
 * ViewModel only maps [AuthResult.ValidationFailure] back to per-field UI errors.
 */
@HiltViewModel
class EmailSignUpViewModel @Inject constructor(
    private val signUpWithEmailUseCase: SignUpWithEmailUseCase,
    private val registerUserIfNeededUseCase: RegisterUserIfNeededUseCase,
    private val startSyncIfNeededUseCase: StartSyncIfNeededUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailSignUpUiState())
    val uiState: StateFlow<EmailSignUpUiState> = _uiState.asStateFlow()

    // ── Form field events ─────────────────────────────────────────────────────

    /** Update the email field and clear its inline error immediately. */
    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    /** Update the password field and clear its inline error immediately. */
    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    /** Update the confirm-password field and clear its inline error immediately. */
    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onToggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    // ── Register action ───────────────────────────────────────────────────────

    /**
     * Starts the sign-up flow.
     *
     * Passes raw field values directly to [SignUpWithEmailUseCase], which trims
     * the email, validates all fields against the business rules, and then calls
     * the repository if everything is valid.
     */
    fun onRegisterClick() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            signUpWithEmailUseCase(state.email, state.password, state.confirmPassword)
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
            is AuthResult.ValidationFailure -> {
                // Use case guarantees the error is EmailSignUpError in this flow.
                handleValidationFailure(result.error as EmailSignUpError)
            }

            is AuthResult.Success -> {
                val registered = registerUserIfNeededUseCase(result.userId)
                if (registered) {
                    startSyncIfNeededUseCase()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            shouldNavigateToSchedule = true,
                            error = null,
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = AuthUiError.Message(R.string.login_error),
                        )
                    }
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
     * Maps each [EmailSignUpError] to the correct per-field string-resource error ID.
     *
     * - [EmailSignUpError.BLANK_EMAIL] and [EmailSignUpError.INVALID_EMAIL_FORMAT]
     *   surface as inline errors on the email field.
     * - [EmailSignUpError.BLANK_PASSWORD] and [EmailSignUpError.WEAK_PASSWORD]
     *   surface on the password field.
     * - [EmailSignUpError.PASSWORDS_DO_NOT_MATCH] surfaces on the confirm-password field.
     */
    private fun handleValidationFailure(error: EmailSignUpError) {
        val emailError: Int?
        val passwordError: Int?
        val confirmPasswordError: Int?

        when (error) {
            EmailSignUpError.BLANK_EMAIL -> {
                emailError = R.string.enter_your_email
                passwordError = null
                confirmPasswordError = null
            }
            EmailSignUpError.INVALID_EMAIL_FORMAT -> {
                emailError = R.string.invalid_email
                passwordError = null
                confirmPasswordError = null
            }
            EmailSignUpError.BLANK_PASSWORD -> {
                emailError = null
                passwordError = R.string.enter_your_password
                confirmPasswordError = null
            }
            EmailSignUpError.WEAK_PASSWORD -> {
                emailError = null
                passwordError = R.string.low_security_password
                confirmPasswordError = null
            }
            EmailSignUpError.PASSWORDS_DO_NOT_MATCH -> {
                emailError = null
                passwordError = null
                confirmPasswordError = R.string.passwords_do_not_match
            }
        }

        _uiState.update {
            it.copy(
                isLoading = false,
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
            )
        }
    }
}