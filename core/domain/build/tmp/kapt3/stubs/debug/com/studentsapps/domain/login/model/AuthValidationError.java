package com.studentsapps.domain.login.model;

/**
 * Sealed interface that marks all input-validation error types returned by
 * authentication use cases.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\bv\u0018\u00002\u00020\u0001\u0082\u0001\u0002\u0002\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/studentsapps/domain/login/model/AuthValidationError;", "", "Lcom/studentsapps/domain/login/model/EmailSignInError;", "Lcom/studentsapps/domain/login/model/EmailSignUpError;", "domain_debug"})
public abstract interface AuthValidationError {
}