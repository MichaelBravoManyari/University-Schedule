package com.mbm.login

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.studentsapps.login.R

/**
 * Shared email input field used across authentication screens.
 *
 * Features:
 * - Leading email icon.
 * - Optional inline validation error.
 * - IME action [ImeAction.Next] that shifts focus to the next field.
 *
 * @param value Current text value.
 * @param onValueChange Callback invoked on text change.
 * @param errorMessage Inline error text shown as supporting text, or null if no error.
 * @param modifier Optional modifier.
 */
@Composable
internal fun EmailField(
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
            focusedBorderColor = MaterialTheme.colorScheme.background,
            focusedLabelColor = MaterialTheme.colorScheme.background,
            cursorColor = MaterialTheme.colorScheme.onSurfaceVariant
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
 * Shared password input field used across authentication screens.
 *
 * Features:
 * - Leading lock icon.
 * - Trailing visibility toggle icon.
 * - Optional inline validation error.
 * - Configurable [imeAction]: use [ImeAction.Next] to advance focus or
 *   [ImeAction.Done] to trigger a primary action via [onImeAction].
 *
 * @param value Current text value.
 * @param label Field label (e.g. "Contraseña", "Confirmar contraseña").
 * @param onValueChange Callback invoked on text change.
 * @param isVisible True when the password is displayed as plain text.
 * @param onToggleVisibility Callback to flip the visibility state.
 * @param errorMessage Inline error text, or null if no error.
 * @param modifier Optional modifier.
 * @param imeAction IME action shown on the keyboard. Defaults to [ImeAction.Done].
 * @param onImeAction Callback fired when the Done IME key is pressed.
 *   Ignored when [imeAction] is [ImeAction.Next] (focus moves automatically).
 */
@Composable
internal fun PasswordField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    errorMessage: String?,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
    visibilityToggleTestTag: String? = null,
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.background,
            focusedLabelColor = MaterialTheme.colorScheme.background,
            cursorColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        label = { Text(text = label) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        trailingIcon = {
            IconButton(
                onClick = onToggleVisibility,
                modifier = if (visibilityToggleTestTag != null) {
                    Modifier.testTag(visibilityToggleTestTag)
                } else {
                    Modifier
                }
            ) {
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
            imeAction = imeAction,
        ),
        // Both actions are declared; only the one matching [imeAction] fires.
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            onDone = { onImeAction() },
        ),
        shape = MaterialTheme.shapes.small,
    )
}

/**
 * Secondary "Volver" outlined button.
 */
@Composable
internal fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(AuthDimens.buttonHeight),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Text(
            text = stringResource(R.string.back),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}