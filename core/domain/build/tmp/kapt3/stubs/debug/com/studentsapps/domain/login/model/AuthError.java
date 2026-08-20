package com.studentsapps.domain.login.model;

/**
 * Enum representing possible authentication errors.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\t\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\t\u00a8\u0006\n"}, d2 = {"Lcom/studentsapps/domain/login/model/AuthError;", "", "(Ljava/lang/String;I)V", "NETWORK_ERROR", "INVALID_CREDENTIALS", "USER_NOT_FOUND", "WEAK_PASSWORD", "EMAIL_ALREADY_IN_USE", "CANCELLED_BY_USER", "UNKNOWN_ERROR", "domain_debug"})
public enum AuthError {
    /*public static final*/ NETWORK_ERROR /* = new NETWORK_ERROR() */,
    /*public static final*/ INVALID_CREDENTIALS /* = new INVALID_CREDENTIALS() */,
    /*public static final*/ USER_NOT_FOUND /* = new USER_NOT_FOUND() */,
    /*public static final*/ WEAK_PASSWORD /* = new WEAK_PASSWORD() */,
    /*public static final*/ EMAIL_ALREADY_IN_USE /* = new EMAIL_ALREADY_IN_USE() */,
    /*public static final*/ CANCELLED_BY_USER /* = new CANCELLED_BY_USER() */,
    /*public static final*/ UNKNOWN_ERROR /* = new UNKNOWN_ERROR() */;
    
    AuthError() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.studentsapps.domain.login.model.AuthError> getEntries() {
        return null;
    }
}