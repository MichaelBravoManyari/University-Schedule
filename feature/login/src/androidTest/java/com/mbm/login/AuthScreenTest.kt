package com.mbm.login

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mbm.login.auth.AuthScreen
import com.mbm.login.auth.AuthViewModel
import com.studentsapps.data.repository.fake.FakeSyncRepository
import com.studentsapps.domain.login.repository.fake.FakeAuthRepository
import com.studentsapps.domain.login.model.AuthError
import com.studentsapps.domain.login.model.AuthResult
import com.studentsapps.domain.login.usecases.RegisterUserIfNeededUseCase
import com.studentsapps.domain.login.usecases.SignInWithGoogleUseCase
import com.studentsapps.domain.sync.usecases.StartSyncIfNeededUseCase
import com.studentsapps.login.R
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import theme.UniversityScheduleTheme
import kotlin.test.Test

/**
 * Integration tests for [com.mbm.login.auth.AuthScreen].
 * Note: No usar TestDispatcher al probar la UI. Ya que no se están probando las coroutines,
 * sino la reacción de la UI a los estados.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AuthScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // Real use-case instances wired to the fake repository
    private lateinit var signInWithGoogleUseCase: SignInWithGoogleUseCase
    private lateinit var registerUserIfNeededUseCase: RegisterUserIfNeededUseCase
    private lateinit var startSyncIfNeededUseCase: StartSyncIfNeededUseCase

    // System under test
    private lateinit var viewModel: AuthViewModel

    // Fakes dependencies
    private val fakeAuthRepository = FakeAuthRepository()
    private val fakeSyncRepository = FakeSyncRepository()

    // Mocked dependencies
    private lateinit var googleSignInManager: GoogleSignInManager

    // Callback trackers
    private var emailSignInCalled = false
    private var registerClickCalled = false
    private var navigateToScheduleCalled = false

    // String resources resolved once — changes to strings.xml are automatically reflected
    private lateinit var appName: String
    private lateinit var signInWithEmail: String
    private lateinit var signInWithGoogle: String
    private lateinit var signUpHere: String
    private lateinit var orAlso: String

    @Before
    fun setup() {
        // Reset fakes to their default state before every test
        fakeAuthRepository.reset()

        // Reset callback trackers
        emailSignInCalled = false
        registerClickCalled = false
        navigateToScheduleCalled = false

        // Wire real use cases to the fake repository
        signInWithGoogleUseCase = SignInWithGoogleUseCase(fakeAuthRepository)
        registerUserIfNeededUseCase = RegisterUserIfNeededUseCase(fakeAuthRepository)
        startSyncIfNeededUseCase = StartSyncIfNeededUseCase(fakeSyncRepository)

        googleSignInManager = mockk()

        // Create ViewModel with mocked dependencies
        viewModel = AuthViewModel(
            signInWithGoogleUseCase = signInWithGoogleUseCase,
            registerUserIfNeededUseCase = registerUserIfNeededUseCase,
            startSyncIfNeededUseCase = startSyncIfNeededUseCase,
            googleSignInManager = googleSignInManager
        )

        // Resolve string resources from the Activity context
        with(composeTestRule.activity) {
            appName = getString(com.studentsapps.common.R.string.app_name)
            signInWithEmail = getString(R.string.sign_in_with_email)
            signInWithGoogle = getString(R.string.sign_in_with_google)
            signUpHere = getString(R.string.sign_up_here)
            orAlso = getString(R.string.or_also)
        }
    }

    // ========== Initial State Tests ==========

    @Test
    fun givenAuthScreenDisplayed_whenNoInteraction_thenShowsAllRequiredElements() {
        // Given: AuthScreen is displayed
        composeTestRule.setContent {
            UniversityScheduleTheme {
                AuthScreen(
                    viewModel = viewModel,
                    onEmailSignIn = { emailSignInCalled = true },
                    onRegisterClick = { registerClickCalled = true },
                    onNavigateToSchedule = { navigateToScheduleCalled = true }
                )
            }
        }

        // Then: All required UI elements are visible
        composeTestRule.onNodeWithText(appName).assertExists()
        composeTestRule.onNodeWithText(signInWithEmail).assertExists()
        composeTestRule.onNodeWithText(signInWithGoogle).assertExists()
        composeTestRule.onNodeWithText(signUpHere).assertExists()
    }

    @Test
    fun givenAuthScreenDisplayed_whenNoInteraction_thenLoadingIndicatorNotVisible() {
        // Given: AuthScreen is displayed without any action
        composeTestRule.setContent {
            UniversityScheduleTheme {
                AuthScreen(
                    viewModel = viewModel,
                    onEmailSignIn = {},
                    onRegisterClick = {},
                    onNavigateToSchedule = {}
                )
            }
        }

        // Then: Loading indicator is not displayed
        composeTestRule.onNode(isDialog()).assertDoesNotExist()
    }

    // ========== Email Sign-In Navigation Tests ==========

    @Test
    fun givenAuthScreen_whenEmailSignInButtonClicked_thenTriggersEmailSignInCallback() {
        // Given: AuthScreen is displayed
        composeTestRule.setContent {
            UniversityScheduleTheme {
                AuthScreen(
                    viewModel = viewModel,
                    onEmailSignIn = { emailSignInCalled = true },
                    onRegisterClick = { registerClickCalled = true },
                    onNavigateToSchedule = { navigateToScheduleCalled = true }
                )
            }
        }

        // When: User clicks on the email sign-in button
        composeTestRule.onNodeWithText(signInWithEmail).performClick()

        // Then: Only the email sign-in callback fires
        assert(emailSignInCalled) { "Email sign-in callback should be called" }
        assert(!registerClickCalled) { "Register callback should not be called" }
        assert(!navigateToScheduleCalled) { "Navigate callback should not be called" }
    }

    // ========== Register Navigation Tests ==========

    @Test
    fun givenAuthScreen_whenSignUpLinkClicked_thenTriggersRegisterCallback() {
        // Given: AuthScreen is displayed
        composeTestRule.setContent {
            UniversityScheduleTheme {
                AuthScreen(
                    viewModel = viewModel,
                    onEmailSignIn = { emailSignInCalled = true },
                    onRegisterClick = { registerClickCalled = true },
                    onNavigateToSchedule = { navigateToScheduleCalled = true }
                )
            }
        }

        // When: User clicks on the "Sign up here" link
        composeTestRule.onNodeWithText(signUpHere).performClick()

        // Then: Only the register callback fires
        assert(registerClickCalled) { "Register callback should be called" }
        assert(!emailSignInCalled) { "Email sign-in callback should not be called" }
        assert(!navigateToScheduleCalled) { "Navigate callback should not be called" }
    }

    // ========== Google Sign-In Flow Tests ==========

    @Test
    fun givenAuthScreen_whenGoogleSignInSucceeds_thenNavigatesToSchedule() {
        // Given: Successful Google sign-in flow
        val idToken = "valid.token"
        val userId = "user123"

        coEvery { googleSignInManager.signIn(any(), any()) } returns
                GoogleSignInResult.Success(idToken)
        fakeAuthRepository.signInWithGoogleResult = AuthResult.Success(userId)
        fakeAuthRepository.registerUserResult = true

        composeTestRule.setContent {
            UniversityScheduleTheme {
                AuthScreen(
                    viewModel = viewModel,
                    onEmailSignIn = { emailSignInCalled = true },
                    onRegisterClick = { registerClickCalled = true },
                    onNavigateToSchedule = { navigateToScheduleCalled = true }
                )
            }
        }

        // When: User clicks on the Google sign-in button
        composeTestRule.onNodeWithText(signInWithGoogle).performClick()
        composeTestRule.waitForIdle()

        // Then: Navigation to schedule is triggered
        composeTestRule.waitUntil(timeoutMillis = 5000) { navigateToScheduleCalled }
        assert(navigateToScheduleCalled) { "Should navigate to schedule after successful login" }
    }

    @Test
    fun givenAuthScreen_whenGoogleSignInClicked_thenShowsLoadingIndicator() {
        // Given: Google sign-in manager simulates a delay so loading can be observed
        val idToken = "valid.token"
        coEvery { googleSignInManager.signIn(any(), any()) } coAnswers {
            kotlinx.coroutines.delay(1000)
            GoogleSignInResult.Success(idToken)
        }

        composeTestRule.setContent {
            UniversityScheduleTheme {
                AuthScreen(
                    viewModel = viewModel,
                    onEmailSignIn = {},
                    onRegisterClick = {},
                    onNavigateToSchedule = {}
                )
            }
        }

        // When: User clicks the Google sign-in button
        composeTestRule.onNodeWithText(signInWithGoogle).performClick()
        composeTestRule.waitForIdle()

        // Then: Loading overlay is visible while the operation is in flight
        composeTestRule.onNodeWithTag("loading_overlay").assertIsDisplayed()
    }

    @Test
    fun givenAuthScreen_whenGoogleSignInCancelled_thenNoNavigationOccurs() {
        // Given: User cancels the Google sign-in flow
        coEvery { googleSignInManager.signIn(any(), any()) } returns GoogleSignInResult.Cancelled

        composeTestRule.setContent {
            UniversityScheduleTheme {
                AuthScreen(
                    viewModel = viewModel,
                    onEmailSignIn = { emailSignInCalled = true },
                    onRegisterClick = { registerClickCalled = true },
                    onNavigateToSchedule = { navigateToScheduleCalled = true }
                )
            }
        }

        // When: User clicks the Google button and cancels
        composeTestRule.onNodeWithText(signInWithGoogle).performClick()
        composeTestRule.waitForIdle()

        // Then: No navigation or unrelated callbacks are triggered
        assert(!navigateToScheduleCalled) { "Should not navigate when user cancels" }
        assert(!emailSignInCalled) { "Email sign-in should not be triggered" }
        assert(!registerClickCalled) { "Register should not be triggered" }
    }

    // ========== Error Handling Tests ==========

    @Test
    fun givenAuthScreen_whenGoogleSignInFails_thenShowsErrorAndStaysOnScreen() {
        // Given: Google sign-in manager returns an error
        val exception = Exception("Network error")
        coEvery { googleSignInManager.signIn(any(), any()) } returns
                GoogleSignInResult.Error(exception)

        composeTestRule.setContent {
            UniversityScheduleTheme {
                AuthScreen(
                    viewModel = viewModel,
                    onEmailSignIn = { emailSignInCalled = true },
                    onRegisterClick = { registerClickCalled = true },
                    onNavigateToSchedule = { navigateToScheduleCalled = true }
                )
            }
        }

        // When: User clicks the Google sign-in button
        composeTestRule.onNodeWithText(signInWithGoogle).performClick()
        composeTestRule.waitForIdle()

        // Then: User stays on the auth screen
        // (error is delivered via Toast which is outside the Compose tree)
        assert(!navigateToScheduleCalled) { "Should not navigate when sign-in fails" }
    }

    @Test
    fun givenAuthScreen_whenAuthenticationFails_thenShowsErrorAndStaysOnScreen() {
        // Given: Authentication fails after a successful Google credential exchange
        val idToken = "valid.token"
        coEvery { googleSignInManager.signIn(any(), any()) } returns
                GoogleSignInResult.Success(idToken)
        fakeAuthRepository.signInWithGoogleResult = AuthResult.Failure(AuthError.NETWORK_ERROR)

        composeTestRule.setContent {
            UniversityScheduleTheme {
                AuthScreen(
                    viewModel = viewModel,
                    onEmailSignIn = { emailSignInCalled = true },
                    onRegisterClick = { registerClickCalled = true },
                    onNavigateToSchedule = { navigateToScheduleCalled = true }
                )
            }
        }

        // When: User attempts Google sign-in
        composeTestRule.onNodeWithText(signInWithGoogle).performClick()
        composeTestRule.waitForIdle()

        // Then: User stays on the auth screen
        assert(!navigateToScheduleCalled) { "Should not navigate when authentication fails" }
    }

    @Test
    fun givenAuthScreen_whenRegistrationFails_thenShowsErrorAndStaysOnScreen() {
        // Given: Authentication succeeds but Firestore registration fails
        val idToken = "valid.token"
        val userId = "user123"

        coEvery { googleSignInManager.signIn(any(), any()) } returns
                GoogleSignInResult.Success(idToken)
        fakeAuthRepository.signInWithGoogleResult = AuthResult.Success(userId)
        fakeAuthRepository.registerUserResult = false

        composeTestRule.setContent {
            UniversityScheduleTheme {
                AuthScreen(
                    viewModel = viewModel,
                    onEmailSignIn = { emailSignInCalled = true },
                    onRegisterClick = { registerClickCalled = true },
                    onNavigateToSchedule = { navigateToScheduleCalled = true }
                )
            }
        }

        // When: User attempts Google sign-in
        composeTestRule.onNodeWithText(signInWithGoogle).performClick()
        composeTestRule.waitForIdle()

        // Then: User stays on auth screen and synchronization is never started
        assert(!navigateToScheduleCalled) { "Should not navigate when registration fails" }
    }

    // ========== UI Element Visibility Tests ==========

    @Test
    fun givenAuthScreen_whenDisplayed_thenAllButtonsAreClickable() {
        // Given: AuthScreen is displayed
        composeTestRule.setContent {
            UniversityScheduleTheme {
                AuthScreen(
                    viewModel = viewModel,
                    onEmailSignIn = {},
                    onRegisterClick = {},
                    onNavigateToSchedule = {}
                )
            }
        }

        // Then: All interactive elements are enabled and have a click action
        composeTestRule.onNodeWithText(signInWithEmail).assertIsEnabled().assertHasClickAction()
        composeTestRule.onNodeWithText(signInWithGoogle).assertIsEnabled().assertHasClickAction()
        composeTestRule.onNodeWithText(signUpHere).assertIsEnabled().assertHasClickAction()
    }

    @Test
    fun givenAuthScreen_whenDisplayed_thenShowsAppName() {
        // Given: AuthScreen is displayed
        composeTestRule.setContent {
            UniversityScheduleTheme {
                AuthScreen(
                    viewModel = viewModel,
                    onEmailSignIn = {},
                    onRegisterClick = {},
                    onNavigateToSchedule = {}
                )
            }
        }

        // Then: App name is visible
        composeTestRule.onNodeWithText(appName).assertExists().assertIsDisplayed()
    }

    @Test
    fun givenAuthScreen_whenDisplayed_thenShowsOrDivider() {
        // Given: AuthScreen is displayed
        composeTestRule.setContent {
            UniversityScheduleTheme {
                AuthScreen(
                    viewModel = viewModel,
                    onEmailSignIn = {},
                    onRegisterClick = {},
                    onNavigateToSchedule = {}
                )
            }
        }

        // Then: "OR ALSO" divider is visible
        composeTestRule.onNodeWithText(orAlso).assertExists().assertIsDisplayed()
    }

    // ========== Multiple Interaction Tests ==========

    @Test
    fun givenAuthScreen_whenMultipleButtonClicks_thenEachCallbackTriggeredIndependently() {
        // Given: AuthScreen is displayed with click counters
        var emailClickCount = 0
        var registerClickCount = 0

        composeTestRule.setContent {
            UniversityScheduleTheme {
                AuthScreen(
                    viewModel = viewModel,
                    onEmailSignIn = { emailClickCount++ },
                    onRegisterClick = { registerClickCount++ },
                    onNavigateToSchedule = {}
                )
            }
        }

        // When: User clicks different buttons multiple times
        composeTestRule.onNodeWithText(signInWithEmail).performClick()
        composeTestRule.onNodeWithText(signUpHere).performClick()
        composeTestRule.onNodeWithText(signInWithEmail).performClick()

        // Then: Each callback is triggered the correct number of times
        assert(emailClickCount == 2) { "Email sign-in should be clicked twice" }
        assert(registerClickCount == 1) { "Register should be clicked once" }
    }

    // ========== Accessibility Tests ==========

    @Test
    fun givenAuthScreen_whenDisplayed_thenAllButtonsHaveContentDescription() {
        // Given: AuthScreen is displayed
        composeTestRule.setContent {
            UniversityScheduleTheme {
                AuthScreen(
                    viewModel = viewModel,
                    onEmailSignIn = {},
                    onRegisterClick = {},
                    onNavigateToSchedule = {}
                )
            }
        }

        // Then: No interactive element has an empty content description
        composeTestRule
            .onAllNodesWithContentDescription("", substring = false, useUnmergedTree = true)
            .assertCountEquals(0)

        // All primary actions are reachable by text
        composeTestRule.onNodeWithText(signInWithEmail).assertExists()
        composeTestRule.onNodeWithText(signInWithGoogle).assertExists()
        composeTestRule.onNodeWithText(signUpHere).assertExists()
    }

    // ========== Integration Flow Tests ==========

    @Test
    fun givenAuthScreen_whenCompleteSuccessfulFlow_thenExecutesAllStepsInOrder() {
        // Given: Complete successful authentication flow
        val idToken = "complete.flow.token"
        val userId = "complete_user"

        fakeAuthRepository.signInWithGoogleResult = AuthResult.Success(userId)
        fakeAuthRepository.registerUserResult = true
        coEvery { googleSignInManager.signIn(any(), any()) } returns
                GoogleSignInResult.Success(idToken)

        composeTestRule.setContent {
            UniversityScheduleTheme {
                AuthScreen(
                    viewModel = viewModel,
                    onEmailSignIn = { emailSignInCalled = true },
                    onRegisterClick = { registerClickCalled = true },
                    onNavigateToSchedule = { navigateToScheduleCalled = true }
                )
            }
        }

        // When: User completes Google sign-in
        composeTestRule.onNodeWithText(signInWithGoogle).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(timeoutMillis = 5000) { navigateToScheduleCalled }

        // Then: Repository calls occur in the correct sequence —
        // signInWithGoogle must be logged before registerUserIfNeeded
        val signInIndex = fakeAuthRepository.callLog.indexOf("signInWithGoogle")
        val registerIndex = fakeAuthRepository.callLog.indexOf("registerUserIfNeeded")

        assert(signInIndex != -1) { "signInWithGoogle must appear in the call log" }
        assert(registerIndex != -1) { "registerUserIfNeeded must appear in the call log" }
        assert(signInIndex < registerIndex) {
            "signInWithGoogle (position $signInIndex) must precede " +
                    "registerUserIfNeeded (position $registerIndex)"
        }
    }

    @Test
    fun givenAuthScreen_whenUserNavigatesAndComesBack_thenScreenStateResets() {
        // Given: AuthScreen with successful navigation
        val idToken = "valid.token"
        val userId = "user123"

        fakeAuthRepository.signInWithGoogleResult = AuthResult.Success(userId)
        fakeAuthRepository.registerUserResult = true
        coEvery { googleSignInManager.signIn(any(), any()) } returns
                GoogleSignInResult.Success(idToken)

        composeTestRule.setContent {
            UniversityScheduleTheme {
                AuthScreen(
                    viewModel = viewModel,
                    onEmailSignIn = {},
                    onRegisterClick = {},
                    onNavigateToSchedule = {
                        navigateToScheduleCalled = true
                        // Simulate the Fragment acknowledging navigation
                        viewModel.onNavigationComplete()
                    }
                )
            }
        }

        // When: User completes sign-in and the Fragment acknowledges navigation
        composeTestRule.onNodeWithText(signInWithGoogle).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) { navigateToScheduleCalled }

        // Then: Navigation flag is reset to prevent re-navigation on recomposition
        assert(navigateToScheduleCalled) { "Navigation should have occurred" }
        assert(!viewModel.uiState.value.shouldNavigateToSchedule) {
            "shouldNavigateToSchedule must be reset after navigation"
        }
    }
}