package com.example.fila_virtual.features.admin.inicio

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fila_virtual.components.BaseFormScreen
import com.example.fila_virtual.components.FormImagePicker
import com.example.fila_virtual.components.FormTextField
import com.example.fila_virtual.core.theme.*

@Composable
fun AñadirEstablecimientoScreen(
    onBack: () -> Unit,
    onSave: (nombre: String, direccion: String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    BaseFormScreen(
        title = "Nuevo Establecimiento",
        onBack = onBack,
        onSave = {
            onSave(nombre, direccion)
        },
        saveButtonText = "Registrar Local"
    ) {
        FormImagePicker(
            label = "LOGO O IMAGEN DEL LOCAL",
            onClick = { /* Lógica para seleccionar imagen */ }
        )

        Spacer(modifier = Modifier.height(24.dp))

        FormTextField(
            label = "NOMBRE DEL ESTABLECIMIENTO",
            value = nombre,
            onValueChange = { nombre = it },
            placeholder = "Ej. Al Toque Manzanillo"
        )

        Spacer(modifier = Modifier.height(16.dp))

        FormTextField(
            label = "DIRECCIÓN COMPLETA",
            value = direccion,
            onValueChange = { direccion = it },
            placeholder = "Calle, Número, Colonia...",
            singleLine = false,
            minHeight = 80
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        // Aquí podrías agregar más campos como Teléfono, Horarios, etc.
    }
}
