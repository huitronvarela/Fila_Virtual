package com.example.fila_virtual.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Esquema de colores de DÍA (Predeterminado)
private val LightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = Color.White,
    onBackground = DarkGray, // Textos oscuros
    onSurface = DarkGray,
    error = TrafficRed
)

@Composable
fun FilaVirtualTheme(
    // Forzamos el modo claro por ahora
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    // Por ahora siempre usamos LightColorScheme
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}