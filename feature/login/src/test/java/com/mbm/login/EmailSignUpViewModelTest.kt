package com.mbm.login

import app.cash.turbine.test
import com.mbm.login.email_signup.EmailSignUpViewModel
import com.studentsapps.data.repository.fake.FakeSyncRepository
import com.studentsapps.domain.login.model.AuthError
import com.studentsapps.domain.login.model.AuthResult
import com.studentsapps.domain.login.model.EmailSignUpError
import com.studentsapps.domain.login.repository.fake.FakeAuthRepository
import com.studentsapps.domain.login.usecases.RegisterUserIfNeededUseCase
import com.studentsapps.domain.login.usecases.SignUpWithEmailUseCase
import com.studentsapps.domain.sync.usecases.StartSyncIfNeededUseCase
import com.studentsapps.login.R
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
 * Unit tests for [com.mbm.login.email_signup.EmailSignUpViewModel].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EmailSignUpViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    // ── Fakes ─────────────────────────────────────────────────────────────────

    private val fakeAuthRepository = FakeAuthRepository()
    private val fakeSyncRepository = FakeSyncRepository()

    // ── Real use-case instances wired to fakes ────────────────────────────────

    private lateinit var signUpWithEmailUseCase: SignUpWithEmailUseCase
    private lateinit var registerUserIfNeededUseCase: RegisterUserIfNeededUseCase
    private lateinit var startSyncIfNeededUseCase: StartSyncIfNeededUseCase

    // ── System under test ─────────────────────────────────────────────────────

    private lateinit var viewModel: EmailSignUpViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeAuthRepository.reset()
        fakeSyncRepository.reset()

        signUpWithEmailUseCase = SignUpWithEmailUseCase(fakeAuthRepository)
        registerUserIfNeededUseCase = RegisterUserIfNeededUseCase(fakeAuthRepository)
        startSyncIfNeededUseCase = StartSyncIfNeededUseCase(fakeSyncRepository)

        viewModel = EmailSignUpViewModel(
            signUpWithEmailUseCase = signUpWithEmailUseCase,
            registerUserIfNeededUseCase = registerUserIfNeededUseCase,
            startSyncIfNeededUseCase = startSyncIfNeededUseCase,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ========== Initial State Tests ==========

    @Test
    fun givenViewModelInitialized_whenNoActionTaken_thenHasCorrectDefaultValues() = runTest {
        // Given: ViewModel is initialized
        // When: No action is taken
        viewModel.uiState.test {
            val initialState = awaitItem()

            // Then: All fields reflect the default EmailSignUpUiState
            assertEquals("", initialState.email)
            assertEquals("", initialState.password)
            assertEquals("", initialState.confirmPassword)
            assertFalse(initialState.isPasswordVisible)
            assertFalse(initialState.isConfirmPasswordVisible)
            assertNull(initialState.emailError)
            assertNull(initialState.passwordError)
            assertNull(initialState.confirmPasswordError)
            assertFalse(initialState.isLoading)
            assertNull(initialState.error)
            assertFalse(initialState.shouldNavigateToSchedule)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========== Form Field Event Tests ==========

    @Test
    fun givenInitialState_whenOnEmailChange_thenEmailIsUpdated() = runTest {
        // Given: ViewModel in initial state
        val newEmail = "user@example.com"

        viewModel.uiState.test {
            awaitItem() // Initial state

            // When
            viewModel.onEmailChange(newEmail)

            // Then
            val updatedState = awaitItem()
            assertEquals(newEmail, updatedState.email)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenEmailErrorPresent_whenOnEmailChange_thenEmailErrorIsCleared() = runTest {
        viewModel.uiState.test {
            awaitItem() // Initial state

            viewModel.onRegisterClick()
            advanceUntilIdle()

            awaitItem() // isLoading = true
            val errorState = awaitItem()
            assertNotNull(errorState.emailError)

            // When: User starts correcting the email field
            viewModel.onEmailChange("u")

            // Then: Error is cleared immediately
            val clearedState = awaitItem()
            assertNull(clearedState.emailError)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenInitialState_whenOnPasswordChange_thenPasswordIsUpdated() = runTest {
        // Given: ViewModel in initial state
        val newPassword = "Secure@123"

        viewModel.uiState.test {
            awaitItem() // Initial state

            // When
            viewModel.onPasswordChange(newPassword)

            // Then
            val updatedState = awaitItem()
            assertEquals(newPassword, updatedState.password)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenPasswordErrorPresent_whenOnPasswordChange_thenPasswordErrorIsCleared() = runTest {
        // Given: Trigger a BLANK_PASSWORD validation error first
        viewModel.apply {
            onEmailChange("user@example.com")
        }

        viewModel.uiState.test {
            awaitItem() // Initial state

            viewModel.onRegisterClick()
            advanceUntilIdle()

            awaitItem() // isLoading = true
            val errorState = awaitItem()
            assertNotNull(errorState.passwordError)

            // When: User starts correcting the password field
            viewModel.onPasswordChange("P")

            // Then: Error is cleared immediately
            val clearedState = awaitItem()
            assertNull(clearedState.passwordError)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenInitialState_whenOnConfirmPasswordChange_thenConfirmPasswordIsUpdated() = runTest {
        // Given: ViewModel in initial state
        val newConfirmPassword = "Secure@123"

        viewModel.uiState.test {
            awaitItem() // Initial state

            // When
            viewModel.onConfirmPasswordChange(newConfirmPassword)

            // Then
            val updatedState = awaitItem()
            assertEquals(newConfirmPassword, updatedState.confirmPassword)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenConfirmPasswordErrorPresent_whenOnConfirmPasswordChange_thenConfirmPasswordErrorIsCleared() =
        runTest {
            // Given: Trigger a PASSWORDS_DO_NOT_MATCH error first
            viewModel.apply {
                onEmailChange("user@example.com")
                onPasswordChange("Secure@123")
                onConfirmPasswordChange("Secure@12")
            }

            viewModel.uiState.test {
                awaitItem() // Initial state

                viewModel.onRegisterClick()
                advanceUntilIdle()

                awaitItem() // isLoading = true
                val errorState = awaitItem()
                assertNotNull(errorState.confirmPasswordError)

                // When: User starts correcting the confirm-password field
                viewModel.onConfirmPasswordChange("S")

                // Then: Error is cleared immediately
                val clearedState = awaitItem()
                assertNull(clearedState.confirmPasswordError)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun givenPasswordNotVisible_whenOnTogglePasswordVisibility_thenPasswordBecomesVisible() =
        runTest {
            // Given: Password is hidden (default)
            viewModel.uiState.test {
                val initialState = awaitItem()
                assertFalse(initialState.isPasswordVisible)

                // When
                viewModel.onTogglePasswordVisibility()

                // Then
                val toggledState = awaitItem()
                assertTrue(toggledState.isPasswordVisible)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun givenPasswordVisible_whenOnTogglePasswordVisibility_thenPasswordBecomesHidden() = runTest {
        // Given: Make password visible first
        viewModel.uiState.test {
            awaitItem() // Initial state

            viewModel.onTogglePasswordVisibility()
            awaitItem() // isPasswordVisible = true

            // When: Toggle again
            viewModel.onTogglePasswordVisibility()

            // Then
            val hiddenState = awaitItem()
            assertFalse(hiddenState.isPasswordVisible)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenConfirmPasswordNotVisible_whenOnToggleConfirmPasswordVisibility_thenConfirmPasswordBecomesVisible() =
        runTest {
            // Given: Confirm-password is hidden (default)
            viewModel.uiState.test {
                val initialState = awaitItem()
                assertFalse(initialState.isConfirmPasswordVisible)

                // When
                viewModel.onToggleConfirmPasswordVisibility()

                // Then
                val toggledState = awaitItem()
                assertTrue(toggledState.isConfirmPasswordVisible)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun givenConfirmPasswordVisible_whenOnToggleConfirmPasswordVisibility_thenConfirmPasswordBecomesHidden() =
        runTest {
            // Given: Make confirm-password visible first
            viewModel.uiState.test {
                awaitItem() // Initial state

                viewModel.onToggleConfirmPasswordVisibility()
                awaitItem() // isConfirmPasswordVisible = true

                // When: Toggle again
                viewModel.onToggleConfirmPasswordVisibility()

                // Then
                val hiddenState = awaitItem()
                assertFalse(hiddenState.isConfirmPasswordVisible)

                cancelAndIgnoreRemainingEvents()
            }
        }

    // ========== Successful Sign-Up Tests ==========

    @Test
    fun givenValidInputs_whenOnRegisterClick_thenShowsLoadingThenNavigates() = runTest {
        // Given
        val userId = "uid_001"
        fakeAuthRepository.signUpWithEmailResult = AuthResult.Success(userId)
        fakeAuthRepository.registerUserResult = true

        viewModel.apply {
            onEmailChange("user@example.com")
            onPasswordChange("Secure@123")
            onConfirmPasswordChange("Secure@123")
        }

        viewModel.uiState.test {
            val initialState = awaitItem()
            assertFalse(initialState.isLoading)
            assertFalse(initialState.shouldNavigateToSchedule)

            // When
            viewModel.onRegisterClick()
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
    fun givenValidInputs_whenOnRegisterClick_thenRegistersUserInFirestore() = runTest {
        // Given
        val userId = "uid_002"
        fakeAuthRepository.signUpWithEmailResult = AuthResult.Success(userId)
        fakeAuthRepository.registerUserResult = true

        viewModel.apply {
            onEmailChange("user@example.com")
            onPasswordChange("Secure@123")
            onConfirmPasswordChange("Secure@123")
        }

        // When
        viewModel.onRegisterClick()
        advanceUntilIdle()

        // Then: registerUserIfNeeded was called after sign-up
        assert(fakeAuthRepository.callLog.contains("registerUserIfNeeded")) {
            "registerUserIfNeeded should be called after successful sign-up"
        }
    }

    @Test
    fun givenValidInputs_whenOnRegisterClick_thenStartsSynchronization() = runTest {
        // Given
        val userId = "uid_003"
        fakeAuthRepository.signUpWithEmailResult = AuthResult.Success(userId)
        fakeAuthRepository.registerUserResult = true

        viewModel.apply {
            onEmailChange("user@example.com")
            onPasswordChange("Secure@123")
            onConfirmPasswordChange("Secure@123")
        }

        // When
        viewModel.onRegisterClick()
        advanceUntilIdle()

        // Then: startSyncIfNeeded was called exactly once after all post-auth work
        assert(fakeSyncRepository.startSyncIfNeededCallCount == 1) {
            "startSyncIfNeeded should be called exactly once after successful sign-up"
        }
    }

    @Test
    fun givenValidInputs_whenOnSignUpClick_thenUseCaseIsCalledWithCurrentFieldValues() = runTest {
        // Given
        val email = "user@example.com"
        val password = "Secure@123"

        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(password)
        viewModel.onConfirmPasswordChange(password)

        fakeAuthRepository.signUpWithEmailResult = AuthResult.Success("uid_004")
        fakeAuthRepository.registerUserResult = true

        // When
        viewModel.onRegisterClick()
        advanceUntilIdle()

        // Then: Repository received the trimmed email and the exact password
        val signUpCall = fakeAuthRepository.callLog.firstOrNull { it.startsWith("signUpWithEmail:") }
        assertNotNull(signUpCall)
        assertEquals(
            "signUpWithEmail:$email",
            signUpCall,
            "The repository must be called with the trimmed email"
        )
    }

    @Test
    fun givenValidInputs_whenSignUpSucceeds_thenStepsExecuteInCorrectOrder() = runTest {
        // Given
        val userId = "uid_order"
        fakeAuthRepository.signUpWithEmailResult = AuthResult.Success(userId)
        fakeAuthRepository.registerUserResult = true

        viewModel.apply {
            onEmailChange("user@example.com")
            onPasswordChange("Secure@123")
            onConfirmPasswordChange("Secure@123")
        }

        // When
        viewModel.onRegisterClick()
        advanceUntilIdle()

        // Then: signUpWithEmail must precede registerUserIfNeeded in the call log
        val signUpIndex = fakeAuthRepository.callLog.indexOfFirst {
            it.startsWith("signUpWithEmail:")
        }
        val registerIndex = fakeAuthRepository.callLog.indexOf("registerUserIfNeeded")

        assert(signUpIndex != -1) { "signUpWithEmail must appear in the call log" }
        assert(registerIndex != -1) { "registerUserIfNeeded must appear in the call log" }
        assert(signUpIndex < registerIndex) {
            "signUpWithEmail (position $signUpIndex) must precede " +
                    "registerUserIfNeeded (position $registerIndex)"
        }
    }

    @Test
    fun givenRegistrationFails_whenSignUpSucceeds_thenSyncIsNeverStarted() = runTest {
        // Given: Firebase account created but Firestore registration fails
        fakeAuthRepository.signUpWithEmailResult = AuthResult.Success("uid_reg_fail")
        fakeAuthRepository.registerUserResult = false

        // When
        viewModel.onRegisterClick()
        advanceUntilIdle()

        // Then: Sync must not start when user registration in Firestore failed
        assert(fakeSyncRepository.startSyncIfNeededCallCount == 0) {
            "startSyncIfNeeded must not be called when user registration fails"
        }
    }

    // ========== Validation Failure Tests ==========

    @Test
    fun givenBlankEmail_whenOnSignUpClick_thenSetsEmailErrorAndNoOtherErrors() = runTest {
        // Given
        fakeAuthRepository.signUpWithEmailResult =
            AuthResult.ValidationFailure(EmailSignUpError.BLANK_EMAIL)

        viewModel.uiState.test {
            awaitItem() // Initial state
            viewModel.onRegisterClick()
            advanceUntilIdle()
            awaitItem() // isLoading = true

            // Then
            val validationState = awaitItem()
            assertFalse(validationState.isLoading)
            assertEquals(R.string.enter_your_email, validationState.emailError)
            assertNull(validationState.passwordError)
            assertNull(validationState.confirmPasswordError)
            assertNull(validationState.error)
            assertFalse(validationState.shouldNavigateToSchedule)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenInvalidEmailFormat_whenOnRegisterClick_thenSetsEmailFormatError() = runTest {
        // Given
        viewModel.apply {
            onEmailChange("user@example")
        }

        viewModel.uiState.test {
            awaitItem() // Initial state
            viewModel.onRegisterClick()
            advanceUntilIdle()
            awaitItem() // isLoading = true

            // Then
            val validationState = awaitItem()
            assertFalse(validationState.isLoading)
            assertEquals(R.string.invalid_email, validationState.emailError)
            assertNull(validationState.passwordError)
            assertNull(validationState.confirmPasswordError)
            assertNull(validationState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenBlankPassword_whenOnRegisterClick_thenSetsPasswordErrorAndNoEmailError() = runTest {
        viewModel.apply {
            onEmailChange("user@example.com")
        }

        viewModel.uiState.test {
            awaitItem() // Initial state
            viewModel.onRegisterClick()
            advanceUntilIdle()
            awaitItem() // isLoading = true

            // Then
            val validationState = awaitItem()
            assertFalse(validationState.isLoading)
            assertEquals(R.string.enter_your_password, validationState.passwordError)
            assertNull(validationState.emailError)
            assertNull(validationState.confirmPasswordError)
            assertNull(validationState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenWeakPassword_whenOnSignUpClick_thenSetsWeakPasswordError() = runTest {
        // Given
        viewModel.apply {
            onEmailChange("user@example.com")
            onPasswordChange("secure")
            onConfirmPasswordChange("secure")
        }

        viewModel.uiState.test {
            awaitItem() // Initial state
            viewModel.onRegisterClick()
            advanceUntilIdle()
            awaitItem() // isLoading = true

            // Then
            val validationState = awaitItem()
            assertFalse(validationState.isLoading)
            assertEquals(R.string.low_security_password, validationState.passwordError)
            assertNull(validationState.emailError)
            assertNull(validationState.confirmPasswordError)
            assertNull(validationState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenPasswordsDoNotMatch_whenOnRegisterClick_thenSetsConfirmPasswordError() = runTest {
        // Given
        viewModel.apply {
            onEmailChange("user@example.com")
            onPasswordChange("Secure@123")
            onConfirmPasswordChange("Secure@12")
        }

        viewModel.uiState.test {
            awaitItem() // Initial state
            viewModel.onRegisterClick()
            advanceUntilIdle()
            awaitItem() // isLoading = true

            // Then
            val validationState = awaitItem()
            assertFalse(validationState.isLoading)
            assertEquals(R.string.passwords_do_not_match, validationState.confirmPasswordError)
            assertNull(validationState.emailError)
            assertNull(validationState.passwordError)
            assertNull(validationState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenValidationFailure_whenOnSignUpClick_thenSynchronizationIsNeverStarted() = runTest {
        // When
        viewModel.onRegisterClick()
        advanceUntilIdle()

        // Then
        assert(fakeSyncRepository.startSyncIfNeededCallCount == 0) {
            "startSyncIfNeeded must not be called when validation fails"
        }
    }

    @Test
    fun givenValidationFailure_whenOnRegisterClick_thenRepositorySignUpIsNeverCalled() = runTest {
        // When
        viewModel.onRegisterClick()
        advanceUntilIdle()

        // Then
        assert(fakeAuthRepository.callLog.none { it.startsWith("signUpWithEmail") }) {
            "Repository.signUpWithEmail must not be called when validation fails"
        }
    }

    // ========== Auth Failure Tests ==========

    @Test
    fun givenEmailAlreadyInUse_whenOnRegisterClick_thenSetsErrorAndStopsLoading() = runTest {
        // Given
        fakeAuthRepository.signUpWithEmailResult =
            AuthResult.Failure(AuthError.EMAIL_ALREADY_IN_USE)

        viewModel.apply {
            onEmailChange("user@example.com")
            onPasswordChange("Secure@123")
            onConfirmPasswordChange("Secure@123")
        }

        viewModel.uiState.test {
            awaitItem() // Initial state
            viewModel.onRegisterClick()
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
    fun givenNetworkError_whenOnSignUpClick_thenSetsErrorAndStopsLoading() = runTest {
        // Given
        fakeAuthRepository.signUpWithEmailResult =
            AuthResult.Failure(AuthError.NETWORK_ERROR)

        viewModel.apply {
            onEmailChange("user@example.com")
            onPasswordChange("Secure@123")
            onConfirmPasswordChange("Secure@123")
        }

        viewModel.uiState.test {
            awaitItem() // Initial state
            viewModel.onRegisterClick()
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
    fun givenUnknownError_whenOnSignUpClick_thenSetsErrorAndStopsLoading() = runTest {
        // Given
        fakeAuthRepository.signUpWithEmailResult =
            AuthResult.Failure(AuthError.UNKNOWN_ERROR)

        viewModel.apply {
            onEmailChange("user@example.com")
            onPasswordChange("Secure@123")
            onConfirmPasswordChange("Secure@123")
        }

        viewModel.uiState.test {
            awaitItem() // Initial state
            viewModel.onRegisterClick()
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
    fun givenAuthFailure_whenOnSignUpClick_thenSynchronizationIsNeverStarted() = runTest {
        // Given: Auth fails — sync must never start unless the full flow succeeds
        fakeAuthRepository.signUpWithEmailResult =
            AuthResult.Failure(AuthError.EMAIL_ALREADY_IN_USE)

        // When
        viewModel.onRegisterClick()
        advanceUntilIdle()

        // Then
        assert(fakeSyncRepository.startSyncIfNeededCallCount == 0) {
            "startSyncIfNeeded must not be called when sign-up auth fails"
        }
    }

    // ========== State Management Tests ==========

    @Test
    fun givenSuccessfulSignUp_whenOnNavigationComplete_thenResetsShouldNavigateFlag() = runTest {
        // Given: Sign-up completes successfully
        fakeAuthRepository.signUpWithEmailResult = AuthResult.Success("uid_nav")
        fakeAuthRepository.registerUserResult = true

        viewModel.apply {
            onEmailChange("user@example.com")
            onPasswordChange("Secure@123")
            onConfirmPasswordChange("Secure@123")
            onRegisterClick()
        }
        advanceUntilIdle()

        // When
        viewModel.uiState.test {
            val stateBeforeNavigation = awaitItem()
            assertTrue(stateBeforeNavigation.shouldNavigateToSchedule)

            viewModel.onNavigationComplete()

            // Then
            val stateAfterNavigation = awaitItem()
            assertFalse(stateAfterNavigation.shouldNavigateToSchedule)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenErrorState_whenDismissError_thenErrorIsCleared() = runTest {
        // Given: An auth failure produces an error
        fakeAuthRepository.signUpWithEmailResult =
            AuthResult.Failure(AuthError.EMAIL_ALREADY_IN_USE)

        viewModel.apply {
            onEmailChange("user@example.com")
            onPasswordChange("Secure@123")
            onConfirmPasswordChange("Secure@123")
            onRegisterClick()
        }
        advanceUntilIdle()

        // When
        viewModel.uiState.test {
            val errorState = awaitItem()
            assertNotNull(errorState.error)

            viewModel.dismissError()

            // Then
            val clearedState = awaitItem()
            assertNull(clearedState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenLoadingState_whenOnSignUpClick_thenIsLoadingIsTrueBeforeResultArrives() = runTest {
        // Given: Use case will emit a result (but loading is observed before that)
        fakeAuthRepository.signUpWithEmailResult =
            AuthResult.Failure(AuthError.NETWORK_ERROR)

        viewModel.uiState.test {
            awaitItem() // Initial state

            viewModel.onRegisterClick()
            advanceUntilIdle()

            // Then: isLoading = true is emitted before the final error state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertNull(loadingState.error)

            awaitItem() // Final error state
            cancelAndIgnoreRemainingEvents()
        }
    }
}