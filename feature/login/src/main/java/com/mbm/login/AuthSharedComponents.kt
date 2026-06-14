package com.mbm.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.studentsapps.login.R

// ================================
// SHARED COMPOSABLES
// ================================

/**
 * Application logo displayed as a circular image.
 *
 * @param size Diameter of the logo box. Defaults to [AuthDimens.logoSize].
 * @param modifier Optional modifier.
 */
@Composable
internal fun AppLogoSection(
    modifier: Modifier = Modifier,
    size: Dp = AuthDimens.logoSize,
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_app_logo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

/**
 * Application name headline ("Mi Horario Universitario").
 *
 * [com.mbm.login.auth.AuthScreen] uses `headlineMedium`; [com.mbm.login.email_login.EmailLoginScreen] uses `headlineSmall`.
 * Pass the desired [style] to keep each screen faithful to its design.
 *
 * @param modifier Optional modifier.
 * @param style Typography style. Defaults to MaterialTheme.typography.headlineMedium.
 */
@Composable
internal fun AppTitleSection(
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.headlineMedium,
) {
    Text(
        text = stringResource(com.studentsapps.common.R.string.app_name),
        style = style,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        modifier = modifier,
    )
}

/**
 * Welcome subtitle shown below the app title.
 *
 * @param modifier Optional modifier.
 */
@Composable
internal fun AppWelcomeTextSection(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.welcome_to_university_schedule),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.fillMaxWidth(),
    )
}

/**
 * Composite column with the app logo, title, and welcome subtitle.
 * Used as the left/top "branding" column in all two-column layouts.
 *
 * @param titleStyle Typography style forwarded to [AppTitleSection].
 * @param modifier Optional modifier.
 */
@Composable
internal fun AppWelcomeColumn(
    modifier: Modifier = Modifier,
    titleStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.headlineMedium,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AppLogoSection()
        Spacer(modifier = Modifier.height(AuthDimens.sectionSpacing))
        AppTitleSection(style = titleStyle)
        Spacer(modifier = Modifier.height(AuthDimens.smallSpacing))
        AppWelcomeTextSection()
    }
}

/**
 * "¿No tienes una cuenta? Regístrate" row displayed below the main
 * auth card on every login/register screen.
 *
 * @param onRegisterClick Called when the user taps "Regístrate".
 * @param modifier Optional modifier.
 */
@Composable
internal fun RegisterLink(
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.are_you_new),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(AuthDimens.linkSpacing))
        TextButton(onClick = onRegisterClick) {
            Text(
                text = stringResource(R.string.sign_up_here),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.background,
            )
        }
    }
}

/**
 * "¿Ya tienes una cuenta? Inicia sesión" footer row.
 * Shown on sign-up screens to redirect existing users to sign-in.
 *
 * @param onLoginClick Called when the user taps "Inicia sesión".
 * @param modifier Optional modifier.
 */
@Composable
internal fun LoginLink(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.already_have_account),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(AuthDimens.linkSpacing))
        TextButton(onClick = onLoginClick) {
            Text(
                text = stringResource(R.string.sign_in_action),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.background,
            )
        }
    }
}

/**
 * Full-screen semi-transparent overlay with a centered progress indicator.
 *
 * Blocks all user interaction while an async operation is in progress.
 *
 * @param modifier Optional modifier.
 */
@Composable
internal fun LoadingOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .zIndex(Float.MAX_VALUE)
            .testTag("loading_overlay"),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(56.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp,
        )
    }
}

// ================================
// SHARED DIMENSION TOKENS
// ================================

/**
 * Single source of truth for dimension constants shared across auth screens.
 *
 * Screen-specific values (e.g. `CARD_PADDING`, `FIELD_SPACING`) remain
 * private constants inside each screen file.
 */
internal object AuthDimens {
    /** Outer padding applied to every auth screen root container. */
    val screenPadding: Dp = 24.dp

    /** Vertical/horizontal gap between major sections. */
    val sectionSpacing: Dp = 24.dp

    /** Small gap between tightly related elements. */
    val smallSpacing: Dp = 8.dp

    /** Gap between the "are you new?" text and the "Register" link. */
    val linkSpacing: Dp = 4.dp

    /** Uniform height for primary action buttons. */
    val buttonHeight: Dp = 56.dp

    /**
     * Logo diameter used by default in [AppLogoSection].
     * [com.mbm.login.auth.AuthScreen] keeps 160 dp; [com.mbm.login.email_login.EmailLoginScreen] overrides to 120 dp.
     */
    val logoSize: Dp = 160.dp

    /**
     * Maximum width applied to each content column on expanded screens.
     * Each screen may override this value for its own layout if needed.
     */
    val maxContentWidth: Dp = 540.dp

    /**
     * Height breakpoint (in dp) below which the compact-height landscape
     * layout is activated.
     */
    const val HEIGHT_COMPACT_THRESHOLD: Int = 480
}