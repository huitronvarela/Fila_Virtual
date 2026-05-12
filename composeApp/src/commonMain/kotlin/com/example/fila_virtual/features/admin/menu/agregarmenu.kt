package com.example.fila_virtual.features.admin.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fila_virtual.components.BaseFormScreen
import com.example.fila_virtual.components.FormImagePicker
import com.example.fila_virtual.components.FormTextField
import com.example.fila_virtual.core.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AgregarPlatilloScreen(
    onBack: () -> Unit,
    onSave: (nombre: String, descripcion: String, precio: Double, categoria: String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }

    val categorias = listOf("Entradas", "Platos Fuertes", "Bebidas", "Postres")
    var categoriaSeleccionada by remember { mutableStateOf(categorias[0]) }

    BaseFormScreen(
        title = "Agregar Platillo",
        onBack = onBack,
        onSave = {
            val precioDouble = precio.toDoubleOrNull() ?: 0.0
            onSave(nombre, descripcion, precioDouble, categoriaSeleccionada)
        },
        saveButtonText = "Guardar Platillo"
    ) {
        FormImagePicker(
            label = "IMAGEN DEL PLATILLO",
            onClick = { /* Selector de imagen */ }
        )

        Spacer(modifier = Modifier.height(24.dp))

        FormTextField(
            label = "NOMBRE DEL PLATILLO",
            value = nombre,
            onValueChange = { nombre = it },
            placeholder = "Ej. Hamburguesa Especial AlToque"
        )

        Spacer(modifier = Modifier.height(16.dp))

        FormTextField(
            label = "DESCRIPCIÓN",
            value = descripcion,
            onValueChange = { descripcion = it },
            placeholder = "Describe los ingredientes, alérgenos...",
            singleLine = false,
            minHeight = 120
        )

        Spacer(modifier = Modifier.height(16.dp))

        FormTextField(
            label = "PRECIO",
            value = precio,
            onValueChange = { precio = it },
            placeholder = "0.00",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = {
                Text(
                    "$",
                    fontWeight = FontWeight.Bold,
                    color = DarkGray,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "CATEGORÍA",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MediumGray
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categorias.forEach { categoria ->
                val isSelected = categoria == categoriaSeleccionada
                Surface(
                    color = if (isSelected) PrimaryOrange else Color.White,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.clickable { categoriaSeleccionada = categoria },
                    border = if (!isSelected) BorderStroke(1.dp, BorderGray) else null
                ) {
                    Text(
                        text = categoria,
                        color = if (isSelected) Color.White else DarkGray,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
