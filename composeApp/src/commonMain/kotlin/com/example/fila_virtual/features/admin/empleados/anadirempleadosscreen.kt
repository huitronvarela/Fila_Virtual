package com.example.fila_virtual.features.admin.empleados

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fila_virtual.components.BaseFormScreen
import com.example.fila_virtual.components.InputField
import com.example.fila_virtual.data.Empleado
import com.example.fila_virtual.features.admin.EstablecimientoViewModel
import com.example.fila_virtual.core.*
import com.example.fila_virtual.core.theme.*
import com.example.fila_virtual.features.admin.FormState
import com.example.fila_virtual.core.BackHandler
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AnadirEmpleadoScreen(
    empleado: EmpleadoDetalle? = null,
    establecimientoId: String,
    ownerUid: String,
    viewModel: EmpleadoViewModel,
    establecimientoViewModel: EstablecimientoViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isEditing = empleado != null
    val focusManager = LocalFocusManager.current

    var correo by remember { mutableStateOf(empleado?.correo ?: "") }
    var rol by remember { mutableStateOf(empleado?.rol ?: "cajero") }
    var localError by remember { mutableStateOf("") }
    var invitationToken by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current
    
    val establecimientos by establecimientoViewModel.establecimientos.collectAsState()
    
    LaunchedEffect(ownerUid) {
        establecimientoViewModel.setOwnerUid(ownerUid)
    }

    var selectedEstablecimientoId by remember { mutableStateOf(establecimientoId) }
    var expandedEstablecimiento by remember { mutableStateOf(false) }

    val sucursalActual = if (selectedEstablecimientoId.isEmpty()) {
        "Seleccionar Sucursal"
    } else {
        establecimientos.find { it.id == selectedEstablecimientoId }?.nombre ?: "Sucursal desconocida"
    }

    LaunchedEffect(uiState) {
        if (uiState is FormState.Success && isEditing) {
            viewModel.resetState()
            onNavigateBack()
        }
    }

    BaseFormScreen(
        title = if (isEditing) "Editar Empleado" else "Añadir Empleado",
        onBack = onNavigateBack,
        //isLoading = uiState is FormState.Loading,
        saveButtonText = if (isEditing) "Guardar Cambios" else "Vincular Empleado",
        onSave = {
            if (selectedEstablecimientoId.isEmpty()) {
                localError = "Debes seleccionar una sucursal."
                return@BaseFormScreen
            }
            localError = ""
            focusManager.clearFocus()
            if (isEditing) {
                viewModel.guardarEmpleadoPorCorreo(
                    correoBusqueda = correo,
                    rol = rol,
                    establecimientoId = selectedEstablecimientoId,
                    onSuccess = onNavigateBack
                )
            } else {
                viewModel.enviarInvitacionPorCorreo(
                    correo = correo,
                    rol = rol,
                    establecimientoId = selectedEstablecimientoId,
                    onSent = { invitationToken = it }
                )
            }
        }
    ) {
        if (!isEditing) {
            Text(
                text = "El usuario debe tener una cuenta registrada en la app antes de poder vincularlo al establecimiento.",
                color = PrimaryOrange,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .background(SoftOrangeBg, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            )
            if (invitationToken.isNotEmpty()) {
                Surface(
                    color = SoftOrangeBg,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Invitación enviada", fontWeight = FontWeight.Bold, color = DarkGray)
                        Text(
                            text = invitationToken,
                            color = PrimaryOrange,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        OutlinedButton(
                            onClick = { clipboardManager.setText(AnnotatedString(invitationToken)) }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Copiar código")
                        }
                        Text(
                            "También se envió al correo. Caduca en 24 horas.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MediumGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        } else {
            Text(
                text = "Editando el rol de: ${empleado?.nombre}",
                color = DarkGray,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        InputField(
            label = "Correo electrónico del usuario",
            value = correo,
            onValueChange = { correo = it },
            placeholder = "ejemplo@correo.com",
            leadingIcon = Icons.Default.Email,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            //enabled = !isEditing
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Sucursal",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = DarkGray
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        Box {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { if (!isEditing) expandedEstablecimiento = true },
                border = BorderStroke(1.dp, ExtraLightGray)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = PrimaryOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = sucursalActual,
                        color = if (selectedEstablecimientoId.isEmpty()) MediumGray else DarkGray,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    if (!isEditing) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MediumGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            DropdownMenu(
                expanded = expandedEstablecimiento,
                onDismissRequest = { expandedEstablecimiento = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(Color.White)
            ) {
                establecimientos.forEach { sucursal ->
                    DropdownMenuItem(
                        text = { Text(sucursal.nombre) },
                        onClick = {
                            selectedEstablecimientoId = sucursal.id
                            expandedEstablecimiento = false
                            localError = ""
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Rol en el establecimiento",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = DarkGray
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RoleChip(
                label = "Cajero",
                isSelected = rol == "cajero",
                onClick = { rol = "cajero" },
                modifier = Modifier.weight(1f)
            )
            RoleChip(
                label = "Cocina",
                isSelected = rol == "cocina",
                onClick = { rol = "cocina" },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RoleChip(
                label = "Supervisor",
                isSelected = rol == "supervisor",
                onClick = { rol = "supervisor" },
                modifier = Modifier.weight(1f)
            )
            RoleChip(
                label = "Admin Local",
                isSelected = rol == "admin",
                onClick = { rol = "admin" },
                modifier = Modifier.weight(1f)
            )
        }

        if (localError.isNotEmpty() || uiState is FormState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            val errorMessage = if (localError.isNotEmpty()) localError else (uiState as FormState.Error).message
            Text(
                text = errorMessage,
                color = TrafficRed,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun RoleChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) PrimaryOrange else Color.White)
            .border(1.dp, if (isSelected) Color.Transparent else BorderGray, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else MediumGray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}