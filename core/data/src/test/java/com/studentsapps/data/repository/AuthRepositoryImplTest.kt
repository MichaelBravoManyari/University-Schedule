package com.studentsapps.data.repository

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.studentsapps.domain.login.model.AuthError
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for [AuthRepositoryImpl].
 */
class AuthRepositoryImplTest {
    // System under test
    private lateinit var authRepository: AuthRepositoryImpl

    // Mocked dependencies
    private lateinit var mockFirebaseAuth: FirebaseAuth
    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockFirebaseUser: FirebaseUser
    private lateinit var mockAuthResult: AuthResult
    private lateinit var mockDocumentReference: DocumentReference
    private lateinit var mockDocumentSnapshot: DocumentSnapshot

    @Before
    fun setup() {
        // Initialize mocks
        mockFirebaseAuth = mockk(relaxed = true)
        mockFirestore = mockk(relaxed = true)
        mockFirebaseUser = mockk(relaxed = true)
        mockAuthResult = mockk(relaxed = true)
        mockDocumentReference = mockk(relaxed = true)
        mockDocumentSnapshot = mockk(relaxed = true)

        // Create system under test
        authRepository = AuthRepositoryImpl(
            firebaseAuth = mockFirebaseAuth,
            firestore = mockFirestore
        )
    }

    // ========================================
    // Sign In with Google Tests
    // ========================================

    @Test
    fun givenValidGoogleIdToken_whenSignInWithGoogle_thenReturnsSuccessWithUserId() =
        runTest {
            // Given
            val testIdToken = "test_id_token_123"
            val expectedUserId = "user_123"

            every { mockAuthResult.user } returns mockFirebaseUser
            every { mockFirebaseUser.uid } returns expectedUserId

            val mockTask = mockSuccessfulTask(mockAuthResult)
            every { mockFirebaseAuth.signInWithCredential(any()) } returns mockTask

            // When
            val result = authRepository.signInWithGoogle(testIdToken).first()

            // Then
            assertTrue(result is com.studentsapps.domain.login.model.AuthResult.Success)
            assertEquals(
                expectedUserId,
                (result as com.studentsapps.domain.login.model.AuthResult.Success).userId
            )

            verify { mockFirebaseAuth.signInWithCredential(any()) }
        }

    @Test
    fun givenNullUser_whenSignInWithGoogle_thenReturnsFailureWithUnknownError() =
        runTest {
            // Given
            val testIdToken = "test_id_token"

            every { mockAuthResult.user } returns null

            val mockTask = mockSuccessfulTask(mockAuthResult)
            every { mockFirebaseAuth.signInWithCredential(any()) } returns mockTask

            // When
            val result = authRepository.signInWithGoogle(testIdToken).first()

            // Then
            assertTrue(result is com.studentsapps.domain.login.model.AuthResult.Failure)
            assertEquals(
                AuthError.UNKNOWN_ERROR,
                (result as com.studentsapps.domain.login.model.AuthResult.Failure).error
            )
        }

    @Test
    fun givenNetworkException_whenSignInWithGoogle_thenReturnsFailureWithNetworkError() =
        runTest {
            // Given
            val testIdToken = "test_id_token"
            val firebaseNetworkException = mockk<FirebaseNetworkException>(relaxed = true)

            val mockTask = mockFailedTask<AuthResult>(firebaseNetworkException)
            every { mockFirebaseAuth.signInWithCredential(any()) } returns mockTask

            // When
            val result = authRepository.signInWithGoogle(testIdToken).first()

            // Then
            assertTrue(result is com.studentsapps.domain.login.model.AuthResult.Failure)
            assertEquals(
                AuthError.NETWORK_ERROR,
                (result as com.studentsapps.domain.login.model.AuthResult.Failure).error
            )
        }

    @Test
    fun givenInvalidCredentialsException_whenSignInWithGoogle_thenReturnsFailureWithInvalidCredentialsError() = runTest {
        // Given
        val testIdToken = "invalid_token"
        val invalidCredentialsException = mockk<FirebaseAuthInvalidCredentialsException>(relaxed = true)

        val mockTask = mockFailedTask<AuthResult>(invalidCredentialsException)
        every { mockFirebaseAuth.signInWithCredential(any()) } returns mockTask

        // When
        val result = authRepository.signInWithGoogle(testIdToken).first()

        // Then
        assertTrue(result is com.studentsapps.domain.login.model.AuthResult.Failure)
        assertEquals(AuthError.INVALID_CREDENTIALS, (result as com.studentsapps.domain.login.model.AuthResult.Failure).error)
    }

