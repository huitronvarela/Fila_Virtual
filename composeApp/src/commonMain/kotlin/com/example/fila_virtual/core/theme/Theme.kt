package com.example.fila_virtual.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Esquema de colores de DÍA
private val LightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = Color.White,
    onBackground = DarkGray, // Textos oscuros
    onSurface = DarkGray,
    error = TrafficRed
)

// Esquema de colores de NOCHE
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryOrange,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.White,
    onBackground = Color.White, // Textos blancos automáticamente
    onSurface = Color.White,
    error = TrafficRed
)

@Composable
fun FilaVirtualTheme(
    // Esta función detecta automáticamente la configuración del celular
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}