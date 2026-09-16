package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CryptoColorScheme = darkColorScheme(
    primary = CryptoGold,
    onPrimary = CryptoBackground,
    primaryContainer = CryptoSurfaceElevated,
    onPrimaryContainer = CryptoGold,
    secondary = CryptoGreen,
    onSecondary = CryptoBackground,
    secondaryContainer = CryptoCardHighlight,
    onSecondaryContainer = CryptoGreen,
    tertiary = CryptoCyan,
    onTertiary = CryptoBackground,
    background = CryptoBackground,
    onBackground = CryptoTextPrimary,
    surface = CryptoSurface,
    onSurface = CryptoTextPrimary,
    surfaceVariant = CryptoSurfaceElevated,
    onSurfaceVariant = CryptoTextSecondary,
    outline = CryptoSurfaceBorder,
    outlineVariant = CryptoSurfaceBorderActive,
    error = CryptoRed,
    onError = CryptoTextPrimary
)

@Composable
fun CryptoCalcTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CryptoColorScheme,
        typography = Typography,
        content = content
    )
}

