package com.studentsapps.domain.login.usecases

import com.studentsapps.domain.login.model.AuthResult
import com.studentsapps.domain.login.model.EmailSignUpError
import com.studentsapps.domain.login.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Use case for creating a new account with email and password.
 *
 * Centralizes all sign-up validation rules so neither the ViewModel nor the
 * repository need to know about them:
 *   - Email and password are trimmed before any check.
 *   - Email must not be blank and must match the RFC-5322-compatible regex.
 *   - Password must not be blank and must satisfy the security requirements.
 *   - Confirm-password must match the password exactly.
 *
 * Returns [AuthResult.ValidationFailure] for invalid inputs, keeping the
 * call-site free of try/catch and consistent with the rest of the auth flows.
 */
class SignUpWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    /**
     * Execute the email sign-up operation.
     *
     * Email is trimmed internally; callers do not need to sanitize it first.
     * Passwords are **not** trimmed — a trailing space in a password is intentional.
     *
     * @param email           Raw email string from the UI.
     * @param password        Raw password string from the UI.
     * @param confirmPassword Repeated password entered by the user.
     * @return Flow emitting an [AuthResult]:
     *   - [AuthResult.ValidationFailure] when inputs do not pass the rules.
     *   - [AuthResult.Success] / [AuthResult.Failure] from the repository otherwise.
     */
    operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String,
    ): Flow<AuthResult> {
        val trimmedEmail = email.trim()

        val validationError = validate(trimmedEmail, password, confirmPassword)
        if (validationError != null) {
            return flowOf(AuthResult.ValidationFailure(validationError))
        }

        return authRepository.signUpWithEmail(trimmedEmail, password)
    }

    // ── Validation ────────────────────────────────────────────────────────────

    /**
     * Validates inputs against all sign-up business rules.
     *
     * Rules (evaluated in order — first failure short-circuits):
     * 1. Email must not be blank.
     * 2. Email must match [EMAIL_REGEX].
     * 3. Password must not be blank.
     * 4. Password must satisfy [PASSWORD_REGEX] (length, complexity, no spaces).
     * 5. Confirm-password must equal the password.
     *
     * @return The first [EmailSignUpError] found, or null if all rules pass.
     */
    private fun validate(
        email: String,
        password: String,
        confirmPassword: String,
    ): EmailSignUpError? = when {
        email.isBlank()                    -> EmailSignUpError.BLANK_EMAIL
        !EMAIL_REGEX.matches(email)         -> EmailSignUpError.INVALID_EMAIL_FORMAT
        password.isEmpty()                 -> EmailSignUpError.BLANK_PASSWORD
        !PASSWORD_REGEX.matches(password)  -> EmailSignUpError.WEAK_PASSWORD
        password != confirmPassword        -> EmailSignUpError.PASSWORDS_DO_NOT_MATCH
        else                               -> null
    }

    private companion object {
        val EMAIL_REGEX = Regex(
            "[a-zA-Z0-9+._%\\-]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )

        /**
         * Password security rules (matches the original Fragment regex):
         * - At least one digit            (?=.*[0-9])
         * - At least one lowercase letter (?=.*[a-z])
         * - At least one uppercase letter (?=.*[A-Z])
         * - At least one special char     (?=.*[@#$%^&+=!])
         * - No whitespace                 (?=\S+$)
         * - Minimum 8 characters          .{8,}
         */
        val PASSWORD_REGEX = Regex(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=!])(?=\\S+\$).{8,}\$"
        )
    }
}