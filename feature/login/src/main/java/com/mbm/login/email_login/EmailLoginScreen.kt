package com.mbm.login.email_login

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.mbm.login.AppWelcomeColumn
import com.mbm.login.AuthDimens
import com.mbm.login.BackButton
import com.mbm.login.LoadingOverlay
import com.mbm.login.RegisterLink
import com.mbm.login.auth.AuthUiError
import com.studentsapps.login.R
import theme.UniversityScheduleTheme

// ================================
// STATEFUL SCREEN
// ================================

/**
 * Adaptive email login screen that adjusts to different screen sizes.
 * Manages UI state and displays loading/error states.
 *
 * @param viewModel The email login ViewModel.
 * @param onNavigateToSchedule Callback invoked once sign-in succeeds.
 * @param onRegisterClick Callback for registration navigation.
 * @param onBackClick      Callback for the "Cancel" / back action.
 * @param modifier Optional modifier for the screen.
 */
@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun EmailLoginScreen(
    viewModel: EmailLoginViewModel,
    onNavigateToSchedule: () -> Unit,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // One-shot: navigate to schedule once sign-in succeeds
    LaunchedEffect(uiState.shouldNavigateToSchedule) {
        if (uiState.shouldNavigateToSchedule) {
            onNavigateToSchedule()
            viewModel.onNavigationComplete()
        }
    }

    // One-shot: show error Toast and dismiss from state
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            val message = when (error) {
                is AuthUiError.Message -> context.getString(error.resId)
                is AuthUiError.Text -> error.message
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.dismissError()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        EmailLoginScreenContent(
            email = uiState.email,
            password = uiState.password,
            isPasswordVisible = uiState.isPasswordVisible,
            emailError = uiState.emailError?.let { context.getString(it) },
            passwordError = uiState.passwordError?.let { context.getString(it) },
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
            onLoginClick = viewModel::onLoginClick,
            onRegisterClick = onRegisterClick,
            onBackClick = onBackClick
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
 * Stateless version of [EmailLoginScreen] for previews and testing.
 * Manages local form state with [rememberSaveable].
 */
@Composable
fun EmailLoginScreen(
    email: String,
    password: String,
    isPasswordVisible: Boolean,
    emailError: String?,
    passwordError: String?,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        EmailLoginScreenContent(
            email = email,
            password = password,
            isPasswordVisible = isPasswordVisible,
            emailError = emailError,
            passwordError = passwordError,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange,
            onTogglePasswordVisibility = onTogglePasswordVisibility,
            onLoginClick = onLoginClick,
            onRegisterClick = onRegisterClick,
            onBackClick = onBackClick
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
 */
@Composable
private fun EmailLoginScreenContent(
    email: String,
    password: String,
    isPasswordVisible: Boolean,
    emailError: String?,
    passwordError: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    // Shared lambda — lets every layout inject its own Modifier without
    // duplicating the form composable tree.
    val loginForm: @Composable (Modifier) -> Unit = { cardModifier ->
        LoginFormCard(
            email = email,
            password = password,
            isPasswordVisible = isPasswordVisible,
            emailError = emailError,
            passwordError = passwordError,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange,
            onTogglePasswordVisibility = onTogglePasswordVisibility,
            onLoginClick = onLoginClick,
            onBackClick = onBackClick,
            modifier = cardModifier,
        )
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.secondary,
    ) {
        when {
            // Compact-height: phone in landscape (height < 480 dp)
            !windowSizeClass.isHeightAtLeastBreakpoint(AuthDimens.HEIGHT_COMPACT_THRESHOLD) -> {
                CompactHeightLayout(
                    loginForm = loginForm,
                    onRegisterClick = onRegisterClick,
                )
            }
            // Expanded / Large: tablets and desktops
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) ||
                    windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_LARGE_LOWER_BOUND) -> {
                ExpandedLayout(
                    loginForm = loginForm,
                    onRegisterClick = onRegisterClick,
                )
            }
            // Default: phone in portrait — two-column layout
            else -> {
                CompactLayout(
                    loginForm = loginForm,
                    onRegisterClick = onRegisterClick,
                )
            }
        }
    }
}

// ================================
// LAYOUTS
// ================================

/**
 * Two-column layout for phones in landscape mode (compact height).
 *
 * Left  → [com.mbm.login.AppWelcomeColumn] (shared branding)
 * Right → login form card
 */
@Composable
private fun CompactHeightLayout(
    loginForm: @Composable (Modifier) -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .verticalScroll(rememberScrollState())
            .padding(AuthDimens.screenPadding),
        horizontalArrangement = Arrangement.spacedBy(
            AuthDimens.sectionSpacing,
            Alignment.CenterHorizontally,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppWelcomeColumn(
            modifier = Modifier.weight(1f),
            titleStyle = MaterialTheme.typography.headlineSmall,
        )
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            loginForm(Modifier)
            Spacer(modifier = Modifier.height(AuthDimens.sectionSpacing))
            RegisterLink(onRegisterClick = onRegisterClick)
        }
    }
}

/**
 * Two-column layout for large / expanded screens (tablets, desktops).
 *
 * Left  → [com.mbm.login.AppWelcomeColumn] with max-width constraint (shared branding)
 * Right → login form card with max-width constraint
 */
@Composable
private fun ExpandedLayout(
    loginForm: @Composable (Modifier) -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(AuthDimens.screenPadding),
        horizontalArrangement = Arrangement.spacedBy(
            AuthDimens.sectionSpacing,
            Alignment.CenterHorizontally,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppWelcomeColumn(modifier = Modifier.widthIn(max = AuthDimens.maxContentWidth))
        Column(
            modifier = Modifier.widthIn(max = AuthDimens.maxContentWidth),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            loginForm(Modifier)
            Spacer(modifier = Modifier.height(AuthDimens.sectionSpacing))
            RegisterLink(onRegisterClick = onRegisterClick)
        }
    }
}

/**
 * Single-column layout for phones in portrait mode.
 */
@Composable
private fun CompactLayout(
    loginForm: @Composable (Modifier) -> Unit,
    onRegisterClick: () -> Unit,
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
        AppWelcomeColumn()
        loginForm(Modifier)
        RegisterLink(onRegisterClick = onRegisterClick)
    }
}

// ================================
// SCREEN-SPECIFIC SECTIONS
// ================================

/**
 * Card that wraps the full login form (title + fields + primary action + secondary link).
 */
@Composable
private fun LoginFormCard(
    email: String,
    password: String,
    isPasswordVisible: Boolean,
    emailError: String?,
    passwordError: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onLoginClick: () -> Unit,
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
        Column(
            modifier = Modifier
                .padding(CARD_PADDING)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(FIELD_SPACING),
        ) {
            LoginFormTitle()

            Spacer(modifier = Modifier.height(AuthDimens.smallSpacing))

            EmailField(
                value = email,
                onValueChange = onEmailChange,
                errorMessage = emailError,
            )

            PasswordField(
                value = password,
                onValueChange = onPasswordChange,
                isVisible = isPasswordVisible,
                onToggleVisibility = onTogglePasswordVisibility,
                errorMessage = passwordError,
                onDone = onLoginClick,
            )

            Spacer(modifier = Modifier.height(AuthDimens.smallSpacing))

            LoginButton(onClick = onLoginClick)

            BackButton(onClick = onBackClick)
        }
    }
}

