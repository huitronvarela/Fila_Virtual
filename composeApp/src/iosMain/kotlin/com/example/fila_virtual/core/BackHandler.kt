package com.example.fila_virtual.core

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // No-op for iOS as back gesture is handled by iOS navigation system
}
