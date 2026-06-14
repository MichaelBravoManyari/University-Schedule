package com.mbm.login.email_signup

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.mbm.login.AppLogoSection
import com.mbm.login.AuthDimens
import com.mbm.login.BackButton
import com.mbm.login.EmailField
import com.mbm.login.LoadingOverlay
import com.mbm.login.LoginLink
import com.mbm.login.PasswordField
import com.mbm.login.auth.AuthUiError
import com.studentsapps.login.R
import theme.UniversityScheduleTheme

// ================================
// STATEFUL SCREEN
// ================================

/**
 * Adaptive email sign-up screen that adjusts to different screen sizes.
 * Manages UI state and displays loading/error states.
 *
 * @param viewModel The email sign-up ViewModel.
 * @param onNavigateToSchedule Callback invoked once registration succeeds.
 * @param onLoginClick Callback to navigate back to the sign-in screen.
 * @param onBackClick Callback for the "Volver" button.
 * @param modifier Optional modifier.
 */
@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun EmailSignUpScreen(
    viewModel: EmailSignUpViewModel,
    onNavigateToSchedule: () -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.shouldNavigateToSchedule) {
        if (uiState.shouldNavigateToSchedule) {
            onNavigateToSchedule()
            viewModel.onNavigationComplete()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            val message = when (error) {
                is AuthUiError.Message -> context.getString(error.resId)
                is AuthUiError.Text    -> error.message
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.dismissError()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        EmailSignUpScreenContent(
            email = uiState.email,
            password = uiState.password,
            confirmPassword = uiState.confirmPassword,
            isPasswordVisible = uiState.isPasswordVisible,
            isConfirmPasswordVisible = uiState.isConfirmPasswordVisible,
            // Resolve @StringRes Int → String at the Compose layer, not in the ViewModel
            emailError = uiState.emailError?.let { context.getString(it) },
            passwordError = uiState.passwordError?.let { context.getString(it) },
            confirmPasswordError = uiState.confirmPasswordError?.let { context.getString(it) },
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
            onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
            onToggleConfirmPasswordVisibility = viewModel::onToggleConfirmPasswordVisibility,
            onRegisterClick = viewModel::onRegisterClick,
            onLoginClick = onLoginClick,
            onBackClick = onBackClick,
        )

        if (uiState.isLoading) {
            LoadingOverlay()
        }
    }
}

// ================================
// STATELESS SCREEN (previews / testing)
// ================================

/**
 * Stateless version of [EmailSignUpScreen] for previews and testing.
 */
@Composable
fun EmailSignUpScreen(
    email: String,
    password: String,
    confirmPassword: String,
    isPasswordVisible: Boolean,
    isConfirmPasswordVisible: Boolean,
    emailError: String?,
    passwordError: String?,
    confirmPasswordError: String?,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        EmailSignUpScreenContent(
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            isPasswordVisible = isPasswordVisible,
            isConfirmPasswordVisible = isConfirmPasswordVisible,
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange,
            onConfirmPasswordChange = onConfirmPasswordChange,
            onTogglePasswordVisibility = onTogglePasswordVisibility,
            onToggleConfirmPasswordVisibility = onToggleConfirmPasswordVisibility,
            onRegisterClick = onRegisterClick,
            onLoginClick = onLoginClick,
            onBackClick = onBackClick,
        )

        if (isLoading) {
            LoadingOverlay()
        }
    }
}

// ================================
// CONTENT ROUTER
// ================================

/**
 * Routes to the correct layout based on the current [WindowSizeClass].
 * Captures the form state once and distributes it to the chosen layout via lambdas,
 * avoiding repeated parameter drilling.
 */
@Composable
private fun EmailSignUpScreenContent(
    email: String,
    password: String,
    confirmPassword: String,
    isPasswordVisible: Boolean,
    isConfirmPasswordVisible: Boolean,
    emailError: String?,
    passwordError: String?,
    confirmPasswordError: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    // Shared lambda: lets each layout inject its own Modifier (weight, widthIn…)
    // without duplicating the form composable tree.
    val signUpFormCard: @Composable (Modifier) -> Unit = { cardModifier ->
        SignUpFormCard(
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            isPasswordVisible = isPasswordVisible,
            isConfirmPasswordVisible = isConfirmPasswordVisible,
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange,
            onConfirmPasswordChange = onConfirmPasswordChange,
            onTogglePasswordVisibility = onTogglePasswordVisibility,
            onToggleConfirmPasswordVisibility = onToggleConfirmPasswordVisibility,
            onRegisterClick = onRegisterClick,
            onBackClick = onBackClick,
            modifier = cardModifier,
        )
    }

    // Inner lambda for the form content without Card wrapper (used in ExpandedLayout).
    val signUpFormContent: @Composable (Modifier) -> Unit = { contentModifier ->
        SignUpFormContent(
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            isPasswordVisible = isPasswordVisible,
            isConfirmPasswordVisible = isConfirmPasswordVisible,
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange,
            onConfirmPasswordChange = onConfirmPasswordChange,
            onTogglePasswordVisibility = onTogglePasswordVisibility,
            onToggleConfirmPasswordVisibility = onToggleConfirmPasswordVisibility,
            onRegisterClick = onRegisterClick,
            onBackClick = onBackClick,
            modifier = contentModifier,
        )
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.secondary,
    ) {
        when {
            // Small screen in landscape mode (height < 480 dp) — single scrollable column
            !windowSizeClass.isHeightAtLeastBreakpoint(AuthDimens.HEIGHT_COMPACT_THRESHOLD) -> {
                CompactHeightLayout(
                    signUpFormCard = signUpFormCard,
                    onLoginClick = onLoginClick,
                )
            }
            // Large or expanded screens (tablets, desktops) — split-card two-column
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) ||
                    windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_LARGE_LOWER_BOUND) -> {
                ExpandedLayout(
                    signUpFormContent = signUpFormContent,
                    onLoginClick = onLoginClick,
                )
            }
            // Default — phone in portrait, two-column layout
            else -> {
                CompactLayout(
                    signUpFormCard = signUpFormCard,
                    onLoginClick = onLoginClick,
                )
            }
        }
    }
}

