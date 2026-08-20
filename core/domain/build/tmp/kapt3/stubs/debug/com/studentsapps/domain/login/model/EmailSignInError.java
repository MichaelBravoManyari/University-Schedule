package com.studentsapps.domain.login.model;

/**
 * Represents input-validation errors specific to the email sign-in flow.
 *
 * These are distinct from [AuthError] (which describes network / Firebase
 * failures) because validation errors are caught before any network call is
 * made and map to per-field UI feedback rather than a global error message.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u00012\u00020\u0002B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/studentsapps/domain/login/model/EmailSignInError;", "", "Lcom/studentsapps/domain/login/model/AuthValidationError;", "(Ljava/lang/String;I)V", "BLANK_EMAIL", "INVALID_EMAIL_FORMAT", "BLANK_PASSWORD", "domain_debug"})
public enum EmailSignInError implements com.studentsapps.domain.login.model.AuthValidationError {
    /*public static final*/ BLANK_EMAIL /* = new BLANK_EMAIL() */,
    /*public static final*/ INVALID_EMAIL_FORMAT /* = new INVALID_EMAIL_FORMAT() */,
    /*public static final*/ BLANK_PASSWORD /* = new BLANK_PASSWORD() */;
    
    EmailSignInError() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.studentsapps.domain.login.model.EmailSignInError> getEntries() {
        return null;
    }
}