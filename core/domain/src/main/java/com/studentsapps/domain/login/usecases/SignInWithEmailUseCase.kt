package com.studentsapps.domain.login.usecases

import com.studentsapps.domain.login.model.AuthResult
import com.studentsapps.domain.login.model.EmailSignInError
import com.studentsapps.domain.login.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Use case for signing in with email and password credentials.
 *
 * Validates inputs before delegating to the repository, keeping
 * business rules out of the ViewModel and data layers.
 */
class SignInWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    /**
     * Execute the email sign-in operation.
     *
     * @param email User's email address (must be non-blank)
     * @param password User's password (must be non-blank)
     * @return Flow emitting the authentication result
     */
    operator fun invoke(email: String, password: String): Flow<AuthResult> {
        val trimmedEmail = email.trim()

        val validationError = validate(trimmedEmail, password)
        if (validationError != null) {
            return flowOf(AuthResult.ValidationFailure(validationError))
        }

        return authRepository.signInWithEmail(trimmedEmail, password)
    }

    /**
     * Validates trimmed email and password against the business rules.
     *
     * Rules (evaluated in order):
     * 1. Email must not be blank.
     * 2. Email must match [EMAIL_REGEX].
     * 3. Password must not be blank.
     *
     * @return The first [EmailSignInError] found, or null if all rules pass.
     */
    private fun validate(email: String, password: String): EmailSignInError? = when {
        email.isBlank()            -> EmailSignInError.BLANK_EMAIL
        !EMAIL_REGEX.matches(email) -> EmailSignInError.INVALID_EMAIL_FORMAT
        password.isBlank()         -> EmailSignInError.BLANK_PASSWORD
        else                       -> null
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
    }
}