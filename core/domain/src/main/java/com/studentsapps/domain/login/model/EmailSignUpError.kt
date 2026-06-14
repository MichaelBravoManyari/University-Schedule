package com.studentsapps.domain.login.model

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
enum class EmailSignUpError : AuthValidationError {
    /** The email field was empty or contained only whitespace after trimming. */
    BLANK_EMAIL,

    /** The email field is non-blank but does not match the expected email format. */
    INVALID_EMAIL_FORMAT,

    /** The password field was empty or contained only whitespace after trimming. */
    BLANK_PASSWORD,

    /**
     * The password does not satisfy the security requirements:
     * at least 8 characters, one digit, one lowercase letter, one uppercase letter,
     * one special character (@#$%^&+=!), and no spaces.
     */
    WEAK_PASSWORD,

    /** The confirm-password field does not match the password field. */
    PASSWORDS_DO_NOT_MATCH,
}