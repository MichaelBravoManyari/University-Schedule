package com.mbm.login

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mbm.login.email_signup.EmailSignUpScreen
import com.mbm.login.email_signup.EmailSignUpViewModel
import com.studentsapps.domain.login.model.AuthError
import com.studentsapps.domain.login.model.AuthResult
import com.studentsapps.domain.login.repository.fake.FakeAuthRepository
import com.studentsapps.domain.login.usecases.RegisterUserIfNeededUseCase
import com.studentsapps.domain.login.usecases.SignUpWithEmailUseCase
import com.studentsapps.data.repository.fake.FakeSyncRepository
import com.studentsapps.domain.sync.usecases.StartSyncIfNeededUseCase
import com.studentsapps.login.R
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import theme.UniversityScheduleTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Integration tests for [com.mbm.login.email_signup.EmailSignUpScreen].
 */
@RunWith(AndroidJUnit4::class)
class EmailSignUpScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // ── Fakes ─────────────────────────────────────────────────────────────────
    private val fakeAuthRepository = FakeAuthRepository()
    private val fakeSyncRepository = FakeSyncRepository()

    // ── Real use-case instances wired to fakes ────────────────────────────────

    private lateinit var signUpWithEmailUseCase: SignUpWithEmailUseCase
    private lateinit var registerUserIfNeededUseCase: RegisterUserIfNeededUseCase
    private lateinit var startSyncIfNeededUseCase: StartSyncIfNeededUseCase

    // ── System under test ─────────────────────────────────────────────────────

    private lateinit var viewModel: EmailSignUpViewModel

    // ── Callback trackers ─────────────────────────────────────────────────────

    private var navigateToScheduleCalled = false
    private var loginClickCalled = false
    private var backClickCalled = false

    // ── String resources resueltos desde el contexto del Activity ────────—─—─—

    private lateinit var emailLabel: String
    private lateinit var passwordLabel: String
    private lateinit var confirmPasswordLabel: String
    private lateinit var registerButton: String
    private lateinit var backButton: String
    private lateinit var showPassword: String
    private lateinit var hidePassword: String
    private lateinit var showConfirmPassword: String
    private lateinit var hideConfirmPassword: String
    private lateinit var enterYourEmail: String
    private lateinit var invalidEmail: String
    private lateinit var enterYourPassword: String
    private lateinit var lowSecurityPassword: String
    private lateinit var passwordsDoNotMatch: String
    private lateinit var signInAction: String

    @Before
    fun setup() {
        // Resetear fakes antes de cada test para garantizar aislamiento
        fakeAuthRepository.reset()
        fakeSyncRepository.reset()

        // Resetear trackers de callbacks
        navigateToScheduleCalled = false
        loginClickCalled = false
        backClickCalled = false

        // Construir, use cases reales sobre los fakes
        signUpWithEmailUseCase = SignUpWithEmailUseCase(fakeAuthRepository)
        registerUserIfNeededUseCase = RegisterUserIfNeededUseCase(fakeAuthRepository)
        startSyncIfNeededUseCase = StartSyncIfNeededUseCase(fakeSyncRepository)

        // ViewModel con dependencias reales/fake — mismo wiring que en el Fragment
        viewModel = EmailSignUpViewModel(
            signUpWithEmailUseCase = signUpWithEmailUseCase,
            registerUserIfNeededUseCase = registerUserIfNeededUseCase,
            startSyncIfNeededUseCase = startSyncIfNeededUseCase,
        )

        // Resolver strings desde el contexto del Activity
        with(composeTestRule.activity) {
            emailLabel = getString(R.string.email)
            passwordLabel = getString(R.string.password)
            confirmPasswordLabel = getString(R.string.confirm_password)
            registerButton = getString(R.string.register)
            backButton = getString(R.string.back)
            showPassword = getString(R.string.show_password)
            hidePassword = getString(R.string.hide_password)
            // Los content descriptions del confirm-password toggle pueden ser los mismos
            // strings o tener sus propios recursos; ajustar si el componente los diferencia.
            showConfirmPassword = getString(R.string.show_password)
            hideConfirmPassword = getString(R.string.hide_password)
            enterYourEmail = getString(R.string.enter_your_email)
            invalidEmail = getString(R.string.invalid_email)
            enterYourPassword = getString(R.string.enter_your_password)
            lowSecurityPassword = getString(R.string.low_security_password)
            passwordsDoNotMatch = getString(R.string.passwords_do_not_match)
            signInAction = getString(R.string.sign_in_action)
        }
    }

    // —— Helper: renderiza la pantalla stateful con los callbacks configurados —─

    private fun renderScreen(
        onNavigateToSchedule: () -> Unit = { navigateToScheduleCalled = true },
        onLoginClick: () -> Unit = { loginClickCalled = true },
        onBackClick: () -> Unit = { backClickCalled = true },
    ) {
        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailSignUpScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = onNavigateToSchedule,
                    onLoginClick = onLoginClick,
                    onBackClick = onBackClick,
                )
            }
        }
    }

    // ========== Initial State Tests ==========

    @Test
    fun givenEmailSignUpScreenDisplayed_whenNoInteraction_thenShowsAllRequiredElements() {
        // Given
        renderScreen()

        // Then
        composeTestRule.onNodeWithText(emailLabel).assertIsDisplayed()
        composeTestRule.onNodeWithText(passwordLabel).assertIsDisplayed()
        composeTestRule.onNodeWithText(confirmPasswordLabel).assertIsDisplayed()
        composeTestRule.onNodeWithText(registerButton).assertIsDisplayed()
        composeTestRule.onNodeWithText(backButton).assertIsDisplayed()
    }

    @Test
    fun givenEmailSignUpScreenDisplayed_whenNoInteraction_thenLoadingOverlayIsNotVisible() {
        // Given
        renderScreen()

        // Then
        composeTestRule.onNodeWithTag("loading_overlay").assertDoesNotExist()
    }

    @Test
    fun givenEmailSignUpScreenDisplayed_whenNoInteraction_thenNoFieldErrorsAreShown() {
        // Given
        renderScreen()

        // Then
        composeTestRule.onNodeWithText(enterYourEmail).assertDoesNotExist()
        composeTestRule.onNodeWithText(invalidEmail).assertDoesNotExist()
        composeTestRule.onNodeWithText(enterYourPassword).assertDoesNotExist()
        composeTestRule.onNodeWithText(lowSecurityPassword).assertDoesNotExist()
        composeTestRule.onNodeWithText(passwordsDoNotMatch).assertDoesNotExist()
    }

    // ========== User Input Tests ==========

    @Test
    fun givenEmailSignUpScreen_whenUserTypesInEmailField_thenEmailValueIsUpdated() {
        // Given
        renderScreen()

        // When
        composeTestRule.onNodeWithText(emailLabel).performTextInput("user@example.com")
        composeTestRule.waitForIdle()

        // Then
        assertEquals(
            viewModel.uiState.value.email,
            "user@example.com",
            "Email should be updated in ViewModel state after user types"
        )
    }

    @Test
    fun givenEmailSignUpScreen_whenUserTypesInPasswordField_thenPasswordValueIsUpdated() {
        // Given
        renderScreen()

        // When
        composeTestRule.onNodeWithText(passwordLabel).performTextInput("Secure@123")
        composeTestRule.waitForIdle()

        // Then
        assertEquals(
            viewModel.uiState.value.password,
            "Secure@123",
            "Password should be updated in ViewModel state after user types"
        )
    }

    @Test
    fun givenEmailSignUpScreen_whenUserTypesInConfirmPasswordField_thenConfirmPasswordValueIsUpdated() {
        // Given
        renderScreen()

        // When
        composeTestRule.onNodeWithText(confirmPasswordLabel).performTextInput("Secure@123")
        composeTestRule.waitForIdle()

        // Then
        assertEquals(
            viewModel.uiState.value.confirmPassword,
            "Secure@123",
            "Confirm password should be updated in ViewModel state after user types"
        )
    }

    // ========== Password Visibility Toggle Tests ==========

    @Test
    fun givenPasswordHidden_whenPasswordToggleClicked_thenIconChangesToHidePassword() {
        // Given
        renderScreen()

        // When
        // useUnmergedTree = true necesario cuando hay múltiples nodos con el mismo CD
        composeTestRule.onNodeWithTag("toggle_password_visibility", useUnmergedTree = true)
            .performClick()
        composeTestRule.waitForIdle()

        // Then
        assertTrue(
            viewModel.uiState.value.isPasswordVisible,
            "isPasswordVisible should be true after toggling"
        )
    }

    @Test
    fun givenPasswordVisible_whenPasswordToggleClickedAgain_thenPasswordBecomesHidden() {
        // Given
        renderScreen()
        composeTestRule.onNodeWithTag("toggle_password_visibility", useUnmergedTree = true)
            .performClick()
        composeTestRule.waitForIdle()

        // When
        composeTestRule.onNodeWithTag("toggle_password_visibility", useUnmergedTree = true)
            .performClick()
        composeTestRule.waitForIdle()

        // Then
        assertFalse(
            viewModel.uiState.value.isPasswordVisible,
            "isPasswordVisible should be false after toggling back"
        )
    }

    @Test
    fun givenConfirmPasswordHidden_whenConfirmPasswordToggleClicked_thenConfirmPasswordBecomesVisible() {
        // Given
        renderScreen()

        // When
        composeTestRule.onNodeWithTag("toggle_confirm_password_visibility", useUnmergedTree = true)
            .performClick()
        composeTestRule.waitForIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.isConfirmPasswordVisible,
            "One of the visibility toggles should be true after clicking"
        )
    }

    // ========== Successful Sign-Up Tests ==========

    @Test
    fun givenValidInputs_whenRegisterButtonClicked_thenNavigatesToSchedule() {
        // Given
        fakeAuthRepository.signUpWithEmailResult = AuthResult.Success("uid_001")
        fakeAuthRepository.registerUserResult = true

        renderScreen()

        // When
        composeTestRule.onNodeWithText(emailLabel).performTextInput("user@example.com")
        composeTestRule.onNodeWithText(passwordLabel).performTextInput("Secure@123")
        composeTestRule.onNodeWithText(confirmPasswordLabel).performTextInput("Secure@123")
        composeTestRule.onNodeWithText(registerButton).performClick()
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.waitUntil(timeoutMillis = 5000) { navigateToScheduleCalled }
        assertTrue(navigateToScheduleCalled, "Should navigate to schedule after successful sign-up")
        assertFalse(loginClickCalled, "Login callback should not be called")
        assertFalse(backClickCalled, "Back callback should not be called")
    }

    @Test
    fun givenValidInputs_whenRegistrationSucceeds_thenStartsSynchronization() {
        // Given
        fakeAuthRepository.signUpWithEmailResult = AuthResult.Success("uid_002")
        fakeAuthRepository.registerUserResult = true

        renderScreen()

        // When
        composeTestRule.onNodeWithText(emailLabel).performTextInput("user@example.com")
        composeTestRule.onNodeWithText(passwordLabel).performTextInput("Secure@123")
        composeTestRule.onNodeWithText(confirmPasswordLabel).performTextInput("Secure@123")
        composeTestRule.onNodeWithText(registerButton).performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) { navigateToScheduleCalled }

        // Then
        assertEquals(
            fakeSyncRepository.startSyncIfNeededCallCount,
            1,
            "startSyncIfNeeded should be called exactly once after successful sign-up"
        )
    }

    @Test
    fun givenRegistrationInProgress_whenRegisterButtonClicked_thenLoadingOverlayIsVisible() {
        // Given
        fakeAuthRepository.signUpWithEmailResult = AuthResult.Success("uid_003")
        fakeAuthRepository.signUpWithEmailDelay = 2_000L

        renderScreen()

        // When
        composeTestRule.onNodeWithText(emailLabel).performTextInput("user@example.com")
        composeTestRule.onNodeWithText(passwordLabel).performTextInput("Secure@123")
        composeTestRule.onNodeWithText(confirmPasswordLabel).performTextInput("Secure@123")
        composeTestRule.onNodeWithText(registerButton).performClick()
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithTag("loading_overlay").assertIsDisplayed()
    }

    @Test
    fun givenSuccessfulSignUp_whenNavigationCompletes_thenShouldNavigateFlagIsReset() {
        // Given
        fakeAuthRepository.signUpWithEmailResult = AuthResult.Success("uid_004")
        fakeAuthRepository.registerUserResult = true

        renderScreen(
            onNavigateToSchedule = {
                navigateToScheduleCalled = true
                // Simula que el Fragment reconoce la navegación
                viewModel.onNavigationComplete()
            })

        // When:
        composeTestRule.onNodeWithText(emailLabel).performTextInput("user@example.com")
        composeTestRule.onNodeWithText(passwordLabel).performTextInput("Secure@123")
        composeTestRule.onNodeWithText(confirmPasswordLabel).performTextInput("Secure@123")
        composeTestRule.onNodeWithText(registerButton).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(timeoutMillis = 5000) { navigateToScheduleCalled }

        // Then
        assertFalse(
            viewModel.uiState.value.shouldNavigateToSchedule,
            "shouldNavigateToSchedule must be reset after navigation to avoid double-triggering"
        )
    }

    // ========== Validation Failure Tests ==========

    @Test
    fun givenBlankEmail_whenRegisterButtonClicked_thenEmailErrorIsDisplayed() {
        // Given
        renderScreen()

        // When
        composeTestRule.onNodeWithText(registerButton).performClick()
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithText(enterYourEmail).assertIsDisplayed()
        composeTestRule.onNodeWithText(enterYourPassword).assertDoesNotExist()
        composeTestRule.onNodeWithText(passwordsDoNotMatch).assertDoesNotExist()
        assertFalse(navigateToScheduleCalled, "Should not navigate when email is blank")
    }

    @Test
    fun givenInvalidEmailFormat_whenRegisterButtonClicked_thenEmailFormatErrorIsDisplayed() {
        // Given
        renderScreen()

        // When
        composeTestRule.onNodeWithText(emailLabel).performTextInput("user@example")
        composeTestRule.onNodeWithText(registerButton).performClick()
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithText(invalidEmail).assertIsDisplayed()
        composeTestRule.onNodeWithText(enterYourPassword).assertDoesNotExist()
        assertFalse(navigateToScheduleCalled, "Should not navigate with invalid email format")
    }

    @Test
    fun givenBlankPassword_whenRegisterButtonClicked_thenPasswordErrorIsDisplayed() {
        // Given
        renderScreen()

        // When
        composeTestRule.onNodeWithText(emailLabel).performTextInput("user@example.com")
        composeTestRule.onNodeWithText(registerButton).performClick()
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithText(enterYourPassword).assertIsDisplayed()
        composeTestRule.onNodeWithText(enterYourEmail).assertDoesNotExist()
        composeTestRule.onNodeWithText(passwordsDoNotMatch).assertDoesNotExist()
        assertFalse(navigateToScheduleCalled, "Should not navigate when password is blank")
    }

    @Test
    fun givenWeakPassword_whenRegisterButtonClicked_thenWeakPasswordErrorIsDisplayed() {
        // Given
        renderScreen()

        // When
        composeTestRule.onNodeWithText(emailLabel).performTextInput("user@example.com")
        composeTestRule.onNodeWithText(passwordLabel).performTextInput("secure123")
        composeTestRule.onNodeWithText(registerButton).performClick()
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithText(lowSecurityPassword).assertIsDisplayed()
        composeTestRule.onNodeWithText(enterYourEmail).assertDoesNotExist()
        composeTestRule.onNodeWithText(passwordsDoNotMatch).assertDoesNotExist()
        assertFalse(navigateToScheduleCalled, "Should not navigate with a weak password")
    }

    @Test
    fun givenPasswordsDoNotMatch_whenRegisterButtonClicked_thenConfirmPasswordErrorIsDisplayed() {
        // Given
        renderScreen()

        // When: El usuario toca Registrarse con contraseñas distintas
        composeTestRule.onNodeWithText(emailLabel).performTextInput("user@example.com")
        composeTestRule.onNodeWithText(passwordLabel).performTextInput("Secure@123")
        composeTestRule.onNodeWithText(confirmPasswordLabel).performTextInput("secure@123")
        composeTestRule.onNodeWithText(registerButton).performClick()
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithText(passwordsDoNotMatch).assertIsDisplayed()
        composeTestRule.onNodeWithText(enterYourEmail).assertDoesNotExist()
        composeTestRule.onNodeWithText(enterYourPassword).assertDoesNotExist()
        assertFalse(navigateToScheduleCalled, "Should not navigate when passwords do not match")
    }

    @Test
    fun givenEmailErrorShown_whenUserStartsTypingEmail_thenEmailErrorDisappears() {
        // Given
        renderScreen()

        composeTestRule.onNodeWithText(registerButton).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(enterYourEmail).assertIsDisplayed()

        // When
        composeTestRule.onNodeWithText(emailLabel).performTextInput("u")
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithText(enterYourEmail).assertDoesNotExist()
    }

    @Test
    fun givenPasswordErrorShown_whenUserStartsTypingPassword_thenPasswordErrorDisappears() {
        // Given
        renderScreen()

        composeTestRule.onNodeWithText(emailLabel).performTextInput("user@example.com")
        composeTestRule.onNodeWithText(registerButton).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(enterYourPassword).assertIsDisplayed()

        // When
        composeTestRule.onNodeWithText(passwordLabel).performTextInput("p")
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithText(enterYourPassword).assertDoesNotExist()
    }

    @Test
    fun givenConfirmPasswordErrorShown_whenUserStartsTypingConfirmPassword_thenConfirmPasswordErrorDisappears() {
        // Given
        renderScreen()

        composeTestRule.onNodeWithText(emailLabel).performTextInput("user@example.com")
        composeTestRule.onNodeWithText(passwordLabel).performTextInput("Secure@123")
        composeTestRule.onNodeWithText(confirmPasswordLabel).performTextInput("secure@123")
        composeTestRule.onNodeWithText(registerButton).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(passwordsDoNotMatch).assertIsDisplayed()

        // When
        composeTestRule.onNodeWithText(confirmPasswordLabel).performTextInput("p")
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithText(passwordsDoNotMatch).assertDoesNotExist()
    }

    @Test
    fun givenValidationFailure_whenRegisterButtonClicked_thenLoadingOverlayIsNeverShown() {
        // Given
        renderScreen()

        // When
        composeTestRule.onNodeWithText(registerButton).performClick()
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithTag("loading_overlay").assertDoesNotExist()
    }

    // ========== Auth Failure Tests ==========

    @Test
    fun givenEmailAlreadyInUse_whenRegisterButtonClicked_thenNoNavigationOccurs() {
        // Given
        fakeAuthRepository.signUpWithEmailResult =
            AuthResult.Failure(AuthError.EMAIL_ALREADY_IN_USE)

        renderScreen()

        // When
        composeTestRule.onNodeWithText(emailLabel).performTextInput("user@example.com")
        composeTestRule.onNodeWithText(passwordLabel).performTextInput("Secure@123")
        composeTestRule.onNodeWithText(confirmPasswordLabel).performTextInput("Secure@123")
        composeTestRule.onNodeWithText(registerButton).performClick()
        composeTestRule.waitForIdle()

        // Then
        assertFalse(navigateToScheduleCalled, "Should not navigate when email is already in use")
        assertFalse(loginClickCalled, "Login callback should not be called")
        assertFalse(backClickCalled, "Back callback should not be called")
    }

    @Test
    fun givenNetworkError_whenRegisterButtonClicked_thenNoNavigationOccurs() {
        // Given
        fakeAuthRepository.signUpWithEmailResult = AuthResult.Failure(AuthError.NETWORK_ERROR)

        renderScreen()

        // When
        composeTestRule.onNodeWithText(emailLabel).performTextInput("user@example.com")
        composeTestRule.onNodeWithText(passwordLabel).performTextInput("Secure@123")
        composeTestRule.onNodeWithText(confirmPasswordLabel).performTextInput("Secure@123")
        composeTestRule.onNodeWithText(registerButton).performClick()
        composeTestRule.waitForIdle()

        // Then
        assertFalse(navigateToScheduleCalled, "Should not navigate when network fails")
    }

    @Test
    fun givenAuthFailure_whenRegisterButtonClicked_thenSynchronizationIsNeverStarted() {
        // Given
        fakeAuthRepository.signUpWithEmailResult = AuthResult.Failure(AuthError.UNKNOWN_ERROR)

        renderScreen()

        // When
        composeTestRule.onNodeWithText(emailLabel).performTextInput("user@example.com")
        composeTestRule.onNodeWithText(passwordLabel).performTextInput("Secure@123")
        composeTestRule.onNodeWithText(confirmPasswordLabel).performTextInput("Secure@123")
        composeTestRule.onNodeWithText(registerButton).performClick()
        composeTestRule.waitForIdle()

        // Then
        assertEquals(
            fakeSyncRepository.startSyncIfNeededCallCount,
            0,
            "startSyncIfNeeded must not be called when sign-up auth fails"
        )
    }

    @Test
    fun givenAuthFailure_whenRegisterButtonClicked_thenLoadingOverlayDisappears() {
        // Given
        fakeAuthRepository.signUpWithEmailResult =
            AuthResult.Failure(AuthError.EMAIL_ALREADY_IN_USE)

        renderScreen()

        // When
        composeTestRule.onNodeWithText(emailLabel).performTextInput("user@example.com")
        composeTestRule.onNodeWithText(passwordLabel).performTextInput("Secure@123")
        composeTestRule.onNodeWithText(confirmPasswordLabel).performTextInput("Secure@123")
        composeTestRule.onNodeWithText(registerButton).performClick()
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithTag("loading_overlay").assertDoesNotExist()
    }

    @Test
    fun givenRegistrationFails_whenRegisterSucceeds_thenSynchronizationIsNeverStarted() {
        // Given
        fakeAuthRepository.signUpWithEmailResult = AuthResult.Success("uid_reg_fail")
        fakeAuthRepository.registerUserResult = false

        renderScreen()

        // When
        composeTestRule.onNodeWithText(emailLabel).performTextInput("user@example.com")
        composeTestRule.onNodeWithText(passwordLabel).performTextInput("Secure@123")
        composeTestRule.onNodeWithText(confirmPasswordLabel).performTextInput("Secure@123")
        composeTestRule.onNodeWithText(registerButton).performClick()
        composeTestRule.waitForIdle()

        // Then
        assertFalse(
            navigateToScheduleCalled, "Should not navigate when Firestore registration fails"
        )
        assertEquals(
            fakeSyncRepository.startSyncIfNeededCallCount,
            0,
            "startSyncIfNeeded must not be called when Firestore registration fails"
        )
    }

    // ========== Navigation Callback Tests ==========

    @Test
    fun givenEmailSignUpScreen_whenLoginLinkClicked_thenTriggersLoginCallback() {
        // Given
        renderScreen()

        // When
        composeTestRule.onNodeWithText(signInAction, substring = true).performClick()
        composeTestRule.waitForIdle()

        // Then
        assertTrue(loginClickCalled, "Login callback should be called")
        assertFalse(navigateToScheduleCalled, "Navigate callback should not be called")
        assertFalse(backClickCalled, "Back callback should not be called")
    }

    @Test
    fun givenEmailSignUpScreen_whenBackButtonClicked_thenTriggersBackCallback() {
        // Given
        renderScreen()

        // When
        composeTestRule.onNodeWithText(backButton).performClick()
        composeTestRule.waitForIdle()

        // Then
        assertTrue(backClickCalled, "Back callback should be called")
        assertFalse(navigateToScheduleCalled, "Navigate callback should not be called")
        assertFalse(loginClickCalled, "Login callback should not be called")
    }

    // ========== UI Element State Tests ==========

    @Test
    fun givenEmailSignUpScreen_whenDisplayed_thenRegisterButtonIsEnabledAndClickable() {
        // Given
        renderScreen()

        // Then
        composeTestRule.onNodeWithText(registerButton).assertIsEnabled().assertHasClickAction()
    }

    @Test
    fun givenEmailSignUpScreen_whenDisplayed_thenBackButtonIsEnabledAndClickable() {
        // Given
        renderScreen()

        // Then
        composeTestRule.onNodeWithText(backButton).assertIsEnabled().assertHasClickAction()
    }

    // ========== Multiple Interaction Tests ==========

    @Test
    fun givenEmailSignUpScreen_whenRegisterClickedMultipleTimes_thenUseCaseIsCalledEachTime() {
        // Given
        renderScreen()

        // When
        composeTestRule.onNodeWithText(emailLabel).performTextInput("user@example.com")
        composeTestRule.onNodeWithText(passwordLabel).performTextInput("Secure@123")
        composeTestRule.onNodeWithText(confirmPasswordLabel).performTextInput("Secure@123")
        composeTestRule.onNodeWithText(registerButton).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(registerButton).performClick()
        composeTestRule.waitForIdle()

        // Then
        val signUpCallCount = fakeAuthRepository.callLog.count { it.startsWith("signUpWithEmail") }
        assertEquals(
            signUpCallCount,
            2,
            "Use case should be called once per register attempt, but was called $signUpCallCount time(s)"
        )
    }
}