package com.studentsapps.data.repository

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.studentsapps.domain.login.model.AuthError
import com.studentsapps.domain.login.model.AuthResult
import com.studentsapps.domain.login.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Implementation of [AuthRepository] using Firebase Authentication.
 */
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {
    override fun signInWithGoogle(idToken: String): Flow<AuthResult> = callbackFlow {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val userId = result.user?.uid

            if (userId != null) {
                trySend(AuthResult.Success(userId))
            } else {
                trySend(AuthResult.Failure(AuthError.UNKNOWN_ERROR))
            }
        } catch (e: Exception) {
            trySend(AuthResult.Failure(mapFirebaseException(e)))
        }
        awaitClose()
    }

    override fun signInWithEmail(email: String, password: String): Flow<AuthResult> = callbackFlow {
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid

            if (userId != null) {
                trySend(AuthResult.Success(userId))
            } else {
                trySend(AuthResult.Failure(AuthError.UNKNOWN_ERROR))
            }
        } catch (e: Exception) {
            trySend(AuthResult.Failure(mapFirebaseException(e)))
        }
        awaitClose()
    }

    override fun signUpWithEmail(email: String, password: String): Flow<AuthResult> = callbackFlow {
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid

            if (userId != null) {
                trySend(AuthResult.Success(userId))
            } else {
                trySend(AuthResult.Failure(AuthError.UNKNOWN_ERROR))
            }
        } catch (e: Exception) {
            trySend(AuthResult.Failure(mapFirebaseException(e)))
        }
        awaitClose()
    }

    override suspend fun registerUserIfNeeded(userId: String): Boolean {
        return try {
            val userDocRef = firestore.collection("users").document(userId)
            val document = userDocRef.get().await()

            if (!document.exists()) {
                userDocRef.set(mapOf("userId" to userId)).await()
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Maps Firebase exceptions to domain-specific auth errors.
     */
    private fun mapFirebaseException(exception: Exception): AuthError {
        return when (exception) {
            is FirebaseAuthInvalidUserException -> AuthError.USER_NOT_FOUND
            is FirebaseAuthInvalidCredentialsException -> AuthError.INVALID_CREDENTIALS
            is FirebaseAuthUserCollisionException -> AuthError.EMAIL_ALREADY_IN_USE
            is FirebaseNetworkException -> AuthError.NETWORK_ERROR
            else -> AuthError.UNKNOWN_ERROR
        }
    }
}