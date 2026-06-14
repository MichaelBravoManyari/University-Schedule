package com.studentsapps.domain.login.model

/**
 * Represents input-validation errors specific to the email sign-in flow.
 *
 * These are distinct from [AuthError] (which describes network / Firebase
 * failures) because validation errors are caught before any network call is
 * made and map to per-field UI feedback rather than a global error message.
 */
enum class EmailSignInError: AuthValidationError {
    /** The email field was empty or contained only whitespace after trimming. */
    BLANK_EMAIL,

    /** The email field is non-blank but does not match the expected email format. */
    INVALID_EMAIL_FORMAT,

    /** The password field was empty or contained only whitespace after trimming. */
    BLANK_PASSWORD,
}