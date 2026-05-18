package com.example.fila_virtual.features.admin.empleados

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fila_virtual.core.theme.*
import com.example.fila_virtual.features.admin.FormState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AnadirEmpleadoScreen(
    establecimientoId: String = "",
    onBack: () -> Unit
) {
    val viewModel = remember { EmpleadoViewModel() }
    val uiState by viewModel.uiState.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var rolSeleccionado by remember { mutableStateOf("Chef") }
    var showSuccessSheet by remember { mutableStateOf(false) }

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
                    text = "¡Empleado Registrado!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkGray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Se ha enviado una invitación a $correo para que pueda unirse a AlToque.",
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Nuevo Empleado",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Zona scrollable
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Avatar selector
                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(LightGray),
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

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "FOTO DE PERFIL",
                    color = MediumGray,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = androidx.compose.ui.unit.TextUnit(
                        1f, androidx.compose.ui.unit.TextUnitType.Sp
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))

                EmpleadoTextField(
                    label = "Nombre Completo",
                    placeholder = "Ej. Carlos Rodríguez",
                    value = nombre,
                    onValueChange = { nombre = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                EmpleadoTextField(
                    label = "Correo Electrónico",
                    placeholder = "carlos@altoque.com",
                    value = correo,
                    onValueChange = { correo = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                EmpleadoTextField(
                    label = "Teléfono",
                    placeholder = "+52 300 000 0000",
                    value = telefono,
                    onValueChange = { telefono = it }
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Selección de Cargo / Rol con FlowRow para adaptarse en pantallas pequeñas
                Column(modifier = Modifier.fillMaxWidth()) {
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
                        listOf("Chef", "Cashier", "Delivery", "Manager").forEach { rol ->
                            RoleChip(
                                label = rol,
                                isSelected = rolSeleccionado == rol,
                                onClick = { rolSeleccionado = rol }
                            )
                        }
                    }
                }

                // Mensaje de error
                if (uiState is FormState.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = (uiState as FormState.Error).message,
                        color = TrafficRed,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Zona fija — Botón de acción
            Surface(
                color = Color.White,
                shadowElevation = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            viewModel.guardarEmpleado(
                                establecimientoId = establecimientoId,
                                nombre = nombre,
                                correo = correo,
                                telefono = telefono,
                                rol = rolSeleccionado,
                                onSuccess = { showSuccessSheet = true }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        shape = RoundedCornerShape(12.dp),
                        enabled = uiState !is FormState.Loading
                    ) {
                        if (uiState is FormState.Loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                "Registrar Empleado",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Al registrar este empleado, se le enviará automáticamente una invitación a su correo electrónico corporativo.",
                        color = MediumGray,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        lineHeight = androidx.compose.ui.unit.TextUnit(
                            16f, androidx.compose.ui.unit.TextUnitType.Sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

// ==============================================================================
// COMPONENTES PRIVADOS DE ESTA PANTALLA
// ==============================================================================

@Composable
private fun EmpleadoTextField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = MediumGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = PrimaryOrange,
                unfocusedContainerColor = LightGray,
                focusedContainerColor = LightGray,
            ),
            singleLine = true
        )
    }
}

@Composable
private fun RoleChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) PrimaryOrange else ExtraLightGray)
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