    // ========================================
    // Register User If Needed Tests
    // ========================================

    @Test
    fun givenNewUser_whenRegisterUserIfNeeded_thenCreatesUserDocumentAndReturnsTrue() = runTest {
        // Given
        val testUserId = "new_user_123"

        every { mockFirestore.collection("users") } returns mockk {
            every { document(testUserId) } returns mockDocumentReference
        }

        every { mockDocumentSnapshot.exists() } returns false

        val getTask = mockSuccessfulTask(mockDocumentSnapshot)
        val setTask = mockSuccessfulTask(mockk<Void>())

        every { mockDocumentReference.get() } returns getTask
        every { mockDocumentReference.set(any<Map<String, Any>>()) } returns setTask

        // When
        val result = authRepository.registerUserIfNeeded(testUserId)

        // Then
        assertTrue(result)
        verify { mockDocumentReference.get() }
        verify { mockDocumentReference.set(mapOf("userId" to testUserId)) }
    }

    @Test
    fun givenExistingUser_whenRegisterUserIfNeeded_thenDoesNotCreateDocumentAndReturnsTrue() = runTest {
        // Given
        val testUserId = "existing_user_456"

        every { mockFirestore.collection("users") } returns mockk {
            every { document(testUserId) } returns mockDocumentReference
        }

        every { mockDocumentSnapshot.exists() } returns true

        val getTask = mockSuccessfulTask(mockDocumentSnapshot)
        every { mockDocumentReference.get() } returns getTask

        // When
        val result = authRepository.registerUserIfNeeded(testUserId)

        // Then
        assertTrue(result)
        verify { mockDocumentReference.get() }
        verify(exactly = 0) { mockDocumentReference.set(any<Map<String, Any>>()) }
    }

    @Test
    fun givenFirestoreError_whenRegisterUserIfNeeded_thenReturnsFalse() = runTest {
        // Given
        val testUserId = "user_789"

        every { mockFirestore.collection("users") } returns mockk {
            every { document(testUserId) } returns mockDocumentReference
        }

        val getTask = mockFailedTask<DocumentSnapshot>(Exception("Firestore error"))
        every { mockDocumentReference.get() } returns getTask

        // When
        val result = authRepository.registerUserIfNeeded(testUserId)

        // Then
        assertFalse(result)
    }

    @Test
    fun givenDocumentCreationFails_whenRegisterUserIfNeeded_thenReturnsFalse() = runTest {
        // Given
        val testUserId = "user_fail_create"

        every { mockFirestore.collection("users") } returns mockk {
            every { document(testUserId) } returns mockDocumentReference
        }

        every { mockDocumentSnapshot.exists() } returns false

        val getTask = mockSuccessfulTask(mockDocumentSnapshot)
        val setTask = mockFailedTask<Void>(Exception("Creation failed"))

        every { mockDocumentReference.get() } returns getTask
        every { mockDocumentReference.set(any<Map<String, Any>>()) } returns setTask

        // When
        val result = authRepository.registerUserIfNeeded(testUserId)

        // Then
        assertFalse(result)
    }

    // ========================================
    // Error Mapping Tests
    // ========================================

    @Test
    fun givenFirebaseAuthInvalidUserException_whenSignInWithGoogle_thenMapsToUserNotFound() = runTest {
        // Given
        val invalidUserException = mockk<FirebaseAuthInvalidUserException>(relaxed = true)
        val mockTask = mockFailedTask<AuthResult>(invalidUserException)
        every { mockFirebaseAuth.signInWithCredential(any()) } returns mockTask

        // When
        val result = authRepository.signInWithGoogle("token").first()

        // Then
        assertTrue(result is com.studentsapps.domain.login.model.AuthResult.Failure)
        assertEquals(AuthError.USER_NOT_FOUND, (result as com.studentsapps.domain.login.model.AuthResult.Failure).error)
    }

    @Test
    fun givenUnknownException_whenSignInWithGoogle_thenMapsToUnknownError() = runTest {
        // Given
        val unknownException = RuntimeException("Unknown error")
        val mockTask = mockFailedTask<AuthResult>(unknownException)
        every { mockFirebaseAuth.signInWithCredential(any()) } returns mockTask

        // When
        val result = authRepository.signInWithGoogle("token").first()

        // Then
        assertTrue(result is com.studentsapps.domain.login.model.AuthResult.Failure)
        assertEquals(AuthError.UNKNOWN_ERROR, (result as com.studentsapps.domain.login.model.AuthResult.Failure).error)
    }

