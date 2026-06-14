package com.mbm.login.auth

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.mbm.login.AppLogoSection
import com.mbm.login.AppTitleSection
import com.mbm.login.AppWelcomeColumn
import com.mbm.login.AppWelcomeTextSection
import com.mbm.login.AuthDimens
import com.mbm.login.LoadingOverlay
import com.mbm.login.RegisterLink
import com.studentsapps.login.BuildConfig
import com.studentsapps.login.R
import theme.UniversityScheduleTheme

/**
 * Adaptive authentication screen that adjusts to different screen sizes.
 * Manages UI state and displays loading/error states.
 *
 * @param viewModel The authentication ViewModel.
 * @param onEmailSignIn Callback for email sign-in navigation.
 * @param onRegisterClick Callback for registration navigation.
 * @param onNavigateToSchedule Callback when user should navigate to schedule.
 * @param modifier Optional modifier for the screen.
 */
@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onEmailSignIn: () -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateToSchedule: () -> Unit,
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
        AuthScreenContent(
            onEmailSignIn = onEmailSignIn,
            onGoogleSignIn = {
                viewModel.initiateGoogleSignIn(context, BuildConfig.GOOGLE_WEB_CLIENT_ID)
            },
            onRegisterClick = onRegisterClick,
        )

        if (uiState.isLoading) {
            // Shared overlay — imported from AuthSharedComponents.kt
            LoadingOverlay()
        }
    }
}

/**
 * Stateless version of [AuthScreen] for previews and testing.
 * This version does not require a ViewModel.
 */
@Composable
fun AuthScreen(
    onEmailSignIn: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AuthScreenContent(
        onEmailSignIn = onEmailSignIn,
        onGoogleSignIn = onGoogleSignIn,
        onRegisterClick = onRegisterClick,
        modifier = modifier,
    )
}

// ================================
// CONTENT ROUTER
// ================================

/**
 * Picks the correct layout based on the current [WindowSizeClass].
 */
@Composable
private fun AuthScreenContent(
    onEmailSignIn: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.secondary,
    ) {
        when {
            // Small screen in horizontal mode (height < 480 dp)
            !windowSizeClass.isHeightAtLeastBreakpoint(AuthDimens.HEIGHT_COMPACT_THRESHOLD) -> {
                CompactHeightLayout(
                    onEmailSignIn = onEmailSignIn,
                    onGoogleSignIn = onGoogleSignIn,
                    onRegisterClick = onRegisterClick,
                )
            }
            // Large or expanded screens (tablets, desktops)
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) ||
                    windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_LARGE_LOWER_BOUND) -> {
                ExpandedLayout(
                    onEmailSignIn = onEmailSignIn,
                    onGoogleSignIn = onGoogleSignIn,
                    onRegisterClick = onRegisterClick,
                )
            }
            // Default layout — phones in portrait orientation
            else -> {
                CompactLayout(
                    onEmailSignIn = onEmailSignIn,
                    onGoogleSignIn = onGoogleSignIn,
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
 */
@Composable
private fun CompactHeightLayout(
    onEmailSignIn: () -> Unit,
    onGoogleSignIn: () -> Unit,
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
        // Left: branding — imported from AuthSharedComponents.kt
        AppWelcomeColumn(modifier = Modifier.weight(1f))

        // Right: sign-in actions
        AuthActionsColumn(
            onEmailSignIn = onEmailSignIn,
            onGoogleSignIn = onGoogleSignIn,
            onRegisterClick = onRegisterClick,
            modifier = Modifier.weight(1f),
        )
    }
}

/**
 * Two-column layout for large / expanded screens (tablets, desktops).
 */
@Composable
private fun ExpandedLayout(
    onEmailSignIn: () -> Unit,
    onGoogleSignIn: () -> Unit,
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
        // Left: branding — imported from AuthSharedComponents.kt
        AppWelcomeColumn(modifier = Modifier.widthIn(max = AuthDimens.maxContentWidth))

        // Right: sign-in actions
        AuthActionsColumn(
            onEmailSignIn = onEmailSignIn,
            onGoogleSignIn = onGoogleSignIn,
            onRegisterClick = onRegisterClick,
            modifier = Modifier.widthIn(max = AuthDimens.maxContentWidth),
        )
    }
}

