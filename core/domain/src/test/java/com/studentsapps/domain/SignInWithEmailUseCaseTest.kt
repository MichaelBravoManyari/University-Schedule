package com.studentsapps.domain

import com.studentsapps.domain.login.model.AuthError
import com.studentsapps.domain.login.model.AuthResult
import com.studentsapps.domain.login.model.EmailSignInError
import com.studentsapps.domain.login.repository.AuthRepository
import com.studentsapps.domain.login.usecases.SignInWithEmailUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for [SignInWithEmailUseCase].
 */
class SignInWithEmailUseCaseTest {

    // Subject under test
    private lateinit var signInWithEmailUseCase: SignInWithEmailUseCase

    // Dependencies
    private lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        authRepository = mockk(relaxed = true)
        signInWithEmailUseCase = SignInWithEmailUseCase(authRepository)
    }

    // ========================================
    // Happy Path Tests
    // ========================================

    @Test
    fun givenValidEmailAndPassword_whenInvoked_thenDelegatesToRepositoryAndReturnsSuccess() = runTest {
        // Given
        val email = "user@example.com"
        val password = "securePassword123"
        val expectedUserId = "uid_001"

        every { authRepository.signInWithEmail(email, password) } returns
                flowOf(AuthResult.Success(expectedUserId))

        // When
        val result = signInWithEmailUseCase(email, password).first()

        // Then
        assertTrue(result is AuthResult.Success)
        assertEquals(expectedUserId, result.userId)
        verify(exactly = 1) { authRepository.signInWithEmail(email, password) }
    }

    @Test
    fun givenValidEmailAndPassword_whenRepositoryReturnsFailure_thenReturnsFailure() = runTest {
        // Given
        val email = "user@example.com"
        val password = "wrongPassword"

        every { authRepository.signInWithEmail(email, password) } returns
                flowOf(AuthResult.Failure(AuthError.INVALID_CREDENTIALS))

        // When
        val result = signInWithEmailUseCase(email, password).first()

        // Then
        assertTrue(result is AuthResult.Failure)
        assertEquals(AuthError.INVALID_CREDENTIALS, result.error)
        verify(exactly = 1) { authRepository.signInWithEmail(email, password) }
    }

    // ========================================
    // Email Trimming Tests
    // ========================================

    @Test
    fun givenEmailWithLeadingAndTrailingSpaces_whenInvoked_thenTrimmedEmailIsForwardedToRepository() = runTest {
        // Given
        val emailWithSpaces = "  user@example.com  "
        val trimmedEmail = "user@example.com"
        val password = "securePassword123"

        every { authRepository.signInWithEmail(trimmedEmail, password) } returns
                flowOf(AuthResult.Success("uid_002"))

        // When
        signInWithEmailUseCase(emailWithSpaces, password).first()

        // Then
        verify(exactly = 1) { authRepository.signInWithEmail(trimmedEmail, password) }
    }

    @Test
    fun givenEmailWithLeadingAndTrailingSpaces_whenInvoked_thenUntrimmedEmailIsNeverForwardedToRepository() = runTest {
        // Given
        val emailWithSpaces = "  user@example.com  "
        val password = "securePassword123"

        every { authRepository.signInWithEmail(any(), any()) } returns
                flowOf(AuthResult.Success("uid_003"))

        // When
        signInWithEmailUseCase(emailWithSpaces, password).first()

        // Then
        verify(exactly = 0) { authRepository.signInWithEmail(emailWithSpaces, password) }
    }

    // ========================================
    // Blank Email Validation Tests
    // ========================================

    @Test
    fun givenBlankEmail_whenInvoked_thenReturnsValidationFailureWithBlankEmailError() = runTest {
        // Given
        val blankEmail = ""
        val password = "securePassword123"

        // When
        val result = signInWithEmailUseCase(blankEmail, password).first()

        // Then
        assertTrue(result is AuthResult.ValidationFailure)
        assertEquals(EmailSignInError.BLANK_EMAIL, result.error)
    }

    @Test
    fun givenWhitespaceOnlyEmail_whenInvoked_thenReturnsValidationFailureWithBlankEmailError() = runTest {
        // Given
        val whitespaceEmail = "    "
        val password = "securePassword123"

        // When
        val result = signInWithEmailUseCase(whitespaceEmail, password).first()

        // Then
        assertTrue(result is AuthResult.ValidationFailure)
        assertEquals(EmailSignInError.BLANK_EMAIL, result.error)
    }

    @Test
    fun givenBlankEmail_whenInvoked_thenRepositoryIsNeverCalled() = runTest {
        // Given
        val blankEmail = ""
        val password = "securePassword123"

        // When
        signInWithEmailUseCase(blankEmail, password).first()

        // Then
        verify(exactly = 0) { authRepository.signInWithEmail(any(), any()) }
    }

    // ========================================
    // Invalid Email Format Validation Tests
    // ========================================

    @Test
    fun givenEmailWithoutAtSymbol_whenInvoked_thenReturnsValidationFailureWithInvalidEmailFormatError() = runTest {
        // Given
        val invalidEmail = "userexample.com"
        val password = "securePassword123"

        // When
        val result = signInWithEmailUseCase(invalidEmail, password).first()

        // Then
        assertTrue(result is AuthResult.ValidationFailure)
        assertEquals(EmailSignInError.INVALID_EMAIL_FORMAT, result.error)
    }

    @Test
    fun givenEmailWithoutDomain_whenInvoked_thenReturnsValidationFailureWithInvalidEmailFormatError() = runTest {
        // Given
        val invalidEmail = "user@"
        val password = "securePassword123"

        // When
        val result = signInWithEmailUseCase(invalidEmail, password).first()

        // Then
        assertTrue(result is AuthResult.ValidationFailure)
        assertEquals(EmailSignInError.INVALID_EMAIL_FORMAT, result.error)
    }

    @Test
    fun givenEmailWithoutTld_whenInvoked_thenReturnsValidationFailureWithInvalidEmailFormatError() = runTest {
        // Given
        val invalidEmail = "user@example"
        val password = "securePassword123"

        // When
        val result = signInWithEmailUseCase(invalidEmail, password).first()

        // Then
        assertTrue(result is AuthResult.ValidationFailure)
        assertEquals(EmailSignInError.INVALID_EMAIL_FORMAT, result.error)
    }

    @Test
    fun givenInvalidEmail_whenInvoked_thenRepositoryIsNeverCalled() = runTest {
        // Given
        val invalidEmail = "not-an-email"
        val password = "securePassword123"

        // When
        signInWithEmailUseCase(invalidEmail, password).first()

        // Then
        verify(exactly = 0) { authRepository.signInWithEmail(any(), any()) }
    }

    // ========================================
    // Blank Password Validation Tests
    // ========================================

    @Test
    fun givenValidEmailAndBlankPassword_whenInvoked_thenReturnsValidationFailureWithBlankPasswordError() = runTest {
        // Given
        val email = "user@example.com"
        val blankPassword = ""

        // When
        val result = signInWithEmailUseCase(email, blankPassword).first()

        // Then
        assertTrue(result is AuthResult.ValidationFailure)
        assertEquals(EmailSignInError.BLANK_PASSWORD, result.error)
    }

    @Test
    fun givenValidEmailAndWhitespaceOnlyPassword_whenInvoked_thenReturnsValidationFailureWithBlankPasswordError() = runTest {
        // Given
        val email = "user@example.com"
        val whitespacePassword = "   "

        // When
        val result = signInWithEmailUseCase(email, whitespacePassword).first()

        // Then
        assertTrue(result is AuthResult.ValidationFailure)
        assertEquals(EmailSignInError.BLANK_PASSWORD, result.error)
    }

    @Test
    fun givenValidEmailAndBlankPassword_whenInvoked_thenRepositoryIsNeverCalled() = runTest {
        // Given
        val email = "user@example.com"
        val blankPassword = ""

        // When
        signInWithEmailUseCase(email, blankPassword).first()

        // Then
        verify(exactly = 0) { authRepository.signInWithEmail(any(), any()) }
    }

    // ========================================
    // Validation Order Tests
    // ========================================

    @Test
    fun givenBlankEmailAndBlankPassword_whenInvoked_thenReturnsBlankEmailErrorFirst() = runTest {
        // Given — email is evaluated before password per the validation rules
        val blankEmail = ""
        val blankPassword = ""

        // When
        val result = signInWithEmailUseCase(blankEmail, blankPassword).first()

        // Then
        assertTrue(result is AuthResult.ValidationFailure)
        assertEquals(EmailSignInError.BLANK_EMAIL, result.error)
    }

    @Test
    fun givenInvalidEmailFormatAndBlankPassword_whenInvoked_thenReturnsInvalidEmailFormatErrorFirst() = runTest {
        // Given — format is evaluated before password per the validation rules
        val invalidEmail = "not-an-email"
        val blankPassword = ""

        // When
        val result = signInWithEmailUseCase(invalidEmail, blankPassword).first()

        // Then
        assertTrue(result is AuthResult.ValidationFailure)
        assertEquals(EmailSignInError.INVALID_EMAIL_FORMAT, result.error)
    }
}