    // ========================================
    // Sign In with Email Tests
    // ========================================

    @Test
    fun givenValidEmailAndPassword_whenSignInWithEmail_thenReturnsSuccessWithUserId() = runTest {
        // Given
        val testEmail = "user@example.com"
        val testPassword = "securePassword123"
        val expectedUserId = "user_abc_123"

        every { mockAuthResult.user } returns mockFirebaseUser
        every { mockFirebaseUser.uid } returns expectedUserId

        val mockTask = mockSuccessfulTask(mockAuthResult)
        every { mockFirebaseAuth.signInWithEmailAndPassword(testEmail, testPassword) } returns mockTask

        // When
        val result = authRepository.signInWithEmail(testEmail, testPassword).first()

        // Then
        assertTrue(result is com.studentsapps.domain.login.model.AuthResult.Success)
        assertEquals(
            expectedUserId,
            (result as com.studentsapps.domain.login.model.AuthResult.Success).userId
        )
        verify { mockFirebaseAuth.signInWithEmailAndPassword(testEmail, testPassword) }
    }

    @Test
    fun givenInvalidCredentialsException_whenSignInWithEmail_thenReturnsFailureWithInvalidCredentialsError() = runTest {
        // Given
        val testEmail = "user@example.com"
        val testPassword = "wrongPassword"
        val invalidCredentialsException = mockk<FirebaseAuthInvalidCredentialsException>(relaxed = true)

        val mockTask = mockFailedTask<AuthResult>(invalidCredentialsException)
        every { mockFirebaseAuth.signInWithEmailAndPassword(testEmail, testPassword) } returns mockTask

        // When
        val result = authRepository.signInWithEmail(testEmail, testPassword).first()

        // Then
        assertTrue(result is com.studentsapps.domain.login.model.AuthResult.Failure)
        assertEquals(
            AuthError.INVALID_CREDENTIALS,
            (result as com.studentsapps.domain.login.model.AuthResult.Failure).error
        )
    }

    @Test
    fun givenInvalidUserException_whenSignInWithEmail_thenReturnsFailureWithUserNotFoundError() = runTest {
        // Given
        val testEmail = "nonexistent@example.com"
        val testPassword = "somePassword"
        val invalidUserException = mockk<FirebaseAuthInvalidUserException>(relaxed = true)

        val mockTask = mockFailedTask<AuthResult>(invalidUserException)
        every { mockFirebaseAuth.signInWithEmailAndPassword(testEmail, testPassword) } returns mockTask

        // When
        val result = authRepository.signInWithEmail(testEmail, testPassword).first()

        // Then
        assertTrue(result is com.studentsapps.domain.login.model.AuthResult.Failure)
        assertEquals(
            AuthError.USER_NOT_FOUND,
            (result as com.studentsapps.domain.login.model.AuthResult.Failure).error
        )
    }

    @Test
    fun givenNetworkException_whenSignInWithEmail_thenReturnsFailureWithNetworkError() = runTest {
        // Given
        val testEmail = "user@example.com"
        val testPassword = "securePassword123"
        val networkException = mockk<FirebaseNetworkException>(relaxed = true)

        val mockTask = mockFailedTask<AuthResult>(networkException)
        every { mockFirebaseAuth.signInWithEmailAndPassword(testEmail, testPassword) } returns mockTask

        // When
        val result = authRepository.signInWithEmail(testEmail, testPassword).first()

        // Then
        assertTrue(result is com.studentsapps.domain.login.model.AuthResult.Failure)
        assertEquals(
            AuthError.NETWORK_ERROR,
            (result as com.studentsapps.domain.login.model.AuthResult.Failure).error
        )
    }

    @Test
    fun givenEmailAlreadyInUseException_whenSignInWithEmail_thenReturnsFailureWithEmailAlreadyInUseError() = runTest {
        // Given
        val testEmail = "taken@example.com"
        val testPassword = "somePassword"
        val collisionException = mockk<FirebaseAuthUserCollisionException>(relaxed = true)

        val mockTask = mockFailedTask<AuthResult>(collisionException)
        every { mockFirebaseAuth.signInWithEmailAndPassword(testEmail, testPassword) } returns mockTask

        // When
        val result = authRepository.signInWithEmail(testEmail, testPassword).first()

        // Then
        assertTrue(result is com.studentsapps.domain.login.model.AuthResult.Failure)
        assertEquals(
            AuthError.EMAIL_ALREADY_IN_USE,
            (result as com.studentsapps.domain.login.model.AuthResult.Failure).error
        )
    }