// ================================
// LAYOUTS
// ================================

/**
 * Single-column scrollable layout for phones in landscape mode (height < 480 dp).
 *
 * Stack order (top → bottom):
 * 1. App logo (compact size)
 * 2. Screen title ("Crear Cuenta")
 * 3. Screen subtitle ("Únete a Mi Horario Universitario")
 * 4. Sign-up form card
 * 5. Login link footer
 */
@Composable
private fun CompactHeightLayout(
    signUpFormCard: @Composable (Modifier) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .verticalScroll(rememberScrollState())
            .padding(AuthDimens.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            AuthDimens.sectionSpacing,
            Alignment.CenterVertically,
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                COMPACT_SECTION_SPACING,
                Alignment.CenterHorizontally,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Left: sign-up branding column
            SignUpWelcomeColumn(modifier = Modifier.weight(0.9f))

            // Right: form card
            signUpFormCard(Modifier.weight(1.1f))
        }

        // Shared footer — imported from AuthSharedComponents.kt
        LoginLink(onLoginClick = onLoginClick)
    }
}

/**
 * Split-card two-column layout for large / expanded screens (tablets, desktops).
 *
 * The outer [Card] provides rounded corners and elevation; inside:
 * - Left panel  → secondary-color background with branding (logo, title, subtitle).
 * - Right panel → surface-color background with the sign-up form content.
 *
 * [IntrinsicSize.Min] on the inner [Row] ensures both panels share the same height
 * regardless of which side has more content.
 */
@Composable
private fun ExpandedLayout(
    signUpFormContent: @Composable (Modifier) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(AuthDimens.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            AuthDimens.sectionSpacing,
            Alignment.CenterVertically,
        ),
    ) {
        // Split-card container
        Card(
            modifier = Modifier.widthIn(max = MAX_ROW_WIDTH),
            shape = MaterialTheme.shapes.extraLarge,
            elevation = CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION),
            // Secondary bg = left panel color; right panel overrides with surface.
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary,
            ),
        ) {
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {

                // ── Left panel: branding ────────────────────────────────────
                SignUpWelcomeColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(AuthDimens.screenPadding),
                )

                // ── Right panel: form ───────────────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(CARD_PADDING),
                    verticalArrangement = Arrangement.spacedBy(
                        FIELD_SPACING,
                        Alignment.CenterVertically,
                    ),
                ) {
                    signUpFormContent(Modifier.fillMaxWidth())
                }
            }
        }

        // Footer login link below the split card
        // Shared footer — imported from AuthSharedComponents.kt
        LoginLink(onLoginClick = onLoginClick)
    }
}

/**
 * Two-column layout for phones in portrait mode.
 *
 * Left  → [SignUpWelcomeColumn] branding (weight 0.9)
 * Right → [SignUpFormCard] (weight 1.1) — the form gets slightly more room
 *
 * Both columns share the secondary background; the Card provides visual separation.
 */
@Composable
private fun CompactLayout(
    signUpFormCard: @Composable (Modifier) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .verticalScroll(rememberScrollState())
            .padding(AuthDimens.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            AuthDimens.sectionSpacing,
            Alignment.CenterVertically,
        ),
    ) {
        // Shared logo — imported from AuthSharedComponents.kt
        AppLogoSection()

        SignUpTitleText()
        SignUpSubtitleText()

        signUpFormCard(Modifier.fillMaxWidth())

        // Shared footer — imported from AuthSharedComponents.kt
        LoginLink(onLoginClick = onLoginClick)
    }
}

// ================================
// REUSABLE SIGN-UP SECTIONS
// ================================

/**
 * Left-panel branding column specific to the sign-up screen.
 * Shows the app logo, screen title ("Crear Cuenta"), and screen subtitle.
 *
 * This is intentionally separate from [com.mbm.login.AppWelcomeColumn] because the title here
 * is the screen's action title, not the generic app name.
 */
