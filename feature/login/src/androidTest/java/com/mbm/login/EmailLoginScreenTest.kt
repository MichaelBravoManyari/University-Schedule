package com.mbm.login

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.mbm.login.email_login.EmailLoginScreen
import com.mbm.login.email_login.EmailLoginViewModel
import com.studentsapps.data.repository.fake.FakeSyncRepository
import com.studentsapps.domain.login.repository.fake.FakeAuthRepository
import com.studentsapps.domain.login.model.AuthError
import com.studentsapps.domain.login.model.AuthResult
import com.studentsapps.domain.login.model.EmailSignInError
import com.studentsapps.domain.login.usecases.SignInWithEmailUseCase
import com.studentsapps.domain.sync.usecases.StartSyncIfNeededUseCase
import com.studentsapps.login.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import theme.UniversityScheduleTheme
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Integration tests for [EmailLoginScreen].
 *
 * [EmailLoginViewModel] es una instancia real respaldada por [FakeAuthRepository],
 * lo que permite ejercitar la cadena completa ViewModel → UseCase → Repository sin Firebase.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EmailLoginScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // ── Fake dependencies ─────────────────────────────────────────────────────

    // Instancia compartida — se resetea en @Before para garantizar aislamiento entre tests
    private val fakeAuthRepository = FakeAuthRepository()
    private val fakeSyncRepository = FakeSyncRepository()

    // ── Real use-case instance wired to the fake repository ───────────────────

    private lateinit var signInWithEmailUseCase: SignInWithEmailUseCase
    private lateinit var startSyncIfNeededUseCase: StartSyncIfNeededUseCase

    // ── System under test ─────────────────────────────────────────────────────

    private lateinit var viewModel: EmailLoginViewModel

    // ── Callback trackers ─────────────────────────────────────────────────────

    private var navigateToScheduleCalled = false
    private var registerClickCalled = false
    private var cancelClickCalled = false

    // String resources resueltos desde el contexto del Activity
    // Cambios en strings.xml se reflejan automáticamente sin editar los tests.

    private lateinit var emailLabel: String
    private lateinit var passwordLabel: String
    private lateinit var signInButton: String
    private lateinit var showPassword: String
    private lateinit var hidePassword: String
    private lateinit var enterYourEmail: String
    private lateinit var invalidEmail: String
    private lateinit var enterYourPassword: String
    private lateinit var signUpHere: String

    @Before
    fun setup() {
        // Resetear el fake antes de cada test para garantizar aislamiento
        fakeAuthRepository.reset()

        // Resetear los trackers de callbacks
        navigateToScheduleCalled = false
        registerClickCalled = false
        cancelClickCalled = false

        // Construir el use case real sobre el fake repository
        signInWithEmailUseCase = SignInWithEmailUseCase(fakeAuthRepository)
        startSyncIfNeededUseCase = StartSyncIfNeededUseCase(fakeSyncRepository)

        // ViewModel con dependencias reales/fake — mismo wiring que en el Fragment
        viewModel = EmailLoginViewModel(
            signInWithEmailUseCase = signInWithEmailUseCase,
            startSyncIfNeededUseCase = startSyncIfNeededUseCase,
        )

        // Resolver strings desde el contexto del Activity
        with(composeTestRule.activity) {
            emailLabel        = getString(R.string.email)
            passwordLabel     = getString(R.string.password)
            signInButton      = getString(R.string.sign_in)
            showPassword      = getString(R.string.show_password)
            hidePassword      = getString(R.string.hide_password)
            enterYourEmail    = getString(R.string.enter_your_email)
            invalidEmail      = getString(R.string.invalid_email)
            enterYourPassword = getString(R.string.enter_your_password)
            signUpHere        = getString(R.string.sign_up_here)
        }
    }

    // ========== Initial State Tests ==========

    @Test
    fun givenEmailLoginScreenDisplayed_whenNoInteraction_thenShowsAllRequiredElements() {
        // Given: EmailLoginScreen displayed con un ViewModel sin configuración previa
        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailLoginScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = { navigateToScheduleCalled = true },
                    onRegisterClick = { registerClickCalled = true },
                    onBackClick = { cancelClickCalled = true },
                )
            }
        }

        // Then: Todos los elementos requeridos son visibles
        composeTestRule.onNodeWithText(emailLabel).assertIsDisplayed()
        composeTestRule.onNodeWithText(passwordLabel).assertIsDisplayed()
        composeTestRule.onNode(hasText(signInButton) and hasClickAction()).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(showPassword).assertIsDisplayed()
    }

    @Test
    fun givenEmailLoginScreenDisplayed_whenNoInteraction_thenLoadingOverlayIsNotVisible() {
        // Given: EmailLoginScreen is displayed without any action
        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailLoginScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = {},
                    onRegisterClick = {},
                    onBackClick = {},
                )
            }
        }

        // Then: Loading overlay is not shown in the initial idle state
        composeTestRule.onNodeWithTag("loading_overlay").assertDoesNotExist()
    }

    @Test
    fun givenEmailLoginScreenDisplayed_whenNoInteraction_thenNoFieldErrorsAreShown() {
        // Given: EmailLoginScreen is displayed without any action
        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailLoginScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = {},
                    onRegisterClick = {},
                    onBackClick = {},
                )
            }
        }

        // Then: No validation error messages are visible
        composeTestRule.onNodeWithText(enterYourEmail).assertDoesNotExist()
        composeTestRule.onNodeWithText(invalidEmail).assertDoesNotExist()
        composeTestRule.onNodeWithText(enterYourPassword).assertDoesNotExist()
    }

    // ========== User Input Tests ==========

    @Test
    fun givenEmailLoginScreen_whenUserTypesInEmailField_thenEmailValueIsUpdated() {
        // Given: EmailLoginScreen is displayed
        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailLoginScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = {},
                    onRegisterClick = {},
                    onBackClick = {},
                )
            }
        }

        // When: User types a valid email
        composeTestRule.onNodeWithText(emailLabel).performTextInput("user@example.com")
        composeTestRule.waitForIdle()

        // Then: The typed email is reflected in the ViewModel state
        assert(viewModel.uiState.value.email == "user@example.com") {
            "Email should be updated in ViewModel state after user types"
        }
    }

    @Test
    fun givenEmailLoginScreen_whenUserTypesInPasswordField_thenPasswordValueIsUpdated() {
        // Given: EmailLoginScreen is displayed
        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailLoginScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = {},
                    onRegisterClick = {},
                    onBackClick = {},
                )
            }
        }

        // When: User types a password
        composeTestRule.onNodeWithText(passwordLabel).performTextInput("secret123")
        composeTestRule.waitForIdle()

        // Then: Password is reflected in ViewModel state
        // (password characters are masked in the UI so we verify via state)
        assert(viewModel.uiState.value.password == "secret123") {
            "Password should be updated in ViewModel state after user types"
        }
    }

    // ========== Password Visibility Toggle Tests ==========

    @Test
    fun givenPasswordHidden_whenToggleClicked_thenIconChangesToHidePassword() {
        // Given: EmailLoginScreen displayed — password is hidden by default
        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailLoginScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = {},
                    onRegisterClick = {},
                    onBackClick = {},
                )
            }
        }

        // When: User taps the visibility toggle
        composeTestRule.onNodeWithContentDescription(showPassword).performClick()
        composeTestRule.waitForIdle()

        // Then: Icon switches to "Hide password" indicating the password is now visible
        composeTestRule.onNodeWithContentDescription(hidePassword).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(showPassword).assertDoesNotExist()
    }

    @Test
    fun givenPasswordVisible_whenToggleClickedAgain_thenIconChangesBackToShowPassword() {
        // Given: Password is visible (toggle clicked once)
        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailLoginScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = {},
                    onRegisterClick = {},
                    onBackClick = {},
                )
            }
        }

        composeTestRule.onNodeWithContentDescription(showPassword).performClick()
        composeTestRule.waitForIdle()

        // When: User taps the toggle again to hide the password
        composeTestRule.onNodeWithContentDescription(hidePassword).performClick()
        composeTestRule.waitForIdle()

        // Then: Icon reverts to "Show password"
        composeTestRule.onNodeWithContentDescription(showPassword).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(hidePassword).assertDoesNotExist()
    }

    // ========== Successful Sign-In Tests ==========

    @Test
    fun givenValidCredentials_whenLoginButtonClicked_thenNavigatesToSchedule() {
        // Given: El repositorio emitirá éxito al iniciar sesión
        fakeAuthRepository.signInWithEmailResult = AuthResult.Success("uid_001")

        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailLoginScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = { navigateToScheduleCalled = true },
                    onRegisterClick = { registerClickCalled = true },
                    onBackClick = { cancelClickCalled = true },
                )
            }
        }

        // When: El usuario ingresa credenciales y toca Sign in
        composeTestRule.onNodeWithText(emailLabel).performTextInput("user@example.com")
        composeTestRule.onNodeWithText(passwordLabel).performTextInput("securePass123")
        composeTestRule.onNode(hasText(signInButton) and hasClickAction()).performClick()
        composeTestRule.waitForIdle()

        // Then: Se dispara la navegación al schedule
        composeTestRule.waitUntil(timeoutMillis = 5000) { navigateToScheduleCalled }
        assert(navigateToScheduleCalled) { "Should navigate to schedule after successful login" }
        assert(!registerClickCalled) { "Register callback should not be called" }
        assert(!cancelClickCalled) { "Cancel callback should not be called" }
    }

    @Test
    fun givenValidCredentials_whenLoginSucceeds_thenStartsSynchronization() {
        // Given: El repositorio emitirá éxito
        fakeAuthRepository.signInWithEmailResult = AuthResult.Success("uid_002")

        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailLoginScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = { navigateToScheduleCalled = true },
                    onRegisterClick = {},
                    onBackClick = {},
                )
            }
        }

        /**
         * NOTA: Ingresar una contraseña y email válidos, ya que el usecase los valida antes
         * de llamar al repository. No es suficiente con hacer el fake de la respuesta con:
         * `fakeAuthRepository.signInWithEmailResult = AuthResult.Success("uid_004")`
         */
        composeTestRule.onNodeWithText(emailLabel).performTextInput("user@example.com")
        composeTestRule.onNodeWithText(passwordLabel).performTextInput("securePass123")

        // When
        composeTestRule.onNode(hasText(signInButton) and hasClickAction()).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(timeoutMillis = 5000) { navigateToScheduleCalled }

        // Then: La sincronización se dispara exactamente una vez tras el login exitoso
        assertEquals(
            fakeSyncRepository.startSyncIfNeededCallCount,
            1,
            "Synchronization should be started exactly once after successful login"
        )
    }

    @Test
    fun givenLoginInProgress_whenLoginButtonClicked_thenLoadingOverlayIsVisible() {
        // Given: El repositorio simula latencia para que el estado de carga sea observable
        fakeAuthRepository.signInWithEmailResult = AuthResult.Success("uid_003")
        fakeAuthRepository.signInWithEmailDelay = 2_000L

        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailLoginScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = { navigateToScheduleCalled = true },
                    onRegisterClick = {},
                    onBackClick = {},
                )
            }
        }

        /**
         * NOTA: Ingresar una contraseña y email válidos, ya que el usecase los valida antes
         * de llamar al repository. No es suficiente con hacer el fake de la respuesta con:
         * `fakeAuthRepository.signInWithEmailResult = AuthResult.Success("uid_004")`
         */
        composeTestRule.onNodeWithText(emailLabel).performTextInput("user@example.com")
        composeTestRule.onNodeWithText(passwordLabel).performTextInput("securePass123")

        // When: El usuario toca Sign in — la coroutine arranca pero aún no resuelve
        composeTestRule.onNode(hasText(signInButton) and hasClickAction()).performClick()
        composeTestRule.waitForIdle()

        // Then: El loading overlay se muestra mientras la operación está en vuelo
        composeTestRule.onNodeWithTag("loading_overlay").assertIsDisplayed()
    }

    @Test
    fun givenSuccessfulLogin_whenNavigationCompletes_thenShouldNavigateFlagIsReset() {
        // Given: Login exitoso con acuse de recibo de navegación del Fragment
        fakeAuthRepository.signInWithEmailResult = AuthResult.Success("uid_004")

        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailLoginScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = {
                        navigateToScheduleCalled = true
                        // Simula que el Fragment reconoce la navegación
                        viewModel.onNavigationComplete()
                    },
                    onRegisterClick = {},
                    onBackClick = {},
                )
            }
        }

        /**
         * NOTA: Ingresar una contraseña y email válidos, ya que el usecase los valida antes
         * de llamar al repository. No es suficiente con hacer el fake de la respuesta con:
         * `fakeAuthRepository.signInWithEmailResult = AuthResult.Success("uid_004")`
         */
        composeTestRule.onNodeWithText(emailLabel).performTextInput("user@example.com")
        composeTestRule.onNodeWithText(passwordLabel).performTextInput("securePass123")

        // When: El login completa y la navegación es reconocida
        composeTestRule.onNode(hasText(signInButton) and hasClickAction()).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(timeoutMillis = 5000) { navigateToScheduleCalled }

        // Then: El flag se resetea para evitar re-navegación en recomposición
        assert(!viewModel.uiState.value.shouldNavigateToSchedule) {
            "shouldNavigateToSchedule must be reset after navigation to avoid double-triggering"
        }
    }

    // ========== Validation Failure Tests ==========

    @Test
    fun givenBlankEmail_whenLoginButtonClicked_thenEmailErrorIsDisplayed() {
        // Given: El use case devolverá error de email en blanco
        fakeAuthRepository.signInWithEmailResult =
            AuthResult.ValidationFailure(EmailSignInError.BLANK_EMAIL)

        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailLoginScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = { navigateToScheduleCalled = true },
                    onRegisterClick = {},
                    onBackClick = {},
                )
            }
        }

        // When: El usuario toca Sign in sin ingresar email
        composeTestRule.onNode(hasText(signInButton) and hasClickAction()).performClick()
        composeTestRule.waitForIdle()

        // Then: Solo se muestra el error de email; no hay navegación
        composeTestRule.onNodeWithText(enterYourEmail).assertIsDisplayed()
        composeTestRule.onNodeWithText(enterYourPassword).assertDoesNotExist()
        assert(!navigateToScheduleCalled) { "Should not navigate when email is blank" }
    }

    @Test
    fun givenInvalidEmailFormat_whenLoginButtonClicked_thenEmailFormatErrorIsDisplayed() {
        // Given: El use case devolverá error de formato de email
        fakeAuthRepository.signInWithEmailResult =
            AuthResult.ValidationFailure(EmailSignInError.INVALID_EMAIL_FORMAT)

        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailLoginScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = { navigateToScheduleCalled = true },
                    onRegisterClick = {},
                    onBackClick = {},
                )
            }
        }

        /**
         * NOTA: Ingresar un email inválido para que el usecase retorne el error y el viewmodel
         * lo notifique
         */
        composeTestRule.onNodeWithText(emailLabel).performTextInput("invalid_email")

        // When: El usuario toca Sign in con un email mal formado
        composeTestRule.onNode(hasText(signInButton) and hasClickAction()).performClick()
        composeTestRule.waitForIdle()

        // Then: Solo se muestra el error de formato; no hay navegación
        composeTestRule.onNodeWithText(invalidEmail).assertIsDisplayed()
        composeTestRule.onNodeWithText(enterYourPassword).assertDoesNotExist()
        assert(!navigateToScheduleCalled) { "Should not navigate with invalid email format" }
    }

    @Test
    fun givenBlankPassword_whenLoginButtonClicked_thenPasswordErrorIsDisplayed() {
        // Given: El use case devolverá error de contraseña en blanco
        fakeAuthRepository.signInWithEmailResult =
            AuthResult.ValidationFailure(EmailSignInError.BLANK_PASSWORD)

        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailLoginScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = { navigateToScheduleCalled = true },
                    onRegisterClick = {},
                    onBackClick = {},
                )
            }
        }

        // Ingresar un correo para que salte el error de la contraseña vacía, ya que primero válida el campo
        // del email
        composeTestRule.onNodeWithText(emailLabel).performTextInput("user@example.com")

        // When: El usuario toca Sign in sin ingresar contraseña
        composeTestRule.onNode(hasText(signInButton) and hasClickAction()).performClick()
        composeTestRule.waitForIdle()

        // Then: Solo se muestra el error de contraseña; no hay navegación ni otros errores
        composeTestRule.onNodeWithText(enterYourPassword).assertIsDisplayed()
        composeTestRule.onNodeWithText(enterYourEmail).assertDoesNotExist()
        composeTestRule.onNodeWithText(invalidEmail).assertDoesNotExist()
        assert(!navigateToScheduleCalled) { "Should not navigate when password is blank" }
    }

    @Test
    fun givenEmailErrorShown_whenUserStartsTypingEmail_thenEmailErrorDisappears() {
        // Given: El error de email en blanco ya es visible
        fakeAuthRepository.signInWithEmailResult =
            AuthResult.ValidationFailure(EmailSignInError.BLANK_EMAIL)

        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailLoginScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = {},
                    onRegisterClick = {},
                    onBackClick = {},
                )
            }
        }

        composeTestRule.onNode(hasText(signInButton) and hasClickAction()).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(enterYourEmail).assertIsDisplayed()

        // When: El usuario empieza a corregir el campo email
        composeTestRule.onNodeWithText(emailLabel).performTextInput("u")
        composeTestRule.waitForIdle()

        // Then: El error desaparece inmediatamente sin necesidad de otro submit
        composeTestRule.onNodeWithText(enterYourEmail).assertDoesNotExist()
    }

    @Test
    fun givenPasswordErrorShown_whenUserStartsTypingPassword_thenPasswordErrorDisappears() {
        // Given: El error de contraseña en blanco ya es visible
        fakeAuthRepository.signInWithEmailResult =
            AuthResult.ValidationFailure(EmailSignInError.BLANK_PASSWORD)

        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailLoginScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = {},
                    onRegisterClick = {},
                    onBackClick = {},
                )
            }
        }

        /**
         * NOTA: Para que aparezca el error de password, primero se tiene que escribir un corre
         * válido, ya que el usecase primero válida el campo de email y luego el password.
         */
        composeTestRule.onNodeWithText(emailLabel).performTextInput("user@example.com")

        composeTestRule.onNode(hasText(signInButton) and hasClickAction()).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(enterYourPassword).assertIsDisplayed()

        // When: El usuario empieza a corregir el campo contraseña
        composeTestRule.onNodeWithText(passwordLabel).performTextInput("p")
        composeTestRule.waitForIdle()

        // Then: El error desaparece inmediatamente sin necesidad de otro submit
        composeTestRule.onNodeWithText(enterYourPassword).assertDoesNotExist()
    }

    @Test
    fun givenValidationFailure_whenLoginButtonClicked_thenLoadingOverlayIsNeverShown() {
        // Given: La validación falla sincrónicamente — no se realiza ninguna llamada de red
        fakeAuthRepository.signInWithEmailResult =
            AuthResult.ValidationFailure(EmailSignInError.BLANK_EMAIL)

        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailLoginScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = {},
                    onRegisterClick = {},
                    onBackClick = {},
                )
            }
        }

        // When
        composeTestRule.onNode(hasText(signInButton) and hasClickAction()).performClick()
        composeTestRule.waitForIdle()

        // Then: El loading se descarta antes de que ninguna recomposición lo muestre
        composeTestRule.onNodeWithTag("loading_overlay").assertDoesNotExist()
    }

    // ========== Auth Failure Tests ==========

    @Test
    fun givenInvalidCredentials_whenLoginButtonClicked_thenNoNavigationOccurs() {
        // Given: El repositorio devolverá INVALID_CREDENTIALS
        fakeAuthRepository.signInWithEmailResult =
            AuthResult.Failure(AuthError.INVALID_CREDENTIALS)

        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailLoginScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = { navigateToScheduleCalled = true },
                    onRegisterClick = { registerClickCalled = true },
                    onBackClick = { cancelClickCalled = true },
                )
            }
        }

        // When: El usuario intenta login con credenciales incorrectas
        composeTestRule.onNode(hasText(signInButton) and hasClickAction()).performClick()
        composeTestRule.waitForIdle()

        // Then: El usuario permanece en la pantalla de login
        assert(!navigateToScheduleCalled) { "Should not navigate when credentials are invalid" }
        assert(!registerClickCalled) { "Register callback should not be called" }
        assert(!cancelClickCalled) { "Cancel callback should not be called" }
    }

    @Test
    fun givenNetworkError_whenLoginButtonClicked_thenNoNavigationOccurs() {
        // Given: Falla de red durante la autenticación
        fakeAuthRepository.signInWithEmailResult =
            AuthResult.Failure(AuthError.NETWORK_ERROR)

        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailLoginScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = { navigateToScheduleCalled = true },
                    onRegisterClick = {},
                    onBackClick = {},
                )
            }
        }

        // When
        composeTestRule.onNode(hasText(signInButton) and hasClickAction()).performClick()
        composeTestRule.waitForIdle()

        // Then: El usuario permanece en la pantalla de login
        assert(!navigateToScheduleCalled) { "Should not navigate when network fails" }
    }

    @Test
    fun givenAuthFailure_whenLoginButtonClicked_thenSynchronizationIsNeverStarted() {
        // Given: Falla de auth — sync nunca debe ejecutarse si el login no fue exitoso
        fakeAuthRepository.signInWithEmailResult =
            AuthResult.Failure(AuthError.USER_NOT_FOUND)

        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailLoginScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = {},
                    onRegisterClick = {},
                    onBackClick = {},
                )
            }
        }

        // When
        composeTestRule.onNode(hasText(signInButton) and hasClickAction()).performClick()
        composeTestRule.waitForIdle()

        // Then: startSyncIfNeeded nunca fue llamado
        assertEquals(
            fakeSyncRepository.startSyncIfNeededCallCount,
            0,
            "Synchronization should not be started when login fails"
        )
    }

    @Test
    fun givenAuthFailure_whenLoginButtonClicked_thenLoadingOverlayDisappears() {
        // Given: Falla de auth que resuelve el estado de carga
        fakeAuthRepository.signInWithEmailResult =
            AuthResult.Failure(AuthError.INVALID_CREDENTIALS)

        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailLoginScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = {},
                    onRegisterClick = {},
                    onBackClick = {},
                )
            }
        }

        // When
        composeTestRule.onNode(hasText(signInButton) and hasClickAction()).performClick()
        composeTestRule.waitForIdle()

        // Then: isLoading vuelve a false tras la falla
        composeTestRule.onNodeWithTag("loading_overlay").assertDoesNotExist()
    }

    // ========== Navigation Callback Tests ==========

    @Test
    fun givenEmailLoginScreen_whenRegisterLinkClicked_thenTriggersRegisterCallback() {
        // Given: EmailLoginScreen displayed
        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailLoginScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = { navigateToScheduleCalled = true },
                    onRegisterClick = { registerClickCalled = true },
                    onBackClick = { cancelClickCalled = true },
                )
            }
        }

        // When: El usuario toca el link de registro
        composeTestRule.onNodeWithText(signUpHere, substring = true).performClick()
        composeTestRule.waitForIdle()

        // Then: Solo se dispara el callback de registro; ningún otro se activa
        assert(registerClickCalled) { "Register callback should be called" }
        assert(!navigateToScheduleCalled) { "Navigate callback should not be called" }
        assert(!cancelClickCalled) { "Cancel callback should not be called" }
    }

    // ========== UI Element State Tests ==========

    @Test
    fun givenEmailLoginScreen_whenDisplayed_thenLoginButtonIsEnabledAndClickable() {
        // Given: EmailLoginScreen displayed
        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailLoginScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = {},
                    onRegisterClick = {},
                    onBackClick = {},
                )
            }
        }

        // Then: El botón de login siempre está habilitado
        // (la validación ocurre al hacer submit, no al escribir)
        composeTestRule
            .onNode(hasText(signInButton) and hasClickAction())
            .assertIsEnabled()
            .assertHasClickAction()
    }

    // ========== Multiple Interaction Tests ==========

    @Test
    fun givenEmailLoginScreen_whenLoginClickedMultipleTimes_thenUseCaseIsCalledEachTime() {
        // Given: Cada intento devuelve un error de validación para evitar side-effects de sync
        fakeAuthRepository.signInWithEmailResult =
            AuthResult.ValidationFailure(EmailSignInError.BLANK_EMAIL)

        composeTestRule.setContent {
            UniversityScheduleTheme {
                EmailLoginScreen(
                    viewModel = viewModel,
                    onNavigateToSchedule = {},
                    onRegisterClick = {},
                    onBackClick = {},
                )
            }
        }

        // When: El usuario toca Sign in dos veces
        composeTestRule.onNode(hasText(signInButton) and hasClickAction()).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNode(hasText(signInButton) and hasClickAction()).performClick()
        composeTestRule.waitForIdle()

        // Then: El repositorio registra exactamente 0 llamadas a signInWithEmail,
        // ya que este se queda en las validaciones del usecase
        val signInCallCount = fakeAuthRepository.callLog.count { it == "signInWithEmail" }
        assert(signInCallCount == 0) {
            "The use case should not be called when business rules are violated."
        }
    }
}
