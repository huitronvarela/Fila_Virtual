package com.example.fila_virtual.features.admin.empleados

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fila_virtual.components.BaseFormScreen
import com.example.fila_virtual.components.InputField
import com.example.fila_virtual.data.Empleado
import com.example.fila_virtual.core.*
import com.example.fila_virtual.core.theme.*
import com.example.fila_virtual.features.admin.FormState
import com.example.fila_virtual.core.BackHandler
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AnadirEmpleadoScreen(
    establecimientoId: String = "",
    empleadoToEdit: Empleado? = null,
    onBack: () -> Unit
) {
    val viewModel = remember { EmpleadoViewModel() }
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    var nombre by remember { mutableStateOf(empleadoToEdit?.nombre ?: "") }
    var correo by remember { mutableStateOf(empleadoToEdit?.correo ?: "") }
    var telefono by remember { mutableStateOf(empleadoToEdit?.telefono ?: "") }
    var rolSeleccionado by remember { mutableStateOf(empleadoToEdit?.rol ?: "") }
    var showSuccessSheet by remember { mutableStateOf(false) }

    // Validaciones idénticas a las del Register
    val isFormValid = isValidName(nombre.trim()) && nombre.trim().length >= 3 &&
            isValidPhone(telefono.trim()) &&
            isValidEmail(correo.trim()) &&
            rolSeleccionado.isNotEmpty()

    val haptic = LocalHapticFeedback.current
    BackHandler(onBack = onBack)

    // Modal de Éxito
    if (showSuccessSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showSuccessSheet = false
                viewModel.resetState()
                onBack()
            },
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = PrimaryOrange,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (empleadoToEdit == null) "¡Empleado Registrado!" else "¡Empleado Actualizado!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkGray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (empleadoToEdit == null) "Se ha enviado una invitación a $correo para que pueda unirse a la plataforma." else "Los datos del empleado se han actualizado correctamente.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MediumGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        showSuccessSheet = false
                        viewModel.resetState()
                        onBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Entendido",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            }
        }
    }

    BaseFormScreen(
        title = if (empleadoToEdit == null) "Nuevo Empleado" else "Editar Empleado",
        onBack = onBack,
        isSaveEnabled = isFormValid,
        onSave = {
            if (isFormValid) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.guardarEmpleado(
                    id = empleadoToEdit?.id ?: "",
                    establecimientoId = establecimientoId,
                    nombre = nombre.trim(),
                    correo = correo.trim(),
                    telefono = telefono.trim(),
                    rol = rolSeleccionado,
                    onSuccess = { showSuccessSheet = true }
                )
            }
        },
        saveButtonText = if (uiState is FormState.Loading) "Guardando..." else if (empleadoToEdit == null) "Registrar Empleado" else "Guardar Cambios",
    ) {
        // Avatar selector - Ahora con fondo blanco para resaltar sobre el gris
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, BorderGray, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Añadir foto",
                        tint = MediumGray,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(PrimaryOrange)
                        .clickable { /* Abrir galería */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "FOTO DE PERFIL",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MediumGray,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        // USANDO INPUTFIELD DE UIComponents (Salen blancos por defecto)
        InputField(
            label = "Nombre Completo",
            value = nombre,
            onValueChange = { if (isValidName(it) || it.isEmpty()) nombre = it },
            placeholder = "Ej. Carlos Rodríguez",
            leadingIcon = Icons.Filled.Person,
            isError = nombre.isNotEmpty() && nombre.length < 3,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )

        Spacer(modifier = Modifier.height(16.dp))

        InputField(
            label = "Correo Electrónico",
            value = correo,
            onValueChange = { correo = it },
            placeholder = "carlos@ejemplo.com",
            leadingIcon = Icons.Filled.Email,
            isError = correo.isNotEmpty() && !isValidEmail(correo),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )

        Spacer(modifier = Modifier.height(16.dp))

        InputField(
            label = "Teléfono",
            value = telefono,
            onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) telefono = it },
            placeholder = "300 000 0000",
            leadingIcon = Icons.Filled.Phone,
            isError = telefono.isNotEmpty() && !isValidPhone(telefono),
            visualTransformation = PhoneVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Selección de Cargo / Rol
        Text(
            "Cargo / Rol",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Chef", "Mesero", "Repartidor", "Gerente").forEach { rol ->
                RoleChip(
                    label = rol,
                    isSelected = rolSeleccionado == rol,
                    onClick = { rolSeleccionado = rol }
                )
            }
        }

        // Mensaje de error del ViewModel
        if (uiState is FormState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = (uiState as FormState.Error).message,
                color = TrafficRed,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Al registrar este empleado, se le enviará automáticamente una invitación a su correo electrónico.",
            color = MediumGray,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            lineHeight = androidx.compose.ui.unit.TextUnit(16f, androidx.compose.ui.unit.TextUnitType.Sp)
        )
    }
}

@Composable
private fun RoleChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) PrimaryOrange else Color.White)
            .border(1.dp, if (isSelected) Color.Transparent else BorderGray, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else MediumGray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