@Composable
private fun SignUpWelcomeColumn(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Shared logo — imported from AuthSharedComponents.kt
        AppLogoSection()
        Spacer(modifier = Modifier.height(AuthDimens.sectionSpacing))
        SignUpTitleText()
        Spacer(modifier = Modifier.height(AuthDimens.smallSpacing))
        SignUpSubtitleText()
    }
}

/**
 * Card wrapping [SignUpFormContent] for [CompactHeightLayout] and [CompactLayout].
 */
@Composable
private fun SignUpFormCard(
    email: String,
    password: String,
    confirmPassword: String,
    isPasswordVisible: Boolean,
    isConfirmPasswordVisible: Boolean,
    emailError: String?,
    passwordError: String?,
    confirmPasswordError: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION),
    ) {
        SignUpFormContent(
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            isPasswordVisible = isPasswordVisible,
            isConfirmPasswordVisible = isConfirmPasswordVisible,
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange,
            onConfirmPasswordChange = onConfirmPasswordChange,
            onTogglePasswordVisibility = onTogglePasswordVisibility,
            onToggleConfirmPasswordVisibility = onToggleConfirmPasswordVisibility,
            onRegisterClick = onRegisterClick,
            onBackClick = onBackClick,
            modifier = Modifier
                .padding(CARD_PADDING)
                .fillMaxWidth(),
        )
    }
}

/**
 * Inner form content: all fields and action buttons.
 * Used directly inside the right panel of [ExpandedLayout] and wrapped
 * by [SignUpFormCard] in the other two layouts.
 */
@Composable
private fun SignUpFormContent(
    email: String,
    password: String,
    confirmPassword: String,
    isPasswordVisible: Boolean,
    isConfirmPasswordVisible: Boolean,
    emailError: String?,
    passwordError: String?,
    confirmPasswordError: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(FIELD_SPACING),
    ) {
        // Shared email field — imported from AuthFormComponents.kt
        EmailField(
            value = email,
            onValueChange = onEmailChange,
            errorMessage = emailError,
        )

        // Shared password field — imported from AuthFormComponents.kt
        // ImeAction.Next advances focus to the confirm-password field automatically.
        PasswordField(
            value = password,
            label = stringResource(R.string.password),
            onValueChange = onPasswordChange,
            isVisible = isPasswordVisible,
            onToggleVisibility = onTogglePasswordVisibility,
            errorMessage = passwordError,
            imeAction = ImeAction.Next,
            visibilityToggleTestTag = "toggle_password_visibility"
        )

        // Shared password field — imported from AuthFormComponents.kt
        // ImeAction.Done triggers registration.
        PasswordField(
            value = confirmPassword,
            label = stringResource(R.string.confirm_password),
            onValueChange = onConfirmPasswordChange,
            isVisible = isConfirmPasswordVisible,
            onToggleVisibility = onToggleConfirmPasswordVisibility,
            errorMessage = confirmPasswordError,
            imeAction = ImeAction.Done,
            onImeAction = onRegisterClick,
            visibilityToggleTestTag = "toggle_confirm_password_visibility"
        )

        Spacer(modifier = Modifier.height(AuthDimens.smallSpacing))

        RegisterButton(onClick = onRegisterClick)

        BackButton(onClick = onBackClick)
    }
}

// ================================
// ATOMIC COMPOSABLES
// ================================

/**
 * Screen title "Crear Cuenta".
 */
@Composable
private fun SignUpTitleText(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.create_account),
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        modifier = modifier,
    )
}

/**
 * Screen subtitle "Únete a Mi Horario Universitario".
 */
@Composable
private fun SignUpSubtitleText(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.join_university_schedule),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.fillMaxWidth(),
    )
}

/**
 * Primary "Registrarse" action button.
 */
@Composable
private fun RegisterButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(AuthDimens.buttonHeight),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Text(
            text = stringResource(R.string.register),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

// ================================
// SCREEN-SPECIFIC CONSTANTS
// ================================

private val CARD_PADDING            = 24.dp
private val CARD_ELEVATION          = 4.dp
private val FIELD_SPACING           = 4.dp
private val COMPACT_SECTION_SPACING = 16.dp
private val MAX_ROW_WIDTH           = 960.dp

// ================================
// PREVIEWS
// ================================

@PreviewScreenSizes
@Composable
private fun EmailSignUpScreenPreview() {
    UniversityScheduleTheme {
        EmailSignUpScreen(
            email = "",
            password = "",
            confirmPassword = "",
            isPasswordVisible = false,
            isConfirmPasswordVisible = false,
            emailError = null,
            passwordError = null,
            confirmPasswordError = null,
            isLoading = false,
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onTogglePasswordVisibility = {},
            onToggleConfirmPasswordVisibility = {},
            onRegisterClick = {},
            onLoginClick = {},
            onBackClick = {},
        )
    }
}