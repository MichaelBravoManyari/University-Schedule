package com.studentsapps.domain.login.usecases

import com.studentsapps.domain.login.model.AuthResult
import com.studentsapps.domain.login.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for signing in with Google credentials.
 *
 * This encapsulates the business logic for Google authentication.
 */
class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Execute the Google sign in operation.
     *
     * @param idToken Google ID token obtained from credential manager
     * @return Flow emitting the authentication result
     */
    operator fun invoke(idToken: String): Flow<AuthResult> {
        require(idToken.isNotBlank()) { "ID token cannot be blank" }

        return authRepository.signInWithGoogle(idToken)
    }
}