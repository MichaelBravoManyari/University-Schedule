package com.studentsapps.domain.login.usecases;

/**
 * Use case for creating a new account with email and password.
 *
 * Centralizes all sign-up validation rules so neither the ViewModel nor the
 * repository need to know about them:
 *  - Email and password are trimmed before any check.
 *  - Email must not be blank and must match the RFC-5322-compatible regex.
 *  - Password must not be blank and must satisfy the security requirements.
 *  - Confirm-password must match the password exactly.
 *
 * Returns [AuthResult.ValidationFailure] for invalid inputs, keeping the
 * call-site free of try/catch and consistent with the rest of the auth flows.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u000e2\u00020\u0001:\u0001\u000eB\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\'\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\tH\u0086\u0002J\"\u0010\f\u001a\u0004\u0018\u00010\r2\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\tH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/studentsapps/domain/login/usecases/SignUpWithEmailUseCase;", "", "authRepository", "Lcom/studentsapps/domain/login/repository/AuthRepository;", "(Lcom/studentsapps/domain/login/repository/AuthRepository;)V", "invoke", "Lkotlinx/coroutines/flow/Flow;", "Lcom/studentsapps/domain/login/model/AuthResult;", "email", "", "password", "confirmPassword", "validate", "Lcom/studentsapps/domain/login/model/EmailSignUpError;", "Companion", "domain_debug"})
public final class SignUpWithEmailUseCase {
    @org.jetbrains.annotations.NotNull()
    private final com.studentsapps.domain.login.repository.AuthRepository authRepository = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex EMAIL_REGEX = null;
    
    /**
     * Password security rules (matches the original Fragment regex):
     * - At least one digit            (?=.*[0-9])
     * - At least one lowercase letter (?=.*[a-z])
     * - At least one uppercase letter (?=.*[A-Z])
     * - At least one special char     (?=.*[@#$%^&+=!])
     * - No whitespace                 (?=\S+$)
     * - Minimum 8 characters          .{8,}
     */
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex PASSWORD_REGEX = null;
    @org.jetbrains.annotations.NotNull()
    private static final com.studentsapps.domain.login.usecases.SignUpWithEmailUseCase.Companion Companion = null;
    
    @javax.inject.Inject()
    public SignUpWithEmailUseCase(@org.jetbrains.annotations.NotNull()
    com.studentsapps.domain.login.repository.AuthRepository authRepository) {
        super();
    }
    
    /**
     * Execute the email sign-up operation.
     *
     * Email is trimmed internally; callers do not need to sanitize it first.
     * Passwords are **not** trimmed — a trailing space in a password is intentional.
     *
     * @param email           Raw email string from the UI.
     * @param password        Raw password string from the UI.
     * @param confirmPassword Repeated password entered by the user.
     * @return Flow emitting an [AuthResult]:
     *  - [AuthResult.ValidationFailure] when inputs do not pass the rules.
     *  - [AuthResult.Success] / [AuthResult.Failure] from the repository otherwise.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.studentsapps.domain.login.model.AuthResult> invoke(@org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    java.lang.String confirmPassword) {
        return null;
    }
    
    /**
     * Validates inputs against all sign-up business rules.
     *
     * Rules (evaluated in order — first failure short-circuits):
     * 1. Email must not be blank.
     * 2. Email must match [EMAIL_REGEX].
     * 3. Password must not be blank.
     * 4. Password must satisfy [PASSWORD_REGEX] (length, complexity, no spaces).
     * 5. Confirm-password must equal the password.
     *
     * @return The first [EmailSignUpError] found, or null if all rules pass.
     */
    private final com.studentsapps.domain.login.model.EmailSignUpError validate(java.lang.String email, java.lang.String password, java.lang.String confirmPassword) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u0011\u0010\u0007\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0006\u00a8\u0006\t"}, d2 = {"Lcom/studentsapps/domain/login/usecases/SignUpWithEmailUseCase$Companion;", "", "()V", "EMAIL_REGEX", "Lkotlin/text/Regex;", "getEMAIL_REGEX", "()Lkotlin/text/Regex;", "PASSWORD_REGEX", "getPASSWORD_REGEX", "domain_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final kotlin.text.Regex getEMAIL_REGEX() {
            return null;
        }
        
        /**
         * Password security rules (matches the original Fragment regex):
         * - At least one digit            (?=.*[0-9])
         * - At least one lowercase letter (?=.*[a-z])
         * - At least one uppercase letter (?=.*[A-Z])
         * - At least one special char     (?=.*[@#$%^&+=!])
         * - No whitespace                 (?=\S+$)
         * - Minimum 8 characters          .{8,}
         */
        @org.jetbrains.annotations.NotNull()
        public final kotlin.text.Regex getPASSWORD_REGEX() {
            return null;
        }
    }
}