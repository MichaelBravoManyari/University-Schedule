package com.studentsapps.domain

import com.studentsapps.domain.login.model.AuthError
import com.studentsapps.domain.login.model.AuthResult
import com.studentsapps.domain.login.model.EmailSignUpError
import com.studentsapps.domain.login.repository.fake.FakeAuthRepository
import com.studentsapps.domain.login.usecases.SignUpWithEmailUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for [com.studentsapps.domain.login.usecases.SignUpWithEmailUseCase].
 */
class SignUpWithEmailUseCaseTest {

    // ── System under test ─────────────────────────────────────────────────────

    private lateinit var signUpWithEmailUseCase: SignUpWithEmailUseCase

    // ── Fake dependencies ─────────────────────────────────────────────────────

    private val fakeAuthRepository = FakeAuthRepository()

    @Before
    fun setup() {
        fakeAuthRepository.reset()
        signUpWithEmailUseCase = SignUpWithEmailUseCase(fakeAuthRepository)
    }

    // ========== Happy Path ==========

    @Test
    fun givenValidInputs_whenInvoked_thenDelegatesToRepositoryAndReturnsSuccess() = runTest {
        // Given
        val email = "user@example.com"
        val password = "Secure@123"
        val confirmPassword = "Secure@123"
        fakeAuthRepository.signUpWithEmailResult = AuthResult.Success("uid_001")

        // When
        val result = signUpWithEmailUseCase(email, password, confirmPassword).first()

        // Then
        assertTrue(result is AuthResult.Success)
        assertEquals("uid_001", result.userId)
    }

    @Test
    fun givenValidInputs_whenInvoked_thenPropagatesRepositoryFailure() = runTest {
        // Given
        val email = "user@example.com"
        val password = "Secure@123"
        val confirmPassword = "Secure@123"
        fakeAuthRepository.signUpWithEmailResult =
            AuthResult.Failure(AuthError.EMAIL_ALREADY_IN_USE)

        // When
        val result = signUpWithEmailUseCase(email, password, confirmPassword).first()

        // Then
        assertTrue(result is AuthResult.Failure)
        assertEquals(
            AuthError.EMAIL_ALREADY_IN_USE,
            result.error
        )
    }

    // ========== Email Trimming ==========

    @Test
    fun givenEmailWithLeadingAndTrailingSpaces_whenInvoked_thenTrimmedEmailIsForwardedToRepository() =
        runTest {
            // Given
            val emailWithSpaces = "  user@example.com  "
            val trimmedEmail = "user@example.com"
            val password = "Secure@123"
            fakeAuthRepository.signUpWithEmailResult = AuthResult.Success("uid_trim")

            // When
            signUpWithEmailUseCase(emailWithSpaces, password, password).first()

            // Then: el repositorio recibe el email sin espacios
            val signUpCall = fakeAuthRepository.callLog.first { it.startsWith("signUpWithEmail:") }
            assertEquals(
                "signUpWithEmail:$trimmedEmail",
                signUpCall,
                "The email forwarded to the repository must be trimmed"
            )
        }

    @Test
    fun givenEmailWithSpacesThatMakeItBlank_whenInvoked_thenReturnsBlankEmailError() = runTest {
        // Given: espacios solos pasan el trim y quedan vacíos → BLANK_EMAIL
        val email = "   "
        val password = "Secure@123"

        // When
        val result = signUpWithEmailUseCase(email, password, password).first()

        // Then
        assertTrue(result is AuthResult.ValidationFailure)
        assertEquals(
            EmailSignUpError.BLANK_EMAIL,
            result.error
        )
    }

