package com.studentsapps.domain

import com.studentsapps.domain.login.model.AuthError
import com.studentsapps.domain.login.model.AuthResult
import com.studentsapps.domain.login.repository.AuthRepository
import com.studentsapps.domain.login.usecases.SignInWithGoogleUseCase
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test

/**
 * Unit tests for [SignInWithGoogleUseCase].
 */
class SignInWithGoogleUseCaseTest {

    // Subject under test
    private lateinit var signInWithGoogleUseCase: SignInWithGoogleUseCase

    // Dependencies
    private lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        // Given: Initialize mock dependencies
        authRepository = mockk(relaxed = true)
        signInWithGoogleUseCase = SignInWithGoogleUseCase(authRepository)
    }

    @Test
    fun givenValidIdToken_whenInvoked_thenReturnsSuccessFlow() = runTest {
        // Given: Valid ID token and successful authentication
        val idToken = "valid.google.idtoken"
        val expectedUserId = "user123"
        val expectedResult = AuthResult.Success(expectedUserId)
        coEvery { authRepository.signInWithGoogle(idToken) } returns flowOf(expectedResult)

        // When: Use case is invoked
        val resultFlow = signInWithGoogleUseCase(idToken)
        val results = resultFlow.first()

        // Then: Repository is called and returns success
        assertTrue(results is AuthResult.Success)
        assertEquals(expectedUserId, (results as AuthResult.Success).userId)
    }

    @Test
    fun givenValidIdToken_whenAuthenticationFails_thenReturnsErrorFlow() = runTest {
        // Given: Valid ID token but authentication fails
        val idToken = "valid.google.idtoken"
        val authError = AuthError.NETWORK_ERROR
        val expectedResult = AuthResult.Failure(authError)
        coEvery { authRepository.signInWithGoogle(idToken) } returns flowOf(expectedResult)

        // When: Use case is invoked
        val resultFlow = signInWithGoogleUseCase(idToken)
        val results = resultFlow.first()

        // Then: Repository is called and returns error
        assertTrue(results is AuthResult.Failure)
        assertEquals(authError, (results as AuthResult.Failure).error)
    }

    @Test
    fun givenBlankIdToken_whenInvoked_thenThrowsIllegalArgumentException() = runTest {
        // Given: Blank ID token
        val blankIdToken = ""

        // When & Then: Invoking use case throws IllegalArgumentException
        val exception = runCatching {
            signInWithGoogleUseCase(blankIdToken)
        }.exceptionOrNull()

        assertTrue(exception is IllegalArgumentException)
        assertEquals("ID token cannot be blank", exception?.message)
    }

    @Test
    fun givenWhitespaceIdToken_whenInvoked_thenThrowsIllegalArgumentException() = runTest {
        // Given: ID token with only whitespace
        val whitespaceIdToken = "   "

        // When & Then: Invoking use case throws IllegalArgumentException
        val exception = runCatching {
            signInWithGoogleUseCase(whitespaceIdToken)
        }.exceptionOrNull()

        assertTrue(exception is IllegalArgumentException)
        assertEquals("ID token cannot be blank", exception?.message)
    }

    @Test
    fun givenRepositoryThrowsException_whenInvoked_thenExceptionIsPropagated() = runTest {
        // Given: Repository throws an exception
        val idToken = "valid.token"
        val expectedException = RuntimeException("Network error")
        coEvery { authRepository.signInWithGoogle(idToken) } throws expectedException

        // When & Then: Exception is propagated
        val exception = runCatching {
            signInWithGoogleUseCase(idToken)
        }.exceptionOrNull()

        assertEquals("Network error", exception?.message)
    }

    @Test
    fun givenDifferentIdTokens_whenInvoked_thenEachTokenIsPassedCorrectly() = runTest {
        // Given: Multiple different ID tokens
        val token1 = "token.one"
        val token2 = "token.two"
        val userId1 = "user1"
        val userId2 = "user2"

        coEvery { authRepository.signInWithGoogle(token1) } returns flowOf(AuthResult.Success(userId1))
        coEvery { authRepository.signInWithGoogle(token2) } returns flowOf(AuthResult.Success(userId2))

        // When: Use case is invoked with different tokens
        val result1 = signInWithGoogleUseCase(token1).first()
        val result2 = signInWithGoogleUseCase(token2).first()

        // Then: Each token is passed correctly to repository
        assertEquals(userId1, (result1 as AuthResult.Success).userId)
        assertEquals(userId2, (result2 as AuthResult.Success).userId)
    }
}