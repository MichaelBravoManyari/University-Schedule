package com.studentsapps.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.studentsapps.designsystem.R

@Composable
fun UniversityScheduleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val lightColors = lightColorScheme(
        primary = colorResource(id = R.color.primary_color),
        onPrimary = colorResource(id = R.color.text_color),
        secondary = colorResource(id = R.color.secondary_color),
        onSecondary = colorResource(id = R.color.secondary_text_color),
        background = colorResource(id = R.color.background_registration_option),
        onBackground = colorResource(id = R.color.text_color),
        surface = colorResource(id = R.color.primary_color),
        onSurface = colorResource(id = R.color.text_color)
    )

    val darkColors = darkColorScheme(
        primary = colorResource(id = R.color.accent_color_black),
        onPrimary = colorResource(id = R.color.secondary_color_black),
        secondary = colorResource(id = R.color.primary_color_black),
        onSecondary = colorResource(id = R.color.secondary_text_color_black),
        background = colorResource(id = R.color.background_registration_option_black),
        onBackground = colorResource(id = R.color.secondary_text_color_black),
        surface = colorResource(id = R.color.secondary_color_black),
        onSurface = colorResource(id = R.color.text_color_black)
    )

    val colors = if (darkTheme) darkColors else lightColors

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
