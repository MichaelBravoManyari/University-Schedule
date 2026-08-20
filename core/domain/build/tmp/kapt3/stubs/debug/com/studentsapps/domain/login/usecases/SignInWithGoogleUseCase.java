package com.studentsapps.domain.login.usecases;

/**
 * Use case for signing in with Google credentials.
 *
 * This encapsulates the business logic for Google authentication.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\b\u001a\u00020\tH\u0086\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lcom/studentsapps/domain/login/usecases/SignInWithGoogleUseCase;", "", "authRepository", "Lcom/studentsapps/domain/login/repository/AuthRepository;", "(Lcom/studentsapps/domain/login/repository/AuthRepository;)V", "invoke", "Lkotlinx/coroutines/flow/Flow;", "Lcom/studentsapps/domain/login/model/AuthResult;", "idToken", "", "domain_debug"})
public final class SignInWithGoogleUseCase {
    @org.jetbrains.annotations.NotNull()
    private final com.studentsapps.domain.login.repository.AuthRepository authRepository = null;
    
    @javax.inject.Inject()
    public SignInWithGoogleUseCase(@org.jetbrains.annotations.NotNull()
    com.studentsapps.domain.login.repository.AuthRepository authRepository) {
        super();
    }
    
    /**
     * Execute the Google sign in operation.
     *
     * @param idToken Google ID token obtained from credential manager
     * @return Flow emitting the authentication result
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.studentsapps.domain.login.model.AuthResult> invoke(@org.jetbrains.annotations.NotNull()
    java.lang.String idToken) {
        return null;
    }
}