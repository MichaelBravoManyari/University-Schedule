package com.studentsapps.domain.login.model;

/**
 * Represents input-validation errors specific to the email sign-up flow.
 *
 * Evaluated in order by [com.studentsapps.domain.login.usecases.SignUpWithEmailUseCase]
 * before any Firebase call is made:
 * 1. [BLANK_EMAIL]
 * 2. [INVALID_EMAIL_FORMAT]
 * 3. [BLANK_PASSWORD]
 * 4. [WEAK_PASSWORD]
 * 5. [PASSWORDS_DO_NOT_MATCH]
 *
 * Implements [AuthValidationError] so it can be carried inside the shared
 * [AuthResult.ValidationFailure] result type.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u00012\u00020\u0002B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\b\u00a8\u0006\t"}, d2 = {"Lcom/studentsapps/domain/login/model/EmailSignUpError;", "", "Lcom/studentsapps/domain/login/model/AuthValidationError;", "(Ljava/lang/String;I)V", "BLANK_EMAIL", "INVALID_EMAIL_FORMAT", "BLANK_PASSWORD", "WEAK_PASSWORD", "PASSWORDS_DO_NOT_MATCH", "domain_debug"})
public enum EmailSignUpError implements com.studentsapps.domain.login.model.AuthValidationError {
    /*public static final*/ BLANK_EMAIL /* = new BLANK_EMAIL() */,
    /*public static final*/ INVALID_EMAIL_FORMAT /* = new INVALID_EMAIL_FORMAT() */,
    /*public static final*/ BLANK_PASSWORD /* = new BLANK_PASSWORD() */,
    /*public static final*/ WEAK_PASSWORD /* = new WEAK_PASSWORD() */,
    /*public static final*/ PASSWORDS_DO_NOT_MATCH /* = new PASSWORDS_DO_NOT_MATCH() */;
    
    EmailSignUpError() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.studentsapps.domain.login.model.EmailSignUpError> getEntries() {
        return null;
    }
}