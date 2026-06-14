package com.studentsapps.domain.login.repository.fake

import com.studentsapps.domain.login.model.AuthResult
import com.studentsapps.domain.login.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Fake implementation of [com.studentsapps.domain.login.repository.AuthRepository] for use in tests.
 *
 * ## Configuration
 * Each property controls the outcome of one operation. Set them in each test's
 * Given block **before** the action is triggered:
 *
 * ```kotlin
 * fakeAuthRepository.signInWithGoogleResult = AuthResult.Success("uid_001")
 * fakeAuthRepository.registerUserResult      = true
 * fakeAuthRepository.signInWithGoogleDelay   = 1_000L  // simulate network latency
 * ```
 *
 * ## Call tracking
 * The [callLog] records the name of every method that was invoked, in order.
 * Use it to verify execution sequences without mocking frameworks:
 *
 * ```kotlin
 * assert(fakeAuthRepository.callLog[0] == "signInWithGoogle")
 * assert(fakeAuthRepository.callLog[1] == "registerUserIfNeeded")
 * ```
 */
class FakeAuthRepository : AuthRepository {

    // ── Configurable results ──────────────────────────────────────────────────

    /**
     * Result emitted by [signInWithGoogle].
     * Defaults to a generic success so tests that don't care about the value
     * work without extra configuration.
     */
    var signInWithGoogleResult: AuthResult = AuthResult.Success(DEFAULT_USER_ID)

    /**
     * Result emitted by [signInWithEmail].
     * Defaults to a generic success.
     */
    var signInWithEmailResult: AuthResult = AuthResult.Success(DEFAULT_USER_ID)

    /**
     * Result emitted by [signUpWithEmail].
     * Defaults to a generic success.
     */
    var signUpWithEmailResult: AuthResult = AuthResult.Success(DEFAULT_USER_ID)

    /**
     * Value returned by [registerUserIfNeeded].
     * Defaults to `true` (registration succeeds).
     */
    var registerUserResult: Boolean = true

    /**
     * Optional artificial delay (in milliseconds) applied before [signInWithGoogle]
     * emits its result. Use this to keep isLoading visible long enough to be
     * asserted in tests that verify the loading state.
     * Set to `0` (default) for no delay.
     */
    var signInWithGoogleDelay: Long = 0L

    /**
     * Optional artificial delay (in milliseconds) applied before [signInWithEmail]
     * emits its result. Use this to keep isLoading visible long enough to be
     * asserted in tests that verify the loading state.
     * Set to `0` (default) for no delay.
     */
    var signInWithEmailDelay: Long = 0L

    /**
     * Optional artificial delay (in milliseconds) applied before [signUpWithEmail]
     * emits its result. Useful for testing loading-indicator visibility.
     * Defaults to `0` (no delay).
     */
    var signUpWithEmailDelay: Long = 0L

    // ── Call tracking ─────────────────────────────────────────────────────────

    /**
     * Ordered log of every method that has been invoked on this fake.
     * Entries: `"signInWithGoogle"`, `"signInWithEmail"`, `"registerUserIfNeeded"`.
     *
     * Reset between tests by calling [reset].
     */
    val callLog: MutableList<String> = mutableListOf()

    // ── AuthRepository implementation ─────────────────────────────────────────

    override fun signInWithGoogle(idToken: String): Flow<AuthResult> = flow {
        callLog.add("signInWithGoogle")
        if (signInWithGoogleDelay > 0L) delay(signInWithGoogleDelay)
        emit(signInWithGoogleResult)
    }

    override fun signInWithEmail(email: String, password: String): Flow<AuthResult> = flow {
        callLog.add("signInWithEmail")
        if (signInWithEmailDelay > 0L) delay(signInWithEmailDelay)
        emit(signInWithEmailResult)
    }

    override fun signUpWithEmail(
        email: String,
        password: String
    ): Flow<AuthResult> = flow {
        callLog.add("signUpWithEmail:$email")
        if (signUpWithEmailDelay > 0L) delay(signUpWithEmailDelay)
        emit(signUpWithEmailResult)
    }

    override suspend fun registerUserIfNeeded(userId: String): Boolean {
        callLog.add("registerUserIfNeeded")
        return registerUserResult
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Resets all configurable properties to their defaults and clears [callLog].
     * Call this in `@Before` if the same fake instance is shared across tests.
     */
    fun reset() {
        signInWithGoogleResult = AuthResult.Success(DEFAULT_USER_ID)
        signInWithEmailResult = AuthResult.Success(DEFAULT_USER_ID)
        registerUserResult = true
        signInWithGoogleDelay = 0L
        signInWithEmailDelay = 0L
        callLog.clear()
    }

    private companion object {
        const val DEFAULT_USER_ID = "default_user_id"
    }
}