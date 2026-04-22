package com.example.fila_virtual.core

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

class AndroidPermissionsManager(
    private val context: android.content.Context
) : PermissionsManager {

    private var onResult: ((Boolean) -> Unit)? = null
    
    // Este launcher se manejará desde el Composable
    var launcher: androidx.activity.result.ActivityResultLauncher<String>? = null

    override fun askPermission(permission: PermissionType, callback: (Boolean) -> Unit) {
        onResult = callback
        val androidPermission = when (permission) {
            PermissionType.CAMERA -> Manifest.permission.CAMERA
            PermissionType.GALLERY -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) 
                Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE
            PermissionType.LOCATION -> Manifest.permission.ACCESS_FINE_LOCATION
            PermissionType.NOTIFICATIONS -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) 
                Manifest.permission.POST_NOTIFICATIONS else null
        }

        if (androidPermission == null || isPermissionGranted(permission)) {
            callback(true)
        } else {
            launcher?.launch(androidPermission)
        }
    }

    override fun isPermissionGranted(permission: PermissionType): Boolean {
        val androidPermission = when (permission) {
            PermissionType.CAMERA -> Manifest.permission.CAMERA
            PermissionType.GALLERY -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) 
                Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE
            PermissionType.LOCATION -> Manifest.permission.ACCESS_FINE_LOCATION
            PermissionType.NOTIFICATIONS -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) 
                Manifest.permission.POST_NOTIFICATIONS else null
        }
        
        return androidPermission?.let {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        } ?: true
    }

    fun handleResult(granted: Boolean) {
        onResult?.invoke(granted)
    }
}

@Composable
actual fun rememberPermissionsManager(): PermissionsManager {
    val context = LocalContext.current
    val manager = remember { AndroidPermissionsManager(context) }
    
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        manager.handleResult(isGranted)
    }
    
    manager.launcher = launcher
    return manager
}
