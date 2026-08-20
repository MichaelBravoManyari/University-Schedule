package com.studentsapps.domain.login.repository;

/**
 * Repository interface for authentication operations.
 * This defines the contract for authentication data sources.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\bf\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u0006J\u001e\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u0005H&J\u0016\u0010\f\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\r\u001a\u00020\u0005H&J\u001e\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u0005H&\u00a8\u0006\u000f"}, d2 = {"Lcom/studentsapps/domain/login/repository/AuthRepository;", "", "registerUserIfNeeded", "", "userId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "signInWithEmail", "Lkotlinx/coroutines/flow/Flow;", "Lcom/studentsapps/domain/login/model/AuthResult;", "email", "password", "signInWithGoogle", "idToken", "signUpWithEmail", "domain_debug"})
public abstract interface AuthRepository {
    
    /**
     * Sign in with Google credentials.
     *
     * @param idToken Google ID token obtained from the credential manager
     * @return Flow emitting the authentication result
     */
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.studentsapps.domain.login.model.AuthResult> signInWithGoogle(@org.jetbrains.annotations.NotNull()
    java.lang.String idToken);
    
    /**
     * Sign in with email and password credentials.
     *
     * @param email User's email address
     * @param password User's password
     * @return Flow emitting the authentication result
     */
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.studentsapps.domain.login.model.AuthResult> signInWithEmail(@org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String password);
    
    /**
     * Create a new Firebase Auth account with email and password.
     *
     * @param email    User's email address (pre-validated and trimmed by the use case).
     * @param password User's password (pre-validated by the use case).
     * @return Flow emitting the authentication result.
     */
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.studentsapps.domain.login.model.AuthResult> signUpWithEmail(@org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String password);
    
    /**
     * Check if a user exists in Firestore and register if necessary.
     *
     * @param userId The user ID to check and register
     * @return true if the operation was successful, false otherwise
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object registerUserIfNeeded(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion);
}