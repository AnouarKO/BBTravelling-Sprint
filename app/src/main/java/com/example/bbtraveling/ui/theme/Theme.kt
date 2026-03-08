package com.example.bbtraveling.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BrandYellow,
    onPrimary = BrandPurpleDark,
    secondary = Purple80,
    tertiary = Pink80,
    background = BrandSurfaceDark,
    surface = Color(0xFF211333),
    surfaceVariant = Color(0xFF302145),
    onBackground = Color(0xFFF8F2FF),
    onSurface = Color(0xFFF8F2FF),
    onSurfaceVariant = Color(0xFFD4C8E8),
    outline = Color(0xFF5D4D79)
)

private val LightColorScheme = lightColorScheme(
    primary = BrandPurple,
    onPrimary = Color.White,
    secondary = BrandYellow,
    onSecondary = BrandText,
    tertiary = Purple80,
    background = BrandSurface,
    onBackground = BrandText,
    surface = Color.White,
    onSurface = BrandText,
    surfaceVariant = BrandPurpleLight,
    onSurfaceVariant = BrandTextMuted,
    outline = BrandStroke
)

@Composable
fun BBTravelingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
