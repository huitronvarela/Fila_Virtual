package com.example.fila_virtual.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Utilidades para manejar la responsividad en Fila Virtual.
 */
data class WindowSize(val width: Dp, val height: Dp) {
    val isTablet = width > 600.dp
    val isSmallScreen = width < 360.dp || height < 650.dp

    // Factor de escala basado en el ancho de la pantalla (tomando 360dp como base estándar)
    private val scaleFactor: Float = (width.value / 360f).coerceIn(0.8f, 1.5f)

    fun adaptiveSp(base: Int): TextUnit {
        return (base * scaleFactor).sp
    }

    fun adaptiveDp(base: Int): Dp {
        return (base * scaleFactor).dp
    }

    // Función para obtener un valor que se reduce en pantallas pequeñas para que quepa todo
    fun compactDp(base: Int, smallScale: Float = 0.8f): Dp {
        return if (isSmallScreen) (base * smallScale).dp else base.dp
    }
}

val LocalWindowSize = compositionLocalOf { WindowSize(0.dp, 0.dp) }

@Composable
fun rememberResponsiveSize(width: Dp, height: Dp): WindowSize {
    // ✅ Ahora sí utiliza el remember de Compose para optimizar rendimiento
    return remember(width, height) {
        WindowSize(width, height)
    }
}