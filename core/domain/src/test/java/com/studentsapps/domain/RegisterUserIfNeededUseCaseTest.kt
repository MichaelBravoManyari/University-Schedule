package com.studentsapps.domain

import com.studentsapps.domain.login.repository.AuthRepository
import com.studentsapps.domain.login.usecases.RegisterUserIfNeededUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test

/**
 * Unit tests for [RegisterUserIfNeededUseCase].
 */
class RegisterUserIfNeededUseCaseTest {

    // Subject under test
    private lateinit var registerUserIfNeededUseCase: RegisterUserIfNeededUseCase

    // Dependencies
    private lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        // Given: Initialize mock dependencies
        authRepository = mockk(relaxed = true)
        registerUserIfNeededUseCase = RegisterUserIfNeededUseCase(authRepository)
    }

    @Test
    fun givenValidUserId_whenInvoked_thenCallsRepositoryAndReturnsTrue() = runTest {
        // Given: Valid user ID and successful repository response
        val userId = "user123"
        coEvery { authRepository.registerUserIfNeeded(userId) } returns true

        // When: Use case is invoked
        val result = registerUserIfNeededUseCase(userId)

        // Then: Repository is called and returns true
        coVerify(exactly = 1) { authRepository.registerUserIfNeeded(userId) }
        assertEquals(true, result)
    }

    @Test
    fun givenValidUserId_whenRepositoryReturnsFalse_thenReturnsFalse() = runTest {
        // Given: Valid user ID but repository operation fails
        val userId = "user456"
        coEvery { authRepository.registerUserIfNeeded(userId) } returns false

        // When: Use case is invoked
        val result = registerUserIfNeededUseCase(userId)

        // Then: Repository is called and returns false
        coVerify(exactly = 1) { authRepository.registerUserIfNeeded(userId) }
        assertEquals(false, result)
    }

    @Test
    fun givenBlankUserId_whenInvoked_thenThrowsIllegalArgumentException() = runTest {
        // Given: Blank user ID
        val blankUserId = ""

        // When & Then: Invoking use case throws IllegalArgumentException
        val exception = runCatching {
            registerUserIfNeededUseCase(blankUserId)
        }.exceptionOrNull()

        assertTrue(exception is IllegalArgumentException)
        assertEquals("User ID cannot be blank", exception?.message)
        coVerify(exactly = 0) { authRepository.registerUserIfNeeded(any()) }
    }

    @Test
    fun givenWhitespaceUserId_whenInvoked_thenThrowsIllegalArgumentException() = runTest {
        // Given: User ID with only whitespace
        val whitespaceUserId = "   "

        // When & Then: Invoking use case throws IllegalArgumentException
        val exception = runCatching {
            registerUserIfNeededUseCase(whitespaceUserId)
        }.exceptionOrNull()

        assertTrue(exception is IllegalArgumentException)
        assertEquals("User ID cannot be blank", exception?.message)
        coVerify(exactly = 0) { authRepository.registerUserIfNeeded(any()) }
    }
}