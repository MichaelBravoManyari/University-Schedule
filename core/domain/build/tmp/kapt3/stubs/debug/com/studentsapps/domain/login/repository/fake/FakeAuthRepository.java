package com.studentsapps.domain.login.repository.fake;

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
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0014\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u0000 22\u00020\u0001:\u00012B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010&\u001a\u00020\t2\u0006\u0010\'\u001a\u00020\u0005H\u0096@\u00a2\u0006\u0002\u0010(J\u0006\u0010)\u001a\u00020*J\u001e\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00150,2\u0006\u0010-\u001a\u00020\u00052\u0006\u0010.\u001a\u00020\u0005H\u0016J\u0016\u0010/\u001a\b\u0012\u0004\u0012\u00020\u00150,2\u0006\u00100\u001a\u00020\u0005H\u0016J\u001e\u00101\u001a\b\u0012\u0004\u0012\u00020\u00150,2\u0006\u0010-\u001a\u00020\u00052\u0006\u0010.\u001a\u00020\u0005H\u0016R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u001a\u0010\b\u001a\u00020\tX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\u001a\u0010\u000e\u001a\u00020\u000fX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001a\u0010\u0014\u001a\u00020\u0015X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\u001a\u0010\u001a\u001a\u00020\u000fX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u0011\"\u0004\b\u001c\u0010\u0013R\u001a\u0010\u001d\u001a\u00020\u0015X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u0017\"\u0004\b\u001f\u0010\u0019R\u001a\u0010 \u001a\u00020\u000fX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b!\u0010\u0011\"\u0004\b\"\u0010\u0013R\u001a\u0010#\u001a\u00020\u0015X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b$\u0010\u0017\"\u0004\b%\u0010\u0019\u00a8\u00063"}, d2 = {"Lcom/studentsapps/domain/login/repository/fake/FakeAuthRepository;", "Lcom/studentsapps/domain/login/repository/AuthRepository;", "()V", "callLog", "", "", "getCallLog", "()Ljava/util/List;", "registerUserResult", "", "getRegisterUserResult", "()Z", "setRegisterUserResult", "(Z)V", "signInWithEmailDelay", "", "getSignInWithEmailDelay", "()J", "setSignInWithEmailDelay", "(J)V", "signInWithEmailResult", "Lcom/studentsapps/domain/login/model/AuthResult;", "getSignInWithEmailResult", "()Lcom/studentsapps/domain/login/model/AuthResult;", "setSignInWithEmailResult", "(Lcom/studentsapps/domain/login/model/AuthResult;)V", "signInWithGoogleDelay", "getSignInWithGoogleDelay", "setSignInWithGoogleDelay", "signInWithGoogleResult", "getSignInWithGoogleResult", "setSignInWithGoogleResult", "signUpWithEmailDelay", "getSignUpWithEmailDelay", "setSignUpWithEmailDelay", "signUpWithEmailResult", "getSignUpWithEmailResult", "setSignUpWithEmailResult", "registerUserIfNeeded", "userId", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "reset", "", "signInWithEmail", "Lkotlinx/coroutines/flow/Flow;", "email", "password", "signInWithGoogle", "idToken", "signUpWithEmail", "Companion", "domain_debug"})
public final class FakeAuthRepository implements com.studentsapps.domain.login.repository.AuthRepository {
    
    /**
     * Result emitted by [signInWithGoogle].
     * Defaults to a generic success so tests that don't care about the value
     * work without extra configuration.
     */
    @org.jetbrains.annotations.NotNull()
    private com.studentsapps.domain.login.model.AuthResult signInWithGoogleResult;
    
    /**
     * Result emitted by [signInWithEmail].
     * Defaults to a generic success.
     */
    @org.jetbrains.annotations.NotNull()
    private com.studentsapps.domain.login.model.AuthResult signInWithEmailResult;
    
    /**
     * Result emitted by [signUpWithEmail].
     * Defaults to a generic success.
     */
    @org.jetbrains.annotations.NotNull()
    private com.studentsapps.domain.login.model.AuthResult signUpWithEmailResult;
    
    /**
     * Value returned by [registerUserIfNeeded].
     * Defaults to `true` (registration succeeds).
     */
    private boolean registerUserResult = true;
    
    /**
     * Optional artificial delay (in milliseconds) applied before [signInWithGoogle]
     * emits its result. Use this to keep isLoading visible long enough to be
     * asserted in tests that verify the loading state.
     * Set to `0` (default) for no delay.
     */
    private long signInWithGoogleDelay = 0L;
    
    /**
     * Optional artificial delay (in milliseconds) applied before [signInWithEmail]
     * emits its result. Use this to keep isLoading visible long enough to be
     * asserted in tests that verify the loading state.
     * Set to `0` (default) for no delay.
     */
    private long signInWithEmailDelay = 0L;
    
    /**
     * Optional artificial delay (in milliseconds) applied before [signUpWithEmail]
     * emits its result. Useful for testing loading-indicator visibility.
     * Defaults to `0` (no delay).
     */
    private long signUpWithEmailDelay = 0L;
    
