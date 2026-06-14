package com.studentsapps.domain.login.repository

import com.studentsapps.domain.login.model.AuthResult
import com.studentsapps.domain.login.model.AuthenticatedUser
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations.
 * This defines the contract for authentication data sources.
 */
interface AuthRepository {
    /**
     * Sign in with Google credentials.
     *
     * @param idToken Google ID token obtained from the credential manager
     * @return Flow emitting the authentication result
     */
    fun signInWithGoogle(idToken: String): Flow<AuthResult>

    /**
     * Sign in with email and password credentials.
     *
     * @param email User's email address
     * @param password User's password
     * @return Flow emitting the authentication result
     */
    fun signInWithEmail(email: String, password: String): Flow<AuthResult>

    /**
     * Create a new Firebase Auth account with email and password.
     *
     * @param email    User's email address (pre-validated and trimmed by the use case).
     * @param password User's password (pre-validated by the use case).
     * @return Flow emitting the authentication result.
     */
    fun signUpWithEmail(email: String, password: String): Flow<AuthResult>

    /**
     * Check if a user exists in Firestore and register if necessary.
     *
     * @param userId The user ID to check and register
     * @return true if the operation was successful, false otherwise
     */
    suspend fun registerUserIfNeeded(userId: String): Boolean
}