// ================================
// ATOMIC COMPOSABLES
// ================================

/**
 * "Iniciar Sesión" title inside the card.
 */
@Composable
private fun LoginFormTitle(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.sign_in),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier,
    )
}

/**
 * Email input field with leading email icon and optional inline error message.
 */
@Composable
private fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    errorMessage: String?,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.background
        ),
        label = { Text(text = stringResource(R.string.email)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Email,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        isError = errorMessage != null,
        supportingText = errorMessage?.let {
            { Text(text = it, color = MaterialTheme.colorScheme.error) }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
        ),
        shape = MaterialTheme.shapes.small,
    )
}

/**
 * Password input field with leading lock icon, trailing visibility toggle,
 * and optional inline error message.
 */
@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    errorMessage: String?,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.background
        ),
        label = { Text(text = stringResource(R.string.password)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (isVisible) ImageVector.vectorResource(R.drawable.outline_visibility_off_24)
                    else ImageVector.vectorResource(R.drawable.outline_visibility_24),
                    contentDescription = if (isVisible) stringResource(R.string.hide_password)
                    else stringResource(R.string.show_password),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        visualTransformation = if (isVisible) VisualTransformation.None
        else PasswordVisualTransformation(),
        isError = errorMessage != null,
        supportingText = errorMessage?.let {
            { Text(text = it, color = MaterialTheme.colorScheme.error) }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        shape = MaterialTheme.shapes.small,
    )
}

/**
 * Primary "Iniciar Sesión" action button.
 */
@Composable
private fun LoginButton(
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
            text = stringResource(R.string.sign_in),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

// ================================
// SCREEN-SPECIFIC CONSTANTS
// ================================

private val CARD_PADDING = 24.dp
private val CARD_ELEVATION = 4.dp
private val FIELD_SPACING = 4.dp

// ================================
// PREVIEWS
// ================================

@PreviewScreenSizes
@Composable
private fun EmailLoginScreenPreview() {
    UniversityScheduleTheme {
        EmailLoginScreen(
            email = "user@example.com",
            password = "",
            isPasswordVisible = false,
            emailError = null,
            passwordError = null,
            isLoading = false,
            onEmailChange = {},
            onPasswordChange = {},
            onTogglePasswordVisibility = {},
            onLoginClick = {},
            onRegisterClick = {},
            onBackClick = {}
        )
    }
}