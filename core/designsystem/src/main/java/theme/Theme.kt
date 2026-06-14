package theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Suppress("ktlint:standard:function-naming")
@Composable
fun UniversityScheduleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val lightColors =
        lightColorScheme(
            primary = PrimaryLight,
            onPrimary = OnPrimaryLight,
            secondary = SecondaryLight,
            onSecondary = OnSecondaryLight,
            background = BackgroundLight,
            onBackground = OnBackgroundLight,
            surface = SurfaceLight,
            onSurface = OnSurfaceLight,
        )

    val darkColors =
        darkColorScheme(
            primary = PrimaryDark,
            onPrimary = OnPrimaryDark,
            secondary = SecondaryDark,
            onSecondary = OnSecondaryDark,
            background = BackgroundDark,
            onBackground = OnBackgroundDark,
            surface = SurfaceDark,
            onSurface = OnSurfaceDark,
        )

    val colors = if (darkTheme) darkColors else lightColors

    MaterialTheme(
        colorScheme = colors,
        content = content,
        typography = UniversityScheduleTypography,
        shapes = Shapes
    )
}
