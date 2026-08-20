package com.studentsapps.domain.login.model;

/**
 * Sealed class representing the result of an authentication operation.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0003\u0003\u0004\u0005B\u0007\b\u0004\u00a2\u0006\u0002\u0010\u0002\u0082\u0001\u0003\u0006\u0007\b\u00a8\u0006\t"}, d2 = {"Lcom/studentsapps/domain/login/model/AuthResult;", "", "()V", "Failure", "Success", "ValidationFailure", "Lcom/studentsapps/domain/login/model/AuthResult$Failure;", "Lcom/studentsapps/domain/login/model/AuthResult$Success;", "Lcom/studentsapps/domain/login/model/AuthResult$ValidationFailure;", "domain_debug"})
public abstract class AuthResult {
    
    private AuthResult() {
        super();
    }
    
    /**
     * Authentication failed.
     * @param error The error that occurred during authentication.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0011"}, d2 = {"Lcom/studentsapps/domain/login/model/AuthResult$Failure;", "Lcom/studentsapps/domain/login/model/AuthResult;", "error", "Lcom/studentsapps/domain/login/model/AuthError;", "(Lcom/studentsapps/domain/login/model/AuthError;)V", "getError", "()Lcom/studentsapps/domain/login/model/AuthError;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "domain_debug"})
    public static final class Failure extends com.studentsapps.domain.login.model.AuthResult {
        @org.jetbrains.annotations.NotNull()
        private final com.studentsapps.domain.login.model.AuthError error = null;
        
        public Failure(@org.jetbrains.annotations.NotNull()
        com.studentsapps.domain.login.model.AuthError error) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.studentsapps.domain.login.model.AuthError getError() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.studentsapps.domain.login.model.AuthError component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.studentsapps.domain.login.model.AuthResult.Failure copy(@org.jetbrains.annotations.NotNull()
        com.studentsapps.domain.login.model.AuthError error) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
    
    /**
     * Authentication was successful.
     * @param userId The unique identifier of the authenticated user.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0010"}, d2 = {"Lcom/studentsapps/domain/login/model/AuthResult$Success;", "Lcom/studentsapps/domain/login/model/AuthResult;", "userId", "", "(Ljava/lang/String;)V", "getUserId", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "domain_debug"})
    public static final class Success extends com.studentsapps.domain.login.model.AuthResult {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String userId = null;
        
        public Success(@org.jetbrains.annotations.NotNull()
        java.lang.String userId) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getUserId() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.studentsapps.domain.login.model.AuthResult.Success copy(@org.jetbrains.annotations.NotNull()
        java.lang.String userId) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
    
    /**
     * Authentication was not attempted because one or more input fields
     * failed validation.
     *
     * Emitted by use cases before any network call is made, so the ViewModel
     * can forward per-field errors to the UI without touching [AuthError] or
     * network-error strings.
     *
     * @param error The specific validation rule that was violated.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0011"}, d2 = {"Lcom/studentsapps/domain/login/model/AuthResult$ValidationFailure;", "Lcom/studentsapps/domain/login/model/AuthResult;", "error", "Lcom/studentsapps/domain/login/model/AuthValidationError;", "(Lcom/studentsapps/domain/login/model/AuthValidationError;)V", "getError", "()Lcom/studentsapps/domain/login/model/AuthValidationError;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "domain_debug"})
    public static final class ValidationFailure extends com.studentsapps.domain.login.model.AuthResult {
        @org.jetbrains.annotations.NotNull()
        private final com.studentsapps.domain.login.model.AuthValidationError error = null;
        
        public ValidationFailure(@org.jetbrains.annotations.NotNull()
        com.studentsapps.domain.login.model.AuthValidationError error) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.studentsapps.domain.login.model.AuthValidationError getError() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.studentsapps.domain.login.model.AuthValidationError component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.studentsapps.domain.login.model.AuthResult.ValidationFailure copy(@org.jetbrains.annotations.NotNull()
        com.studentsapps.domain.login.model.AuthValidationError error) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
}