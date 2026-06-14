package com.mbm.login

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import javax.inject.Inject

/**
 * Result of Google Sign-In credential retrieval.
 */
sealed class GoogleSignInResult {
    /**
     * Successfully retrieved the ID token.
     */
    data class Success(val idToken: String) : GoogleSignInResult()

    /**
     * User canceled the sign-in flow.
     */
    data object Cancelled : GoogleSignInResult()

    /**
     * An error occurred during sign-in.
     */
    data class Error(val exception: Exception) : GoogleSignInResult()
}

/**
 * Manager for Google Sign-In using Credential Manager API.
 *
 * This class encapsulates the Google Sign-In logic and can be used from Compose.
 * It handles credential retrieval and token parsing, returning a sealed result.
 */
class GoogleSignInManager @Inject constructor() {

    /**
     * Initiates Google Sign-In flow and retrieves the ID token.
     *
     * @param context Android context (can be application or activity context)
     * @param webClientId The Google Web Client ID from Firebase console
     * @return GoogleSignInResult indicating success, cancellation, or error
     */
    suspend fun signIn(context: Context, webClientId: String): GoogleSignInResult {
        return try {
            val googleIdOption = GetSignInWithGoogleOption
                .Builder(webClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credentialManager = CredentialManager.create(context)
            val result = credentialManager.getCredential(context, request)

            handleCredentialResult(result)
        } catch (_: GetCredentialCancellationException) {
            GoogleSignInResult.Cancelled
        } catch (e: GetCredentialException) {
            GoogleSignInResult.Error(e)
        } catch (e: Exception) {
            GoogleSignInResult.Error(e)
        }
    }

    /**
     * Processes the credential result and extracts the ID token.
     */
    private fun handleCredentialResult(
        result: androidx.credentials.GetCredentialResponse
    ): GoogleSignInResult {
        return try {
            when (val credential = result.credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)

                        GoogleSignInResult.Success(googleIdTokenCredential.idToken)
                    } else {
                        GoogleSignInResult.Error(
                            IllegalStateException("Unexpected credential type: ${credential.type}")
                        )
                    }
                }
                else -> {
                    GoogleSignInResult.Error(
                        IllegalStateException("Unexpected credential type")
                    )
                }
            }
        } catch (e: GoogleIdTokenParsingException) {
            GoogleSignInResult.Error(e)
        } catch (e: Exception) {
            GoogleSignInResult.Error(e)
        }
    }
}