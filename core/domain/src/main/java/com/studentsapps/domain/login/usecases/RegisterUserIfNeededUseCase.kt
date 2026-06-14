package com.studentsapps.domain.login.usecases

import com.studentsapps.domain.login.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for registering a user in Firestore if they don't exist.
 *
 * This ensures that authenticated users are properly registered in the database.
 */
class RegisterUserIfNeededUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Execute the user registration check and registration if needed.
     *
     * @param userId The user ID to check and register
     * @return true if the operation was successful, false otherwise
     */
    suspend operator fun invoke(userId: String): Boolean {
        require(userId.isNotBlank()) { "User ID cannot be blank" }

        return authRepository.registerUserIfNeeded(userId)
    }
}