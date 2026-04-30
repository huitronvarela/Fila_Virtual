package com.example.fila_virtual.core

import androidx.compose.runtime.*

class IosPermissionsManager : PermissionsManager {
    override fun askPermission(permission: PermissionType, callback: (Boolean) -> Unit) {
        // En iOS los permisos se manejan de forma distinta (Info.plist)
        // Por ahora simulamos que siempre se conceden para que compile
        callback(true)
    }

    override fun isPermissionGranted(permission: PermissionType): Boolean {
        return true
    }
}

@Composable
actual fun rememberPermissionsManager(): PermissionsManager {
    return remember { IosPermissionsManager() }
}