    @Test
    fun givenPasswordWithTrailingSpace_whenInvoked_thenPasswordIsNotTrimmed() = runTest {
        // Given: la contraseña con espacio final NO se recorta; ambas contraseñas
        // tienen el mismo espacio, por lo que pasan la validación de coincidencia.
        // Sin embargo, no pasan PASSWORD_REGEX (no whitespace rule) → WEAK_PASSWORD,
        // lo que confirma que el espacio sigue presente y no fue eliminado.
        val email = "user@example.com"
        val passwordWithSpace = "Secure@123 "
        val confirmPassword = "Secure@123 "

        // When
        val result = signUpWithEmailUseCase(email, passwordWithSpace, confirmPassword).first()

        // Then: WEAK_PASSWORD confirma que el espacio no fue recortado
        assertTrue(result is AuthResult.ValidationFailure)
        assertEquals(
            EmailSignUpError.WEAK_PASSWORD,
            result.error
        )
    }

    // ========== Email Validation ==========

    @Test
    fun givenEmptyEmail_whenInvoked_thenReturnsBlankEmailError() = runTest {
        // Given
        val email = ""
        val password = "Secure@123"

        // When
        val result = signUpWithEmailUseCase(email, password, password).first()

        // Then
        assertTrue(result is AuthResult.ValidationFailure)
        assertEquals(
            EmailSignUpError.BLANK_EMAIL,
            result.error
        )
    }

    @Test
    fun givenEmailMissingAtSymbol_whenInvoked_thenReturnsInvalidEmailFormatError() = runTest {
        // Given
        val email = "userexample.com"
        val password = "Secure@123"

        // When
        val result = signUpWithEmailUseCase(email, password, password).first()

        // Then
        assertTrue(result is AuthResult.ValidationFailure)
        assertEquals(
            EmailSignUpError.INVALID_EMAIL_FORMAT,
            result.error
        )
    }

    @Test
    fun givenEmailMissingDomain_whenInvoked_thenReturnsInvalidEmailFormatError() = runTest {
        // Given
        val email = "user@"
        val password = "Secure@123"

        // When
        val result = signUpWithEmailUseCase(email, password, password).first()

        // Then
        assertTrue(result is AuthResult.ValidationFailure)
        assertEquals(
            EmailSignUpError.INVALID_EMAIL_FORMAT,
            result.error
        )
    }

    @Test
    fun givenEmailMissingTopLevelDomain_whenInvoked_thenReturnsInvalidEmailFormatError() =
        runTest {
            // Given
            val email = "user@example"
            val password = "Secure@123"

            // When
            val result = signUpWithEmailUseCase(email, password, password).first()

            // Then
            assertTrue(result is AuthResult.ValidationFailure)
            assertEquals(
                EmailSignUpError.INVALID_EMAIL_FORMAT,
                result.error
            )
        }

    // ========== Password Validation ==========

    @Test
    fun givenEmptyPassword_whenInvoked_thenReturnsBlankPasswordError() = runTest {
        // Given
        val email = "user@example.com"
        val password = ""

        // When
        val result = signUpWithEmailUseCase(email, password, password).first()

        // Then
        assertTrue(result is AuthResult.ValidationFailure)
        assertEquals(
            EmailSignUpError.BLANK_PASSWORD,
            result.error
        )
    }

    @Test
    fun givenPasswordTooShort_whenInvoked_thenReturnsWeakPasswordError() = runTest {
        // Given: 7 caracteres — falla la regla de mínimo 8
        val email = "user@example.com"
        val password = "Se@1aaa"

        // When
        val result = signUpWithEmailUseCase(email, password, password).first()

        // Then
        assertTrue(result is AuthResult.ValidationFailure)
        assertEquals(
            EmailSignUpError.WEAK_PASSWORD,
            result.error
        )
    }

    @Test
    fun givenPasswordWithoutDigit_whenInvoked_thenReturnsWeakPasswordError() = runTest {
        // Given: sin dígito — falla (?=.*[0-9])
        val email = "user@example.com"
        val password = "Secure@Abc"

        // When
        val result = signUpWithEmailUseCase(email, password, password).first()

        // Then
        assertTrue(result is AuthResult.ValidationFailure)
        assertEquals(
            EmailSignUpError.WEAK_PASSWORD,
            result.error
        )
    }

