package com.example.fila_virtual.core

import androidx.compose.runtime.Composable

/**
 * Interfaz para manejar permisos en KMP.
 * La implementación real (actual) se encuentra en androidMain e iosMain.
 */
interface PermissionsManager {
    fun askPermission(permission: PermissionType, callback: (Boolean) -> Unit)
    fun isPermissionGranted(permission: PermissionType): Boolean
}

enum class PermissionType {
    CAMERA,
    GALLERY,
    LOCATION,
    NOTIFICATIONS
}

@Composable
expect fun rememberPermissionsManager(): PermissionsManager

