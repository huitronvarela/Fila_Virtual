package com.example.fila_virtual.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.fila_virtual.data.Roles
import com.example.fila_virtual.features.admin.AdminMainScreen
import com.example.fila_virtual.features.empleados.EmpleadoMainScreen
import com.example.fila_virtual.features.user.ClienteMainScreen
import com.example.fila_virtual.features.user.UserViewModel

@Composable
fun MainScreen(
    viewModel: UserViewModel = remember { UserViewModel() },
    onLogout: () -> Unit
) {
    val usuario = viewModel.usuario
    val isLoading = viewModel.isLoading

    if (isLoading || usuario == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else {
        // --- DEPURACIÓN ---
        // Esto aparecerá en tu Logcat/Consola para confirmar qué está pasando
        val rolDetectado = usuario.rolGlobal.lowercase().trim()
        println("DEBUG_ROLE: El usuario ${usuario.email} tiene el rol: '$rolDetectado'")

        // Enrutador basado en el rol del usuario (Normalizado)
        when (rolDetectado) {
            "admin" -> {
                AdminMainScreen(viewModel = viewModel, onLogout = onLogout)
            }
            "empleado" -> {
                EmpleadoMainScreen(viewModel = viewModel, onLogout = onLogout)
            }
            else -> {
                // Si llega aquí, es porque el rol no es admin ni empleado (ej. "cliente")
                ClienteMainScreen(viewModel = viewModel, onLogout = onLogout)
            }
        }
    }
}