    @Test
    fun givenUnknownException_whenSignInWithEmail_thenReturnsFailureWithUnknownError() = runTest {
        // Given
        val testEmail = "user@example.com"
        val testPassword = "securePassword123"
        val unknownException = RuntimeException("Unexpected error")

        val mockTask = mockFailedTask<AuthResult>(unknownException)
        every { mockFirebaseAuth.signInWithEmailAndPassword(testEmail, testPassword) } returns mockTask

        // When
        val result = authRepository.signInWithEmail(testEmail, testPassword).first()

        // Then
        assertTrue(result is com.studentsapps.domain.login.model.AuthResult.Failure)
        assertEquals(
            AuthError.UNKNOWN_ERROR,
            (result as com.studentsapps.domain.login.model.AuthResult.Failure).error
        )
    }

    @Test
    fun givenValidCredentials_whenSignInWithEmail_thenCallsFirebaseWithExactEmailAndPassword() = runTest {
        // Given
        val testEmail = "user@example.com"
        val testPassword = "securePassword123"

        every { mockAuthResult.user } returns mockFirebaseUser
        every { mockFirebaseUser.uid } returns "uid_xyz"

        val mockTask = mockSuccessfulTask(mockAuthResult)
        every { mockFirebaseAuth.signInWithEmailAndPassword(any(), any()) } returns mockTask

        // When
        authRepository.signInWithEmail(testEmail, testPassword).first()

        // Then — verifies credentials are forwarded as-is, without mutation
        verify(exactly = 1) {
            mockFirebaseAuth.signInWithEmailAndPassword(testEmail, testPassword)
        }
    }

    // ========================================
    // Sign Up with Email — Happy Path
    // ========================================

    @Test
    fun givenValidEmailAndPassword_whenSignUpWithEmail_thenReturnsSuccessWithUserId() = runTest {
        // Given
        val testEmail = "newuser@example.com"
        val testPassword = "SecurePass123!"
        val expectedUserId = "new_user_uid_001"

        every { mockAuthResult.user } returns mockFirebaseUser
        every { mockFirebaseUser.uid } returns expectedUserId

        val mockTask = mockSuccessfulTask(mockAuthResult)
        every {
            mockFirebaseAuth.createUserWithEmailAndPassword(testEmail, testPassword)
        } returns mockTask

        // When
        val result = authRepository.signUpWithEmail(testEmail, testPassword).first()

        // Then
        assertTrue(result is com.studentsapps.domain.login.model.AuthResult.Success)
        assertEquals(
            expectedUserId,
            (result as com.studentsapps.domain.login.model.AuthResult.Success).userId
        )
    }

    @Test
    fun givenValidCredentials_whenSignUpWithEmail_thenCallsFirebaseWithExactEmailAndPassword() =
        runTest {
            // Given
            val testEmail = "newuser@example.com"
            val testPassword = "SecurePass123!"

            every { mockAuthResult.user } returns mockFirebaseUser
            every { mockFirebaseUser.uid } returns "uid_xyz"

            val mockTask = mockSuccessfulTask(mockAuthResult)
            every { mockFirebaseAuth.createUserWithEmailAndPassword(any(), any()) } returns mockTask

            // When
            authRepository.signUpWithEmail(testEmail, testPassword).first()

            // Then — las credenciales se reenvían a Firebase sin ninguna mutación
            verify(exactly = 1) {
                mockFirebaseAuth.createUserWithEmailAndPassword(testEmail, testPassword)
            }
        }

    // ========================================
    // Sign Up with Email — Null User
    // ========================================

    @Test
    fun givenNullUserAfterSignUp_whenSignUpWithEmail_thenReturnsFailureWithUnknownError() =
        runTest {
            // Given
            every { mockAuthResult.user } returns null

            val mockTask = mockSuccessfulTask(mockAuthResult)
            every {
                mockFirebaseAuth.createUserWithEmailAndPassword(any(), any())
            } returns mockTask

            // When
            val result = authRepository.signUpWithEmail("user@example.com", "pass").first()

            // Then
            assertTrue(result is com.studentsapps.domain.login.model.AuthResult.Failure)
            assertEquals(
                AuthError.UNKNOWN_ERROR,
                (result as com.studentsapps.domain.login.model.AuthResult.Failure).error
            )
        }

