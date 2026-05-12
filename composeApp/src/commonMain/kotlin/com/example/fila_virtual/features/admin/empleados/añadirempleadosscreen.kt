package com.example.fila_virtual.features.admin.empleados

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AñadirEmpleadoScreen(
    establecimientoId: String,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir Empleado") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Formulario para el establecimiento: $establecimientoId")
            // Aquí irían los campos del formulario
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Nombre del Empleado") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Rol") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { /* Lógica para guardar */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Empleado")
            }
        }
    }
}
