package com.example.fila_virtual.features.empleados

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.fila_virtual.features.user.UserViewModel

@Composable
fun EmpleadoMainScreen(
    viewModel: UserViewModel,
    onLogout: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Pantalla de Empleado (Cocina, Cajero, etc.)")

        Button(onClick = { viewModel.signOut(onLogout) }) {
            Text("Cerrar Sesión")
        }
    }
}