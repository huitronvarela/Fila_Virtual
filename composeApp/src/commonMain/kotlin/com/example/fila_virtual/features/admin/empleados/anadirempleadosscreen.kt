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
import androidx.compose.ui.unit.sp
import com.example.fila_virtual.features.admin.FormState

// Colores del diseño
private val PrimaryOrange = Color(0xFFF05A32)
private val LightGrayBg = Color(0xFFF5F7F9)
private val TextGray = Color(0xFF757575)
private val TrafficRed = Color(0xFFE53935)
private val DarkGray = Color(0xFF333333)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnadirEmpleadoScreen(
    establecimientoId: String = "",
    onBack: () -> Unit
) {
    // Instanciamos el ViewModel
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
                    color = TextGray,
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
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Entendido", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nuevo Empleado", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            // ZONA SCROLLABLE
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.BottomEnd) {
                    Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(LightGrayBg), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Añadir foto", tint = Color.LightGray, modifier = Modifier.size(32.dp))
                    }
                    Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(PrimaryOrange).clickable { /* Abrir galería */ }, contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("FOTO DE PERFIL", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(24.dp))

                CustomTextField(label = "Nombre Completo", placeholder = "Ej. Carlos Rodríguez", value = nombre, onValueChange = { nombre = it })
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(label = "Correo Electrónico", placeholder = "carlos@altoque.com", value = correo, onValueChange = { correo = it })
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(label = "Teléfono", placeholder = "+52 300 000 0000", value = telefono, onValueChange = { telefono = it })
                Spacer(modifier = Modifier.height(24.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Cargo / Rol", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        RoleChip("Chef", rolSeleccionado == "Chef") { rolSeleccionado = "Chef" }
                        RoleChip("Cashier", rolSeleccionado == "Cashier") { rolSeleccionado = "Cashier" }
                        RoleChip("Delivery", rolSeleccionado == "Delivery") { rolSeleccionado = "Delivery" }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        RoleChip("Manager", rolSeleccionado == "Manager") { rolSeleccionado = "Manager" }
                    }
                }

                // Mostrar mensajes de error si existen
                if (uiState is FormState.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = (uiState as FormState.Error).message,
                        color = TrafficRed,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // ZONA FIJA (Botón)
            Surface(color = Color.White, shadowElevation = 16.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        // AQUÍ CONECTAMOS EL BOTÓN AL VIEWMODEL
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
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        shape = RoundedCornerShape(12.dp),
                        enabled = uiState !is FormState.Loading // Bloquear botón mientras carga
                    ) {
                        if (uiState is FormState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Registrar Empleado", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Al registrar este empleado, se le enviará automáticamente una invitación a su correo electrónico corporativo.",
                        color = TextGray, fontSize = 11.sp, textAlign = TextAlign.Center, lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

// ==============================================================================
// COMPONENTES REUTILIZABLES
// ==============================================================================

@Composable
fun CustomTextField(label: String, placeholder: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = PrimaryOrange,
                unfocusedContainerColor = LightGrayBg,
                focusedContainerColor = LightGrayBg,
            ),
            singleLine = true
        )
    }
}

@Composable
fun RoleChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) PrimaryOrange else Color.White)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(text = label, color = if (isSelected) Color.White else TextGray, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp)
    }
}