    @Test
    fun givenPasswordWithoutUppercaseLetter_whenInvoked_thenReturnsWeakPasswordError() = runTest {
        // Given: sin mayúscula — falla (?=.*[A-Z])
        val email = "user@example.com"
        val password = "secure@123"

        // When
        val result = signUpWithEmailUseCase(email, password, password).first()

        // Then
        assertTrue(result is AuthResult.ValidationFailure)
        assertEquals(
            EmailSignUpError.WEAK_PASSWORD,
            result.error
        )
    }

    @Test
    fun givenPasswordWithoutLowercaseLetter_whenInvoked_thenReturnsWeakPasswordError() = runTest {
        // Given: sin minúscula — falla (?=.*[a-z])
        val email = "user@example.com"
        val password = "SECURE@123"

        // When
        val result = signUpWithEmailUseCase(email, password, password).first()

        // Then
        assertTrue(result is AuthResult.ValidationFailure)
        assertEquals(
            EmailSignUpError.WEAK_PASSWORD,
            result.error
        )
    }

    @Test
    fun givenPasswordWithoutSpecialCharacter_whenInvoked_thenReturnsWeakPasswordError() = runTest {
        // Given: sin carácter especial — falla (?=.*[@#$%^&+=!])
        val email = "user@example.com"
        val password = "Secure1234"

        // When
        val result = signUpWithEmailUseCase(email, password, password).first()

        // Then
        assertTrue(result is AuthResult.ValidationFailure)
        assertEquals(
            EmailSignUpError.WEAK_PASSWORD,
            result.error
        )
    }

    @Test
    fun givenPasswordWithWhitespace_whenInvoked_thenReturnsWeakPasswordError() = runTest {
        // Given: con espacio interno — falla (?=\S+$)
        val email = "user@example.com"
        val password = "Secure @123"

        // When
        val result = signUpWithEmailUseCase(email, password, password).first()

        // Then
        assertTrue(result is AuthResult.ValidationFailure)
        assertEquals(
            EmailSignUpError.WEAK_PASSWORD,
            result.error
        )
    }

    // ========== Confirm Password Validation ==========

    @Test
    fun givenPasswordsDoNotMatch_whenInvoked_thenReturnsPasswordsDoNotMatchError() = runTest {
        // Given
        val email = "user@example.com"
        val password = "Secure@123"
        val confirmPassword = "Secure@456"

        // When
        val result = signUpWithEmailUseCase(email, password, confirmPassword).first()

        // Then
        assertTrue(result is AuthResult.ValidationFailure)
        assertEquals(
            EmailSignUpError.PASSWORDS_DO_NOT_MATCH,
            result.error
        )
    }

    @Test
    fun givenConfirmPasswordWithDifferentCase_whenInvoked_thenReturnsPasswordsDoNotMatchError() =
        runTest {
            // Given: la comparación es case-sensitive
            val email = "user@example.com"
            val password = "Secure@123"
            val confirmPassword = "secure@123"

            // When
            val result = signUpWithEmailUseCase(email, password, confirmPassword).first()

            // Then
            assertTrue(result is AuthResult.ValidationFailure)
            assertEquals(
                EmailSignUpError.PASSWORDS_DO_NOT_MATCH,
                result.error
            )
        }

    // ========== Validation Order ==========

    @Test
    fun givenBlankEmailAndBlankPassword_whenInvoked_thenReturnsBlankEmailBeforeBlankPassword() =
        runTest {
            // Given: tanto email como contraseña están vacíos
            val email = ""
            val password = ""

            // When
            val result = signUpWithEmailUseCase(email, password, password).first()

            // Then: BLANK_EMAIL precede a BLANK_PASSWORD
            assertTrue(result is AuthResult.ValidationFailure)
            assertEquals(
                EmailSignUpError.BLANK_EMAIL,
                result.error
            )
        }

