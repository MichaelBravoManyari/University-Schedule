package com.mbm.login

import app.cash.turbine.test
import com.mbm.login.email_login.EmailLoginViewModel
import com.studentsapps.domain.login.model.AuthError
import com.studentsapps.domain.login.model.AuthResult
import com.studentsapps.domain.login.model.EmailSignInError
import com.studentsapps.domain.login.usecases.SignInWithEmailUseCase
import com.studentsapps.domain.sync.usecases.StartSyncIfNeededUseCase
import com.studentsapps.login.R
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for [com.mbm.login.email_login.EmailLoginViewModel].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EmailLoginViewModelTest {

    // Test dispatcher for controlling coroutine execution
    private val testDispatcher = StandardTestDispatcher()

    // Subject under test
    private lateinit var emailLoginViewModel: EmailLoginViewModel

    // Dependencies
    private lateinit var signInWithEmailUseCase: SignInWithEmailUseCase
    private lateinit var startSyncIfNeededUseCase: StartSyncIfNeededUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        signInWithEmailUseCase = mockk()
        startSyncIfNeededUseCase = mockk()

        emailLoginViewModel = EmailLoginViewModel(
            signInWithEmailUseCase = signInWithEmailUseCase,
            startSyncIfNeededUseCase = startSyncIfNeededUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ========================================
    // Initial State Tests
    // ========================================

    @Test
    fun givenViewModelInitialized_whenNoActionTaken_thenHasCorrectDefaultValues() = runTest {
        // Given: ViewModel is initialized
        // When: No action is taken
        emailLoginViewModel.uiState.test {
            val initialState = awaitItem()

            // Then: All fields reflect the default EmailLoginUiState
            assertEquals("", initialState.email)
            assertEquals("", initialState.password)
            assertFalse(initialState.isPasswordVisible)
            assertNull(initialState.emailError)
            assertNull(initialState.passwordError)
            assertFalse(initialState.isLoading)
            assertNull(initialState.error)
            assertFalse(initialState.shouldNavigateToSchedule)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========================================
    // Form Field Event Tests
    // ========================================

    @Test
    fun givenInitialState_whenOnEmailChange_thenEmailIsUpdated() = runTest {
        // Given: ViewModel in initial state
        val newEmail = "user@example.com"

        // When
        emailLoginViewModel.uiState.test {
            awaitItem() // Initial state

            emailLoginViewModel.onEmailChange(newEmail)

            // Then
            val updatedState = awaitItem()
            assertEquals(newEmail, updatedState.email)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenEmailErrorPresent_whenOnEmailChange_thenEmailErrorIsCleared() = runTest {
        // Given: Trigger a blank-email validation error first
        every { signInWithEmailUseCase(any(), any()) } returns
                flowOf(AuthResult.ValidationFailure(EmailSignInError.BLANK_EMAIL))

        emailLoginViewModel.uiState.test {
            awaitItem() // Initial state

            emailLoginViewModel.onLoginClick()
            advanceUntilIdle()

            awaitItem() // isLoading = true
            val errorState = awaitItem()
            assertNotNull(errorState.emailError)

            // When: User starts correcting the field
            emailLoginViewModel.onEmailChange("u")

            // Then: Error is cleared immediately
            val clearedState = awaitItem()
            assertNull(clearedState.emailError)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenInitialState_whenOnPasswordChange_thenPasswordIsUpdated() = runTest {
        // Given: ViewModel in initial state
        val newPassword = "securePass123"

        // When
        emailLoginViewModel.uiState.test {
            awaitItem() // Initial state

            emailLoginViewModel.onPasswordChange(newPassword)

            // Then
            val updatedState = awaitItem()
            assertEquals(newPassword, updatedState.password)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenPasswordErrorPresent_whenOnPasswordChange_thenPasswordErrorIsCleared() = runTest {
        // Given: Trigger a blank-password validation error first
        every { signInWithEmailUseCase(any(), any()) } returns
                flowOf(AuthResult.ValidationFailure(EmailSignInError.BLANK_PASSWORD))

        emailLoginViewModel.uiState.test {
            awaitItem() // Initial state

            emailLoginViewModel.onLoginClick()
            advanceUntilIdle()

            awaitItem() // isLoading = true
            val errorState = awaitItem()
            assertNotNull(errorState.passwordError)

            // When: User starts correcting the field
            emailLoginViewModel.onPasswordChange("p")

            // Then: Error is cleared immediately
            val clearedState = awaitItem()
            assertNull(clearedState.passwordError)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenPasswordNotVisible_whenOnTogglePasswordVisibility_thenPasswordBecomesVisible() = runTest {
        // Given: Password is hidden (default)
        emailLoginViewModel.uiState.test {
            val initialState = awaitItem()
            assertFalse(initialState.isPasswordVisible)

            // When
            emailLoginViewModel.onTogglePasswordVisibility()

            // Then
            val toggledState = awaitItem()
            assertTrue(toggledState.isPasswordVisible)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenPasswordVisible_whenOnTogglePasswordVisibility_thenPasswordBecomesHidden() = runTest {
        // Given: Make password visible first
        emailLoginViewModel.uiState.test {
            awaitItem() // Initial state

            emailLoginViewModel.onTogglePasswordVisibility()
            awaitItem() // isPasswordVisible = true

            // When: Toggle again
            emailLoginViewModel.onTogglePasswordVisibility()

            // Then
            val hiddenState = awaitItem()
            assertFalse(hiddenState.isPasswordVisible)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========================================
    // Successful Sign-In Tests
    // ========================================

    @Test
    fun givenValidCredentials_whenOnLoginClick_thenShowsLoadingThenNavigates() = runTest {
        // Given
        val userId = "uid_001"
        every { signInWithEmailUseCase(any(), any()) } returns
                flowOf(AuthResult.Success(userId))
        coEvery { startSyncIfNeededUseCase() } just runs

        // When
        emailLoginViewModel.uiState.test {
            val initialState = awaitItem()
            assertFalse(initialState.isLoading)
            assertFalse(initialState.shouldNavigateToSchedule)

            emailLoginViewModel.onLoginClick()
            advanceUntilIdle()

            // Then: Loading state emitted first
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertNull(loadingState.error)

            // Then: Success state
            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertTrue(successState.shouldNavigateToSchedule)
            assertNull(successState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenValidCredentials_whenOnLoginClick_thenStartsSynchronization() = runTest {
        // Given
        val userId = "uid_002"
        every { signInWithEmailUseCase(any(), any()) } returns
                flowOf(AuthResult.Success(userId))
        coEvery { startSyncIfNeededUseCase() } just runs

        // When
        emailLoginViewModel.onLoginClick()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { startSyncIfNeededUseCase() }
    }

    @Test
    fun givenValidCredentials_whenOnLoginClick_thenUseCaseIsCalledWithCurrentFieldValues() = runTest {
        // Given
        val email = "user@example.com"
        val password = "securePass123"
        val userId = "uid_003"

        emailLoginViewModel.onEmailChange(email)
        emailLoginViewModel.onPasswordChange(password)

        every { signInWithEmailUseCase(email, password) } returns
                flowOf(AuthResult.Success(userId))
        coEvery { startSyncIfNeededUseCase() } just runs

        // When
        emailLoginViewModel.onLoginClick()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { signInWithEmailUseCase(email, password) }
    }

    // ========================================
    // Validation Failure Tests
    // ========================================

    @Test
    fun givenBlankEmail_whenOnLoginClick_thenSetsEmailErrorAndNoPasswordError() = runTest {
        // Given
        every { signInWithEmailUseCase(any(), any()) } returns
                flowOf(AuthResult.ValidationFailure(EmailSignInError.BLANK_EMAIL))

        // When
        emailLoginViewModel.uiState.test {
            awaitItem() // Initial state

            emailLoginViewModel.onLoginClick()
            advanceUntilIdle()

            awaitItem() // isLoading = true

            // Then
            val validationState = awaitItem()
            assertFalse(validationState.isLoading)
            assertEquals(R.string.enter_your_email, validationState.emailError)
            assertNull(validationState.passwordError)
            assertNull(validationState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenInvalidEmailFormat_whenOnLoginClick_thenSetsEmailErrorAndNoPasswordError() = runTest {
        // Given
        every { signInWithEmailUseCase(any(), any()) } returns
                flowOf(AuthResult.ValidationFailure(EmailSignInError.INVALID_EMAIL_FORMAT))

        // When
        emailLoginViewModel.uiState.test {
            awaitItem() // Initial state

            emailLoginViewModel.onLoginClick()
            advanceUntilIdle()

            awaitItem() // isLoading = true

            // Then
            val validationState = awaitItem()
            assertFalse(validationState.isLoading)
            assertEquals(R.string.invalid_email, validationState.emailError)
            assertNull(validationState.passwordError)
            assertNull(validationState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenBlankPassword_whenOnLoginClick_thenSetsPasswordErrorAndNoEmailError() = runTest {
        // Given
        every { signInWithEmailUseCase(any(), any()) } returns
                flowOf(AuthResult.ValidationFailure(EmailSignInError.BLANK_PASSWORD))

        // When
        emailLoginViewModel.uiState.test {
            awaitItem() // Initial state

            emailLoginViewModel.onLoginClick()
            advanceUntilIdle()

            awaitItem() // isLoading = true

            // Then
            val validationState = awaitItem()
            assertFalse(validationState.isLoading)
            assertEquals(R.string.enter_your_password, validationState.passwordError)
            assertNull(validationState.emailError)
            assertNull(validationState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenValidationFailure_whenOnLoginClick_thenSynchronizationIsNeverStarted() = runTest {
        // Given
        every { signInWithEmailUseCase(any(), any()) } returns
                flowOf(AuthResult.ValidationFailure(EmailSignInError.BLANK_EMAIL))

        // When
        emailLoginViewModel.onLoginClick()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { startSyncIfNeededUseCase() }
    }

    // ========================================
    // Auth Failure Tests
    // ========================================

    @Test
    fun givenInvalidCredentials_whenOnLoginClick_thenSetsErrorAndStopsLoading() = runTest {
        // Given
        every { signInWithEmailUseCase(any(), any()) } returns
                flowOf(AuthResult.Failure(AuthError.INVALID_CREDENTIALS))

        // When
        emailLoginViewModel.uiState.test {
            awaitItem() // Initial state

            emailLoginViewModel.onLoginClick()
            advanceUntilIdle()

            awaitItem() // isLoading = true

            // Then
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertNotNull(errorState.error)
            assertFalse(errorState.shouldNavigateToSchedule)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenUserNotFound_whenOnLoginClick_thenSetsErrorAndStopsLoading() = runTest {
        // Given
        every { signInWithEmailUseCase(any(), any()) } returns
                flowOf(AuthResult.Failure(AuthError.USER_NOT_FOUND))

        // When
        emailLoginViewModel.uiState.test {
            awaitItem() // Initial state

            emailLoginViewModel.onLoginClick()
            advanceUntilIdle()

            awaitItem() // isLoading = true

            // Then
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertNotNull(errorState.error)
            assertFalse(errorState.shouldNavigateToSchedule)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenNetworkError_whenOnLoginClick_thenSetsErrorAndStopsLoading() = runTest {
        // Given
        every { signInWithEmailUseCase(any(), any()) } returns
                flowOf(AuthResult.Failure(AuthError.NETWORK_ERROR))

        // When
        emailLoginViewModel.uiState.test {
            awaitItem() // Initial state

            emailLoginViewModel.onLoginClick()
            advanceUntilIdle()

            awaitItem() // isLoading = true

            // Then
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertNotNull(errorState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenUnknownError_whenOnLoginClick_thenSetsErrorAndStopsLoading() = runTest {
        // Given
        every { signInWithEmailUseCase(any(), any()) } returns
                flowOf(AuthResult.Failure(AuthError.UNKNOWN_ERROR))

        // When
        emailLoginViewModel.uiState.test {
            awaitItem() // Initial state

            emailLoginViewModel.onLoginClick()
            advanceUntilIdle()

            awaitItem() // isLoading = true

            // Then
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertNotNull(errorState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenAuthFailure_whenOnLoginClick_thenSynchronizationIsNeverStarted() = runTest {
        // Given
        every { signInWithEmailUseCase(any(), any()) } returns
                flowOf(AuthResult.Failure(AuthError.INVALID_CREDENTIALS))

        // When
        emailLoginViewModel.onLoginClick()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { startSyncIfNeededUseCase() }
    }

    // ========================================
    // State Management Tests
    // ========================================

    @Test
    fun givenSuccessfulSignIn_whenOnNavigationComplete_thenResetsShouldNavigateFlag() = runTest {
        // Given: Sign-in completes successfully
        every { signInWithEmailUseCase(any(), any()) } returns
                flowOf(AuthResult.Success("uid_nav"))
        coEvery { startSyncIfNeededUseCase() } just runs

        emailLoginViewModel.onLoginClick()
        advanceUntilIdle()

        // When
        emailLoginViewModel.uiState.test {
            val stateBeforeNavigation = awaitItem()
            assertTrue(stateBeforeNavigation.shouldNavigateToSchedule)

            emailLoginViewModel.onNavigationComplete()

            // Then
            val stateAfterNavigation = awaitItem()
            assertFalse(stateAfterNavigation.shouldNavigateToSchedule)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenErrorState_whenDismissError_thenErrorIsCleared() = runTest {
        // Given: An auth failure produces an error
        every { signInWithEmailUseCase(any(), any()) } returns
                flowOf(AuthResult.Failure(AuthError.INVALID_CREDENTIALS))

        emailLoginViewModel.onLoginClick()
        advanceUntilIdle()

        // When
        emailLoginViewModel.uiState.test {
            val errorState = awaitItem()
            assertNotNull(errorState.error)

            emailLoginViewModel.dismissError()

            // Then
            val clearedState = awaitItem()
            assertNull(clearedState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenLoadingState_whenOnLoginClick_thenIsLoadingIsTrueBeforeResultArrives() = runTest {
        // Given: Use case will emit a result
        every { signInWithEmailUseCase(any(), any()) } returns
                flowOf(AuthResult.Failure(AuthError.NETWORK_ERROR))

        // When
        emailLoginViewModel.uiState.test {
            awaitItem() // Initial state

            emailLoginViewModel.onLoginClick()
            advanceUntilIdle()

            // Then: Loading is set to true before result arrives
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertNull(loadingState.error)

            awaitItem() // Final state
            cancelAndIgnoreRemainingEvents()
        }
    }
}