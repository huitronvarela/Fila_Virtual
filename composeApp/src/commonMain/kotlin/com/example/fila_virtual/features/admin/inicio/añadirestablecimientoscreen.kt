package com.example.fila_virtual.features.admin.inicio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fila_virtual.components.*
import com.example.fila_virtual.data.*
import com.example.fila_virtual.core.theme.*
import com.example.fila_virtual.features.admin.EstablecimientoViewModel
import com.example.fila_virtual.features.admin.FormState
import com.example.fila_virtual.core.PermissionType
import com.example.fila_virtual.core.rememberPermissionsManager

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AñadirEstablecimientoScreen(
    onBack: () -> Unit,
    viewModel: EstablecimientoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val permissions = rememberPermissionsManager()

    // Estados de los campos
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var activo by remember { mutableStateOf(true) }
    
    // Horario
    var apertura by remember { mutableStateOf("09:00 AM") }
    var cierre by remember { mutableStateOf("10:00 PM") }
    
    // Estados para diálogos y selectores
    var showAperturaPicker by remember { mutableStateOf(false) }
    var showCierrePicker by remember { mutableStateOf(false) }
    var showImageSheet by remember { mutableStateOf(false) }

    val timePickerStateApertura = rememberTimePickerState(initialHour = 9, initialMinute = 0)
    val timePickerStateCierre = rememberTimePickerState(initialHour = 22, initialMinute = 0)

    // Categorías
    val categoriasDisponibles = listOf("Cafetería", "Restaurante")
    var categoriasSeleccionadas by remember { mutableStateOf(listOf("Comida Rápida")) }

    // Función para formatear la hora
    fun formatTime(hour: Int, minute: Int): String {
        val amPm = if (hour < 12) "AM" else "PM"
        val h = if (hour % 12 == 0) 12 else hour % 12
        return "${h.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')} $amPm"
    }

    // Diálogo de selección de hora (Apertura)
    if (showAperturaPicker) {
        TimePickerDialog(
            onDismissRequest = { showAperturaPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    apertura = formatTime(timePickerStateApertura.hour, timePickerStateApertura.minute)
                    showAperturaPicker = false
                }) { Text("Confirmar", color = PrimaryOrange) }
            },
            dismissButton = {
                TextButton(onClick = { showAperturaPicker = false }) { Text("Cancelar", color = MediumGray) }
            }
        ) {
            TimePicker(state = timePickerStateApertura)
        }
    }

    // Diálogo de selección de hora (Cierre)
    if (showCierrePicker) {
        TimePickerDialog(
            onDismissRequest = { showCierrePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    cierre = formatTime(timePickerStateCierre.hour, timePickerStateCierre.minute)
                    showCierrePicker = false
                }) { Text("Confirmar", color = PrimaryOrange) }
            },
            dismissButton = {
                TextButton(onClick = { showCierrePicker = false }) { Text("Cancelar", color = MediumGray) }
            }
        ) {
            TimePicker(state = timePickerStateCierre)
        }
    }

    // Modal para seleccionar origen de imagen
    if (showImageSheet) {
        ModalBottomSheet(
            onDismissRequest = { showImageSheet = false },
            containerColor = Color.White
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
                Text(
                    "Seleccionar imagen",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkGray
                )
                ListItem(
                    headlineContent = { Text("Tomar foto") },
                    leadingContent = { Icon(Icons.Default.CameraAlt, contentDescription = null, tint = PrimaryOrange) },
                    modifier = Modifier.clickable {
                        permissions.askPermission(PermissionType.CAMERA) { granted ->
                            if (granted) {
                                // Aquí se llamaría a la lógica para abrir la cámara
                            }
                        }
                        showImageSheet = false
                    }
                )
                ListItem(
                    headlineContent = { Text("Elegir de la galería") },
                    leadingContent = { Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = PrimaryOrange) },
                    modifier = Modifier.clickable {
                        permissions.askPermission(PermissionType.GALLERY) { granted ->
                            if (granted) {
                                // Aquí se llamaría a la lógica para abrir la galería
                            }
                        }
                        showImageSheet = false
                    }
                )
            }
        }
    }

    BaseFormScreen(
        title = "Añadir Establecimiento",
        onBack = onBack,
        onSave = {
            if (nombre.isNotBlank()) {
                val nuevo = Establecimiento(
                    nombre = nombre,
                    descripcion = descripcion,
                    ubicacion = Ubicacion(direccion = direccion),
                    activo = activo,
                    categorias = categoriasSeleccionadas,
                    horario = mapOf(
                        "todos" to HorarioDia(apertura = apertura, cierre = cierre)
                    ),
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                viewModel.guardarEstablecimiento(nuevo) {
                    onBack()
                }
            }
        },
        saveButtonText = if (uiState is FormState.Loading) "Guardando..." else "Guardar Establecimiento",
        saveIcon = Icons.Default.Save
    ) {
        // TARJETA 1: Subir Imagen
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                FormImagePicker(
                    label = "Logotipo del Establecimiento",
                    onClick = { showImageSheet = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TARJETA 2: Información General
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Información General",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkGray
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = activo,
                            onCheckedChange = { activo = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PrimaryOrange,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = MediumGray,
                                uncheckedBorderColor = Color.Transparent
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Activo", color = DarkGray, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                FormTextField(
                    label = "Nombre del Establecimiento",
                    value = nombre,
                    onValueChange = { nombre = it },
                    placeholder = "Ej. Burger Station"
                )

                Spacer(modifier = Modifier.height(16.dp))

                FormTextField(
                    label = "Dirección",
                    value = direccion,
                    onValueChange = { direccion = it },
                    placeholder = "Av. Principal #123, Col. Centro"
                )

                Spacer(modifier = Modifier.height(16.dp))

                FormTextField(
                    label = "Descripción",
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    placeholder = "Describe el ambiente y especialidades...",
                    singleLine = false,
                    minHeight = 100
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Categorías",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categoriasSeleccionadas.forEach { categoria ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = PrimaryOrange.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, PrimaryOrange.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = categoria, color = PrimaryOrange, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Quitar",
                                    tint = PrimaryOrange,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable {
                                            categoriasSeleccionadas = categoriasSeleccionadas - categoria
                                        }
                                )
                            }
                        }
                    }

                    categoriasDisponibles.filter { it !in categoriasSeleccionadas }.forEach { categoria ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = LightBackground,
                            modifier = Modifier.clickable {
                                categoriasSeleccionadas = categoriasSeleccionadas + categoria
                            }
                        ) {
                            Text(
                                text = categoria,
                                color = DarkGray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = LightBackground,
                        modifier = Modifier.clickable { /* Diálogo para añadir nueva categoría */ }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = DarkGray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Añadir", color = DarkGray, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TARJETA 3: Horario Dinámico
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Horario",
                        tint = PrimaryOrange,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Horario",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkGray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        FormTextField(
                            label = "Apertura",
                            value = apertura,
                            onValueChange = {},
                            onClick = { showAperturaPicker = true },
                            trailingIcon = {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = DarkGray, modifier = Modifier.size(20.dp))
                            }
                        )
                    }
                    Text(text = "-", fontWeight = FontWeight.Bold, color = DarkGray, modifier = Modifier.padding(top = 24.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        FormTextField(
                            label = "Cierre",
                            value = cierre,
                            onValueChange = {},
                            onClick = { showCierrePicker = true },
                            trailingIcon = {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = DarkGray, modifier = Modifier.size(20.dp))
                            }
                        )
                    }
                }
            }
        }

        if (uiState is FormState.Error) {
            Text(
                text = (uiState as FormState.Error).message,
                color = TrafficRed,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    title: String = "Seleccionar hora",
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        title = { Text(text = title, style = MaterialTheme.typography.titleMedium, color = DarkGray) },
        text = { content() },
        containerColor = Color.White,
    )
}