    @Test
    fun givenInvalidEmailFormatAndBlankPassword_whenInvoked_thenReturnsInvalidEmailFormatBeforeBlankPassword() =
        runTest {
            // Given: email mal formado y contraseña vacía
            val email = "not-an-email"
            val password = ""

            // When
            val result = signUpWithEmailUseCase(email, password, password).first()

            // Then: INVALID_EMAIL_FORMAT precede a BLANK_PASSWORD
            assertTrue(result is AuthResult.ValidationFailure)
            assertEquals(
                EmailSignUpError.INVALID_EMAIL_FORMAT,
                result.error
            )
        }

    @Test
    fun givenBlankPasswordAndPasswordsMismatch_whenInvoked_thenReturnsBlankPasswordBeforeMismatch() =
        runTest {
            // Given: contraseña vacía y confirmación diferente
            val email = "user@example.com"
            val password = ""
            val confirmPassword = "Different@1"

            // When
            val result = signUpWithEmailUseCase(email, password, confirmPassword).first()

            // Then: BLANK_PASSWORD precede a PASSWORDS_DO_NOT_MATCH
            assertTrue(result is AuthResult.ValidationFailure)
            assertEquals(
                EmailSignUpError.BLANK_PASSWORD,
                result.error
            )
        }

    @Test
    fun givenWeakPasswordAndPasswordsMismatch_whenInvoked_thenReturnsWeakPasswordBeforeMismatch() =
        runTest {
            // Given: contraseña débil y confirmación diferente
            val email = "user@example.com"
            val password = "weak"
            val confirmPassword = "Different@1"

            // When
            val result = signUpWithEmailUseCase(email, password, confirmPassword).first()

            // Then: WEAK_PASSWORD precede a PASSWORDS_DO_NOT_MATCH
            assertTrue(result is AuthResult.ValidationFailure)
            assertEquals(
                EmailSignUpError.WEAK_PASSWORD,
                result.error
            )
        }

    // ========== Repository Not Called On Validation Failure ==========

    @Test
    fun givenBlankEmail_whenInvoked_thenRepositoryIsNeverCalled() = runTest {
        // Given
        val email = ""
        val password = "Secure@123"

        // When
        signUpWithEmailUseCase(email, password, password).first()

        // Then
        assertTrue(
            fakeAuthRepository.callLog.none { it.startsWith("signUpWithEmail") },
            "Repository must not be called when email validation fails"
        )
    }

    @Test
    fun givenInvalidEmailFormat_whenInvoked_thenRepositoryIsNeverCalled() = runTest {
        // Given
        val email = "not-an-email"
        val password = "Secure@123"

        // When
        signUpWithEmailUseCase(email, password, password).first()

        // Then
        assertTrue(
            fakeAuthRepository.callLog.none { it.startsWith("signUpWithEmail") },
            "Repository must not be called when email format is invalid"
        )
    }

    @Test
    fun givenBlankPassword_whenInvoked_thenRepositoryIsNeverCalled() = runTest {
        // Given
        val email = "user@example.com"
        val password = ""

        // When
        signUpWithEmailUseCase(email, password, password).first()

        // Then
        assertTrue(
            fakeAuthRepository.callLog.none { it.startsWith("signUpWithEmail") },
            "Repository must not be called when password is blank"
        )
    }

    @Test
    fun givenWeakPassword_whenInvoked_thenRepositoryIsNeverCalled() = runTest {
        // Given
        val email = "user@example.com"
        val password = "weak"

        // When
        signUpWithEmailUseCase(email, password, password).first()

        // Then
        assertTrue(
            fakeAuthRepository.callLog.none { it.startsWith("signUpWithEmail") },
            "Repository must not be called when password is weak"
        )
    }

    @Test
    fun givenPasswordsDoNotMatch_whenInvoked_thenRepositoryIsNeverCalled() = runTest {
        // Given
        val email = "user@example.com"
        val password = "Secure@123"
        val confirmPassword = "Secure@456"

        // When
        signUpWithEmailUseCase(email, password, confirmPassword).first()

        // Then
        assertTrue(
            fakeAuthRepository.callLog.none { it.startsWith("signUpWithEmail") },
            "Repository must not be called when passwords do not match"
        )
    }
}