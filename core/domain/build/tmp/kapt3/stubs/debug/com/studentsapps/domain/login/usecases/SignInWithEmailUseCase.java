package com.studentsapps.domain.login.usecases;

/**
 * Use case for signing in with email and password credentials.
 *
 * Validates inputs before delegating to the repository, keeping
 * business rules out of the ViewModel and data layers.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \r2\u00020\u0001:\u0001\rB\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\tH\u0086\u0002J\u001a\u0010\u000b\u001a\u0004\u0018\u00010\f2\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\tH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/studentsapps/domain/login/usecases/SignInWithEmailUseCase;", "", "authRepository", "Lcom/studentsapps/domain/login/repository/AuthRepository;", "(Lcom/studentsapps/domain/login/repository/AuthRepository;)V", "invoke", "Lkotlinx/coroutines/flow/Flow;", "Lcom/studentsapps/domain/login/model/AuthResult;", "email", "", "password", "validate", "Lcom/studentsapps/domain/login/model/EmailSignInError;", "Companion", "domain_debug"})
public final class SignInWithEmailUseCase {
    @org.jetbrains.annotations.NotNull()
    private final com.studentsapps.domain.login.repository.AuthRepository authRepository = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex EMAIL_REGEX = null;
    @org.jetbrains.annotations.NotNull()
    private static final com.studentsapps.domain.login.usecases.SignInWithEmailUseCase.Companion Companion = null;
    
    @javax.inject.Inject()
    public SignInWithEmailUseCase(@org.jetbrains.annotations.NotNull()
    com.studentsapps.domain.login.repository.AuthRepository authRepository) {
        super();
    }
    
    /**
     * Execute the email sign-in operation.
     *
     * @param email User's email address (must be non-blank)
     * @param password User's password (must be non-blank)
     * @return Flow emitting the authentication result
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.studentsapps.domain.login.model.AuthResult> invoke(@org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String password) {
        return null;
    }
    
    /**
     * Validates trimmed email and password against the business rules.
     *
     * Rules (evaluated in order):
     * 1. Email must not be blank.
     * 2. Email must match [EMAIL_REGEX].
     * 3. Password must not be blank.
     *
     * @return The first [EmailSignInError] found, or null if all rules pass.
     */
    private final com.studentsapps.domain.login.model.EmailSignInError validate(java.lang.String email, java.lang.String password) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/studentsapps/domain/login/usecases/SignInWithEmailUseCase$Companion;", "", "()V", "EMAIL_REGEX", "Lkotlin/text/Regex;", "getEMAIL_REGEX", "()Lkotlin/text/Regex;", "domain_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final kotlin.text.Regex getEMAIL_REGEX() {
            return null;
        }
    }
}