    /**
     * Ordered log of every method that has been invoked on this fake.
     * Entries: `"signInWithGoogle"`, `"signInWithEmail"`, `"registerUserIfNeeded"`.
     *
     * Reset between tests by calling [reset].
     */
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> callLog = null;
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String DEFAULT_USER_ID = "default_user_id";
    @org.jetbrains.annotations.NotNull()
    private static final com.studentsapps.domain.login.repository.fake.FakeAuthRepository.Companion Companion = null;
    
    public FakeAuthRepository() {
        super();
    }
    
    /**
     * Result emitted by [signInWithGoogle].
     * Defaults to a generic success so tests that don't care about the value
     * work without extra configuration.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.studentsapps.domain.login.model.AuthResult getSignInWithGoogleResult() {
        return null;
    }
    
    /**
     * Result emitted by [signInWithGoogle].
     * Defaults to a generic success so tests that don't care about the value
     * work without extra configuration.
     */
    public final void setSignInWithGoogleResult(@org.jetbrains.annotations.NotNull()
    com.studentsapps.domain.login.model.AuthResult p0) {
    }
    
    /**
     * Result emitted by [signInWithEmail].
     * Defaults to a generic success.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.studentsapps.domain.login.model.AuthResult getSignInWithEmailResult() {
        return null;
    }
    
    /**
     * Result emitted by [signInWithEmail].
     * Defaults to a generic success.
     */
    public final void setSignInWithEmailResult(@org.jetbrains.annotations.NotNull()
    com.studentsapps.domain.login.model.AuthResult p0) {
    }
    
    /**
     * Result emitted by [signUpWithEmail].
     * Defaults to a generic success.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.studentsapps.domain.login.model.AuthResult getSignUpWithEmailResult() {
        return null;
    }
    
    /**
     * Result emitted by [signUpWithEmail].
     * Defaults to a generic success.
     */
    public final void setSignUpWithEmailResult(@org.jetbrains.annotations.NotNull()
    com.studentsapps.domain.login.model.AuthResult p0) {
    }
    
    /**
     * Value returned by [registerUserIfNeeded].
     * Defaults to `true` (registration succeeds).
     */
    public final boolean getRegisterUserResult() {
        return false;
    }
    
    /**
     * Value returned by [registerUserIfNeeded].
     * Defaults to `true` (registration succeeds).
     */
    public final void setRegisterUserResult(boolean p0) {
    }
    
    /**
     * Optional artificial delay (in milliseconds) applied before [signInWithGoogle]
     * emits its result. Use this to keep isLoading visible long enough to be
     * asserted in tests that verify the loading state.
     * Set to `0` (default) for no delay.
     */
    public final long getSignInWithGoogleDelay() {
        return 0L;
    }
    
    /**
     * Optional artificial delay (in milliseconds) applied before [signInWithGoogle]
     * emits its result. Use this to keep isLoading visible long enough to be
     * asserted in tests that verify the loading state.
     * Set to `0` (default) for no delay.
     */
    public final void setSignInWithGoogleDelay(long p0) {
    }
    
    /**
     * Optional artificial delay (in milliseconds) applied before [signInWithEmail]
     * emits its result. Use this to keep isLoading visible long enough to be
     * asserted in tests that verify the loading state.
     * Set to `0` (default) for no delay.
     */
    public final long getSignInWithEmailDelay() {
        return 0L;
    }
    
    /**
     * Optional artificial delay (in milliseconds) applied before [signInWithEmail]
     * emits its result. Use this to keep isLoading visible long enough to be
     * asserted in tests that verify the loading state.
     * Set to `0` (default) for no delay.
     */
    public final void setSignInWithEmailDelay(long p0) {
    }
    
    /**
     * Optional artificial delay (in milliseconds) applied before [signUpWithEmail]
     * emits its result. Useful for testing loading-indicator visibility.
     * Defaults to `0` (no delay).
     */
    public final long getSignUpWithEmailDelay() {
        return 0L;
    }
    
    /**
     * Optional artificial delay (in milliseconds) applied before [signUpWithEmail]
     * emits its result. Useful for testing loading-indicator visibility.
     * Defaults to `0` (no delay).
     */
    public final void setSignUpWithEmailDelay(long p0) {
    }
    
    /**
     * Ordered log of every method that has been invoked on this fake.
     * Entries: `"signInWithGoogle"`, `"signInWithEmail"`, `"registerUserIfNeeded"`.
     *
     * Reset between tests by calling [reset].
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getCallLog() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<com.studentsapps.domain.login.model.AuthResult> signInWithGoogle(@org.jetbrains.annotations.NotNull()
    java.lang.String idToken) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<com.studentsapps.domain.login.model.AuthResult> signInWithEmail(@org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String password) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<com.studentsapps.domain.login.model.AuthResult> signUpWithEmail(@org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String password) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object registerUserIfNeeded(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    /**
     * Resets all configurable properties to their defaults and clears [callLog].
     * Call this in `@Before` if the same fake instance is shared across tests.
     */
    public final void reset() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/studentsapps/domain/login/repository/fake/FakeAuthRepository$Companion;", "", "()V", "DEFAULT_USER_ID", "", "domain_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
}