/**
 * Single-column layout for phones in portrait mode.
 */
@Composable
private fun CompactLayout(
    onEmailSignIn: () -> Unit,
    onGoogleSignIn: () -> Unit,
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
        // Shared branding — imported from AuthSharedComponents.kt
        AppLogoSection()
        AppTitleSection()
        AppWelcomeTextSection()

        Spacer(modifier = Modifier.height(EXTRA_SPACING))

        SignInButtons(
            onEmailSignIn = onEmailSignIn,
            onGoogleSignIn = onGoogleSignIn,
        )

        // Shared footer link — imported from AuthSharedComponents.kt
        RegisterLink(onRegisterClick = onRegisterClick)
    }
}

// ================================
// SCREEN-SPECIFIC SECTIONS
// ================================

/**
 * Right column grouping sign-in buttons + register link for two-column layouts.
 */
@Composable
private fun AuthActionsColumn(
    onEmailSignIn: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        SignInButtons(
            onEmailSignIn = onEmailSignIn,
            onGoogleSignIn = onGoogleSignIn,
        )
        Spacer(modifier = Modifier.height(AuthDimens.sectionSpacing))
        // Shared footer link — imported from AuthSharedComponents.kt
        RegisterLink(onRegisterClick = onRegisterClick)
    }
}

/**
 * Column with the "Iniciar sesión" header, email button, divider, and Google button.
 */
@Composable
private fun SignInButtons(
    onEmailSignIn: () -> Unit,
    onGoogleSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(BUTTON_SPACING),
    ) {
        SignInSectionHeader()

        Spacer(modifier = Modifier.height(AuthDimens.smallSpacing))

        EmailSignInButton(onClick = onEmailSignIn)

        Spacer(modifier = Modifier.height(DIVIDER_SPACING))

        OrDivider()

        GoogleSignInButton(onClick = onGoogleSignIn)
    }
}

/**
 * Section title "Inicia sesión para acceder a tu calendario".
 */
@Composable
private fun SignInSectionHeader(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.login_to_access_calendar),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier,
    )
}

/**
 * Primary "Continuar con email" button.
 */
@Composable
private fun EmailSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(AuthDimens.buttonHeight),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_email),
            contentDescription = null,
            modifier = Modifier.size(ICON_SIZE),
            tint = MaterialTheme.colorScheme.secondary,
        )
        Spacer(modifier = Modifier.width(ICON_SPACING))
        Text(
            text = stringResource(R.string.sign_in_with_email),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

/**
 * "── O TAMBIÉN ──" horizontal divider with text.
 */
@Composable
private fun OrDivider(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = DIVIDER_VERTICAL_PADDING),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.or_also),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = DIVIDER_TEXT_PADDING),
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

/**
 * "Continuar con Google" outlined text button.
 */
@Composable
private fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(AuthDimens.buttonHeight),
        shape = MaterialTheme.shapes.large,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.logo_google),
            contentDescription = null,
            modifier = Modifier.size(ICON_SIZE),
            tint = Color.Unspecified,
        )
        Spacer(modifier = Modifier.width(ICON_SPACING))
        Text(
            text = stringResource(R.string.sign_in_with_google),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

// ================================
// SCREEN-SPECIFIC CONSTANTS
// ================================

private val EXTRA_SPACING          = 24.dp
private val BUTTON_SPACING         = 12.dp
private val DIVIDER_SPACING        = 2.dp
private val ICON_SPACING           = 8.dp
private val ICON_SIZE              = 20.dp
private val DIVIDER_VERTICAL_PADDING = 8.dp
private val DIVIDER_TEXT_PADDING   = 16.dp

// ================================
// PREVIEWS
// ================================

@PreviewScreenSizes
@Composable
private fun AuthScreenPreview() {
    UniversityScheduleTheme {
        AuthScreen(
            onEmailSignIn = {},
            onGoogleSignIn = {},
            onRegisterClick = {},
        )
    }
}