package com.mbm.login.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbm.login.GoogleSignInManager
import com.mbm.login.GoogleSignInResult
import com.studentsapps.domain.login.model.AuthResult
import com.studentsapps.domain.login.usecases.RegisterUserIfNeededUseCase
import com.studentsapps.domain.login.usecases.SignInWithGoogleUseCase
import com.studentsapps.domain.sync.usecases.StartSyncIfNeededUseCase
import com.studentsapps.login.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the authentication screen.
 *
 * Handles authentication logic and manages UI state.
 * All UI state is now exposed through StateFlow and consumed by Compose.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val registerUserIfNeededUseCase: RegisterUserIfNeededUseCase,
    private val startSyncIfNeededUseCase: StartSyncIfNeededUseCase,
    private val googleSignInManager: GoogleSignInManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /**
     * Initiates Google Sign-In flow.
     *
     * @param context Android context (from LocalContext. Current in Compose)
     * @param webClientId The Google Web Client ID
     */
    fun initiateGoogleSignIn(context: Context, webClientId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = googleSignInManager.signIn(context, webClientId)) {
                is GoogleSignInResult.Success -> {
                    // ID token retrieved successfully, proceed with authentication
                    signInWithGoogleIdToken(result.idToken)
                }

                is GoogleSignInResult.Cancelled -> {
                    // User canceled - just hide loading, no error message
                    _uiState.update { it.copy(isLoading = false) }
                }

                is GoogleSignInResult.Error -> {
                    // Show error to user
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = AuthUiError.Message(R.string.login_error)
                        )
                    }
                }
            }
        }
    }

    /**
     * Sign in with Google ID token.
     * Internal method called after successfully retrieving the token.
     */
    private suspend fun signInWithGoogleIdToken(idToken: String) {
        signInWithGoogleUseCase(idToken).collect { result ->
            handleAuthResult(result)
        }
    }

    /**
     * Handles the authentication result and performs post-authentication actions.
     */
    private suspend fun handleAuthResult(result: AuthResult) {
        when (result) {
            is AuthResult.Success -> {
                val registrationSuccess = registerUserIfNeededUseCase(result.userId)

                if (registrationSuccess) {
                    // Start synchronization
                    startSyncIfNeededUseCase()

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            shouldNavigateToSchedule = true,
                            error = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = AuthUiError.Message(R.string.login_error)
                        )
                    }
                }
            }

            is AuthResult.Failure -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.error.toUiError()
                    )
                }
            }

            else -> {}
        }
    }

    /**
     * Resets the navigation flag after navigation is complete.
     */
    fun onNavigationComplete() {
        _uiState.update { it.copy(shouldNavigateToSchedule = false) }
    }

    /**
     * Dismisses the current error.
     */
    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}