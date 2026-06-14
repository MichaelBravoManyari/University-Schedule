package com.mbm.login

import android.content.Context
import app.cash.turbine.test
import com.mbm.login.auth.AuthUiError
import com.mbm.login.auth.AuthViewModel
import com.studentsapps.domain.login.model.AuthError
import com.studentsapps.domain.login.model.AuthResult
import com.studentsapps.domain.login.usecases.RegisterUserIfNeededUseCase
import com.studentsapps.domain.login.usecases.SignInWithGoogleUseCase
import com.studentsapps.domain.sync.usecases.StartSyncIfNeededUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import junit.framework.TestCase.assertFalse
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
import kotlin.test.assertNull

/**
 * Unit tests for [com.mbm.login.auth.AuthViewModel].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    // Test dispatcher for controlling coroutine execution
    private val testDispatcher = StandardTestDispatcher()

    // Subject under test
    private lateinit var authViewModel: AuthViewModel

    // Dependencies
    private lateinit var signInWithGoogleUseCase: SignInWithGoogleUseCase
    private lateinit var registerUserIfNeededUseCase: RegisterUserIfNeededUseCase
    private lateinit var startSyncIfNeededUseCase: StartSyncIfNeededUseCase
    private lateinit var googleSignInManager: GoogleSignInManager
    private lateinit var mockContext: Context

    @Before
    fun setup() {
        // Given: Set up test dispatcher and mock dependencies
        Dispatchers.setMain(testDispatcher)

        signInWithGoogleUseCase = mockk()
        registerUserIfNeededUseCase = mockk()
        startSyncIfNeededUseCase = mockk()
        googleSignInManager = mockk()
        mockContext = mockk(relaxed = true)

        authViewModel = AuthViewModel(
            signInWithGoogleUseCase = signInWithGoogleUseCase,
            registerUserIfNeededUseCase = registerUserIfNeededUseCase,
            startSyncIfNeededUseCase = startSyncIfNeededUseCase,
            googleSignInManager = googleSignInManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ========== Successful Authentication Flow Tests ==========

    @Test
    fun givenValidCredentials_whenInitiateGoogleSignIn_thenAuthenticatesSuccessfully() = runTest {
        // Given: Successful Google sign-in, authentication, and registration
        val idToken = "valid.id.token"
        val userId = "user123"
        val webClientId = "web-client-id"

        coEvery { googleSignInManager.signIn(mockContext, webClientId) } returns
                GoogleSignInResult.Success(idToken)
        every { signInWithGoogleUseCase(idToken) } returns
                flowOf(AuthResult.Success(userId))
        coEvery { registerUserIfNeededUseCase(userId) } returns true
        coEvery { startSyncIfNeededUseCase() } just runs

        // When: Initiating Google sign-in
        authViewModel.uiState.test {
            // Initial state
            val initialState = awaitItem()
            assertFalse(initialState.isLoading)
            assertFalse(initialState.isAuthenticated)
            assertFalse(initialState.shouldNavigateToSchedule)
            assertNull(initialState.error)

            authViewModel.initiateGoogleSignIn(mockContext, webClientId)
            advanceUntilIdle()

            // Loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertNull(loadingState.error)

            // Success state
            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertTrue(successState.isAuthenticated)
            assertTrue(successState.shouldNavigateToSchedule)
            assertNull(successState.error)

            // Then: Verify all dependencies were called
            coVerify(exactly = 1) { googleSignInManager.signIn(mockContext, webClientId) }
            coVerify(exactly = 1) { signInWithGoogleUseCase(idToken) }
            coVerify(exactly = 1) { registerUserIfNeededUseCase(userId) }
            coVerify(exactly = 1) { startSyncIfNeededUseCase() }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenSuccessfulAuth_whenRegistrationSucceeds_thenStartsSynchronization() = runTest {
        // Given: Successful authentication and registration
        val idToken = "valid.token"
        val userId = "user456"
        val webClientId = "client-id"

        coEvery { googleSignInManager.signIn(mockContext, webClientId) } returns
                GoogleSignInResult.Success(idToken)
        every { signInWithGoogleUseCase(idToken) } returns
                flowOf(AuthResult.Success(userId))
        coEvery { registerUserIfNeededUseCase(userId) } returns true
        coEvery { startSyncIfNeededUseCase() } just runs

        // When: Authentication completes
        authViewModel.initiateGoogleSignIn(mockContext, webClientId)
        advanceUntilIdle()

        // Then: Synchronization is started
        coVerify(exactly = 1) { startSyncIfNeededUseCase() }
    }

    // ========== User Cancellation Tests ==========

    @Test
    fun givenUserCancelsSignIn_whenInitiateGoogleSignIn_thenStopsLoadingWithoutError() = runTest {
        // Given: User cancels the Google sign-in flow
        val webClientId = "web-client-id"
        coEvery { googleSignInManager.signIn(mockContext, webClientId) } returns
                GoogleSignInResult.Cancelled

        // When: Initiating Google sign-in
        authViewModel.uiState.test {
            val initialState = awaitItem()
            assertFalse(initialState.isLoading)

            authViewModel.initiateGoogleSignIn(mockContext, webClientId)
            advanceUntilIdle()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val cancelledState = awaitItem()
            assertFalse(cancelledState.isLoading)
            assertFalse(cancelledState.isAuthenticated)
            assertNull(cancelledState.error)

            // Then: No authentication use case is called
            coVerify(exactly = 0) { signInWithGoogleUseCase(any()) }
            coVerify(exactly = 0) { registerUserIfNeededUseCase(any()) }
            coVerify(exactly = 0) { startSyncIfNeededUseCase() }

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========== Error Handling Tests ==========

    @Test
    fun givenGoogleSignInError_whenInitiateGoogleSignIn_thenShowsError() = runTest {
        // Given: Google sign-in manager returns error
        val webClientId = "web-client-id"
        val exception = Exception("Sign-in failed")
        coEvery { googleSignInManager.signIn(mockContext, webClientId) } returns
                GoogleSignInResult.Error(exception)

        // When: Initiating Google sign-in
        authViewModel.uiState.test {
            awaitItem() // Initial state

            authViewModel.initiateGoogleSignIn(mockContext, webClientId)
            advanceUntilIdle()

            awaitItem() // Loading state

            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertTrue(errorState.error is AuthUiError.Message)
            assertFalse(errorState.isAuthenticated)

            // Then: No further authentication steps are performed
            coVerify(exactly = 0) { signInWithGoogleUseCase(any()) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenAuthenticationFailure_whenSigningIn_thenShowsAuthError() = runTest {
        // Given: Authentication fails with network error
        val idToken = "valid.token"
        val webClientId = "client-id"
        coEvery { googleSignInManager.signIn(mockContext, webClientId) } returns
                GoogleSignInResult.Success(idToken)
        every { signInWithGoogleUseCase(idToken) } returns
                flowOf(AuthResult.Failure(AuthError.NETWORK_ERROR))

        // When: Initiating Google sign-in
        authViewModel.uiState.test {
            awaitItem() // Initial state

            authViewModel.initiateGoogleSignIn(mockContext, webClientId)
            advanceUntilIdle()

            awaitItem() // Loading state

            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertTrue(errorState.error != null)
            assertFalse(errorState.isAuthenticated)
            assertFalse(errorState.shouldNavigateToSchedule)

            // Then: Registration and sync are not called
            coVerify(exactly = 0) { registerUserIfNeededUseCase(any()) }
            coVerify(exactly = 0) { startSyncIfNeededUseCase() }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenRegistrationFailure_whenAuthSucceeds_thenShowsError() = runTest {
        // Given: Authentication succeeds but registration fails
        val idToken = "valid.token"
        val userId = "user789"
        val webClientId = "client-id"

        coEvery { googleSignInManager.signIn(mockContext, webClientId) } returns
                GoogleSignInResult.Success(idToken)
        every { signInWithGoogleUseCase(idToken) } returns
                flowOf(AuthResult.Success(userId))
        coEvery { registerUserIfNeededUseCase(userId) } returns false

        // When: Initiating Google sign-in
        authViewModel.uiState.test {
            awaitItem() // Initial state

            authViewModel.initiateGoogleSignIn(mockContext, webClientId)
            advanceUntilIdle()

            awaitItem() // Loading state

            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertTrue(errorState.error is AuthUiError.Message)
            assertFalse(errorState.isAuthenticated)
            assertFalse(errorState.shouldNavigateToSchedule)

            // Then: Synchronization is not started
            coVerify(exactly = 0) { startSyncIfNeededUseCase() }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenInvalidCredentialsError_whenSigningIn_thenShowsInvalidCredentialsError() = runTest {
        // Given: Authentication fails with invalid credentials
        val idToken = "invalid.token"
        val webClientId = "client-id"
        coEvery { googleSignInManager.signIn(mockContext, webClientId) } returns
                GoogleSignInResult.Success(idToken)
        every { signInWithGoogleUseCase(idToken) } returns
                flowOf(AuthResult.Failure(AuthError.INVALID_CREDENTIALS))

        // When: Initiating Google sign-in
        authViewModel.uiState.test {
            awaitItem() // Initial state

            authViewModel.initiateGoogleSignIn(mockContext, webClientId)
            advanceUntilIdle()

            awaitItem() // Loading state

            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertTrue(errorState.error != null)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========== State Management Tests ==========

    @Test
    fun givenAuthenticatedState_whenOnNavigationComplete_thenResetsShouldNavigateFlag() = runTest {
        // Given: User is authenticated and should navigate
        val idToken = "valid.token"
        val userId = "user123"
        val webClientId = "client-id"

        coEvery { googleSignInManager.signIn(mockContext, webClientId) } returns
                GoogleSignInResult.Success(idToken)
        every { signInWithGoogleUseCase(idToken) } returns
                flowOf(AuthResult.Success(userId))
        coEvery { registerUserIfNeededUseCase(userId) } returns true
        coEvery { startSyncIfNeededUseCase() } just runs

        authViewModel.initiateGoogleSignIn(mockContext, webClientId)
        advanceUntilIdle()

        // When: Navigation is complete
        authViewModel.uiState.test {
            val stateBeforeNavigation = awaitItem()
            assertTrue(stateBeforeNavigation.shouldNavigateToSchedule)

            authViewModel.onNavigationComplete()

            val stateAfterNavigation = awaitItem()
            assertFalse(stateAfterNavigation.shouldNavigateToSchedule)
            assertTrue(stateAfterNavigation.isAuthenticated)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenErrorState_whenDismissError_thenClearsError() = runTest {
        // Given: ViewModel has an error state
        val webClientId = "client-id"
        val exception = Exception("Test error")
        coEvery { googleSignInManager.signIn(mockContext, webClientId) } returns
                GoogleSignInResult.Error(exception)

        authViewModel.initiateGoogleSignIn(mockContext, webClientId)
        advanceUntilIdle()

        // When: Error is dismissed
        authViewModel.uiState.test {
            val errorState = awaitItem()
            assertTrue(errorState.error != null)

            authViewModel.dismissError()

            val clearedState = awaitItem()
            assertNull(clearedState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenInitialState_whenNoAction_thenHasCorrectDefaultValues() = runTest {
        // Given: ViewModel is initialized
        // When: No action is taken
        authViewModel.uiState.test {
            val initialState = awaitItem()

            // Then: State has correct default values
            assertFalse(initialState.isLoading)
            assertFalse(initialState.isAuthenticated)
            assertFalse(initialState.shouldNavigateToSchedule)
            assertNull(initialState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========== Multiple Error Types Tests ==========

    @Test
    fun givenNetworkError_whenSigningIn_thenShowsNetworkError() = runTest {
        // Given: Network error during authentication
        val idToken = "valid.token"
        val webClientId = "client-id"
        coEvery { googleSignInManager.signIn(mockContext, webClientId) } returns
                GoogleSignInResult.Success(idToken)
        every { signInWithGoogleUseCase(idToken) } returns
                flowOf(AuthResult.Failure(AuthError.NETWORK_ERROR))

        // When: Initiating Google sign-in
        authViewModel.uiState.test {
            awaitItem() // Initial state

            authViewModel.initiateGoogleSignIn(mockContext, webClientId)
            advanceUntilIdle()

            awaitItem() // Loading state

            val errorState = awaitItem()
            assertTrue(errorState.error != null)
            assertFalse(errorState.isAuthenticated)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenUnknownError_whenSigningIn_thenShowsUnknownError() = runTest {
        // Given: Unknown error during authentication
        val idToken = "valid.token"
        val webClientId = "client-id"
        coEvery { googleSignInManager.signIn(mockContext, webClientId) } returns
                GoogleSignInResult.Success(idToken)
        every { signInWithGoogleUseCase(idToken) } returns
                flowOf(AuthResult.Failure(AuthError.UNKNOWN_ERROR))

        // When: Initiating Google sign-in
        authViewModel.uiState.test {
            awaitItem() // Initial state

            authViewModel.initiateGoogleSignIn(mockContext, webClientId)
            advanceUntilIdle()

            awaitItem() // Loading state

            val errorState = awaitItem()
            assertTrue(errorState.error != null)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========== Integration Flow Tests ==========

    @Test
    fun givenCompleteFlow_whenSignInSucceeds_thenAllStepsExecuteInOrder() = runTest {
        // Given: Complete successful flow
        val idToken = "complete.flow.token"
        val userId = "complete_user"
        val webClientId = "complete-client-id"

        coEvery { googleSignInManager.signIn(mockContext, webClientId) } returns
                GoogleSignInResult.Success(idToken)
        every { signInWithGoogleUseCase(idToken) } returns
                flowOf(AuthResult.Success(userId))
        coEvery { registerUserIfNeededUseCase(userId) } returns true
        coEvery { startSyncIfNeededUseCase() } just runs

        // When: Complete authentication flow
        authViewModel.initiateGoogleSignIn(mockContext, webClientId)
        advanceUntilIdle()

        // Then: Verify execution order
        coVerify(ordering = io.mockk.Ordering.SEQUENCE) {
            googleSignInManager.signIn(mockContext, webClientId)
            signInWithGoogleUseCase(idToken)
            registerUserIfNeededUseCase(userId)
            startSyncIfNeededUseCase()
        }
    }

    @Test
    fun givenMultipleSignInAttempts_whenCalledSequentially_thenEachAttemptProcessedIndependently() = runTest {
        // Given: Multiple sign-in attempts
        val webClientId = "client-id"
        val firstToken = "first.token"
        val secondToken = "second.token"
        val firstUserId = "user1"
        val secondUserId = "user2"

        // When: First attempt succeeds
        coEvery { googleSignInManager.signIn(mockContext, webClientId) } returns
                GoogleSignInResult.Success(firstToken)
        every { signInWithGoogleUseCase(firstToken) } returns
                flowOf(AuthResult.Success(firstUserId))
        coEvery { registerUserIfNeededUseCase(firstUserId) } returns true
        coEvery { startSyncIfNeededUseCase() } just runs

        authViewModel.initiateGoogleSignIn(mockContext, webClientId)
        advanceUntilIdle()

        // Then: First attempt completes
        coVerify(exactly = 1) { registerUserIfNeededUseCase(firstUserId) }

        // When: Second attempt with different credentials
        coEvery { googleSignInManager.signIn(mockContext, webClientId) } returns
                GoogleSignInResult.Success(secondToken)
        every { signInWithGoogleUseCase(secondToken) } returns
                flowOf(AuthResult.Success(secondUserId))
        coEvery { registerUserIfNeededUseCase(secondUserId) } returns true

        authViewModel.initiateGoogleSignIn(mockContext, webClientId)
        advanceUntilIdle()

        // Then: Second attempt also completes
        coVerify(exactly = 1) { registerUserIfNeededUseCase(secondUserId) }
        coVerify(exactly = 2) { startSyncIfNeededUseCase() }
    }
}