    // ========================================
    // Sign Up with Email — Firebase Exception Mapping
    // ========================================

    @Test
    fun givenEmailAlreadyInUseException_whenSignUpWithEmail_thenReturnsFailureWithEmailAlreadyInUseError() =
        runTest {
            // Given: El email ya está registrado en Firebase
            val testEmail = "taken@example.com"
            val testPassword = "SecurePass123!"
            val collisionException = mockk<FirebaseAuthUserCollisionException>(relaxed = true)

            val mockTask = mockFailedTask<AuthResult>(collisionException)
            every {
                mockFirebaseAuth.createUserWithEmailAndPassword(testEmail, testPassword)
            } returns mockTask

            // When
            val result = authRepository.signUpWithEmail(testEmail, testPassword).first()

            // Then
            assertTrue(result is com.studentsapps.domain.login.model.AuthResult.Failure)
            assertEquals(
                AuthError.EMAIL_ALREADY_IN_USE,
                (result as com.studentsapps.domain.login.model.AuthResult.Failure).error
            )
        }

    @Test
    fun givenWeakPasswordException_whenSignUpWithEmail_thenReturnsFailureWithInvalidCredentialsError() =
        runTest {
            // Given: Firebase rechaza la contraseña por ser demasiado débil;
            // Firebase lanza FirebaseAuthInvalidCredentialsException para contraseñas débiles
            val testEmail = "user@example.com"
            val testPassword = "123"
            val weakPasswordException =
                mockk<FirebaseAuthInvalidCredentialsException>(relaxed = true)

            val mockTask = mockFailedTask<AuthResult>(weakPasswordException)
            every {
                mockFirebaseAuth.createUserWithEmailAndPassword(testEmail, testPassword)
            } returns mockTask

            // When
            val result = authRepository.signUpWithEmail(testEmail, testPassword).first()

            // Then
            assertTrue(result is com.studentsapps.domain.login.model.AuthResult.Failure)
            assertEquals(
                AuthError.INVALID_CREDENTIALS,
                (result as com.studentsapps.domain.login.model.AuthResult.Failure).error
            )
        }

    @Test
    fun givenNetworkException_whenSignUpWithEmail_thenReturnsFailureWithNetworkError() = runTest {
        // Given: No hay conexión a internet durante el registro
        val testEmail = "user@example.com"
        val testPassword = "SecurePass123!"
        val networkException = mockk<FirebaseNetworkException>(relaxed = true)

        val mockTask = mockFailedTask<AuthResult>(networkException)
        every {
            mockFirebaseAuth.createUserWithEmailAndPassword(testEmail, testPassword)
        } returns mockTask

        // When
        val result = authRepository.signUpWithEmail(testEmail, testPassword).first()

        // Then
        assertTrue(result is com.studentsapps.domain.login.model.AuthResult.Failure)
        assertEquals(
            AuthError.NETWORK_ERROR,
            (result as com.studentsapps.domain.login.model.AuthResult.Failure).error
        )
    }

    @Test
    fun givenUnknownException_whenSignUpWithEmail_thenReturnsFailureWithUnknownError() = runTest {
        // Given: Se lanza una excepción inesperada no mapeada
        val testEmail = "user@example.com"
        val testPassword = "SecurePass123!"
        val unknownException = RuntimeException("Unexpected error")

        val mockTask = mockFailedTask<AuthResult>(unknownException)
        every {
            mockFirebaseAuth.createUserWithEmailAndPassword(testEmail, testPassword)
        } returns mockTask

        // When
        val result = authRepository.signUpWithEmail(testEmail, testPassword).first()

        // Then
        assertTrue(result is com.studentsapps.domain.login.model.AuthResult.Failure)
        assertEquals(
            AuthError.UNKNOWN_ERROR,
            (result as com.studentsapps.domain.login.model.AuthResult.Failure).error
        )
    }

    // ========================================
    // Helper Methods
    // ========================================

    /**
     * Creates a successful Task mock that immediately returns the given result.
     */
    private fun <T> mockSuccessfulTask(result: T): Task<T> {
        return Tasks.forResult(result)
    }

    /**
     * Creates a failed Task mock that immediately throws the given exception.
     */
    private fun <T> mockFailedTask(exception: Exception): Task<T> {
        return Tasks.forException(exception)
    }
}