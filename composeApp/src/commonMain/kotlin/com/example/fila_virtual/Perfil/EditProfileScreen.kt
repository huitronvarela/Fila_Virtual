package com.example.fila_virtual.features.user

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fila_virtual.Perfil.ProfileHeader
import com.example.fila_virtual.core.theme.*
import com.example.fila_virtual.data.Usuario
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    usuario: Usuario?,
    viewModel: UserViewModel,
    onBack: () -> Unit
) {
    var nombre by remember { mutableStateOf(usuario?.nombre ?: "") }
    var telefono by remember { mutableStateOf(if (usuario?.telefono == "Sin registrar") "" else (usuario?.telefono ?: "")) }
    val email = usuario?.email ?: "" // El correo suele ser de solo lectura en esta pantalla

    val scope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Editar Perfil",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        // Flecha en tono rojo/error según el diseño
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // --- FOTO DE PERFIL ---
            Box(contentAlignment = Alignment.BottomEnd) {
                ProfileHeader(usuario)

                // Icono de edición flotante
                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .offset(x = 4.dp, y = 4.dp), // Ajuste para que sobresalga un poco
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    border = BorderStroke(3.dp, MaterialTheme.colorScheme.background) // Borde para separar
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar Foto",
                        modifier = Modifier.padding(8.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- TARJETA DE FORMULARIO ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // 1. Campo: Nombre
                    CustomEditField(
                        label = "NOMBRE COMPLETO",
                        value = nombre,
                        onValueChange = { nombre = it },
                        icon = Icons.Default.Person
                    )

                    // 2. Campo: Correo (Solo lectura)
                    CustomEditField(
                        label = "CORREO ELECTRÓNICO",
                        value = email,
                        readOnly = true,
                        icon = Icons.Default.Email
                    )

                    // 3. Campo: Teléfono
                    CustomEditField(
                        label = "NÚMERO DE TELÉFONO",
                        value = telefono,
                        onValueChange = { telefono = it },
                        icon = Icons.Default.Phone
                    )

                    // 4. Campo: Contraseña (Simulada visualmente)
                    CustomEditField(
                        label = "CONTRASEÑA",
                        value = "••••••••",
                        readOnly = true,
                        icon = Icons.Default.Lock,
                        trailingContent = {
                            Surface(
                                color = BorderGray, // Gris claro para el botón interno
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { /* Lógica cambiar pass */ }
                            ) {
                                Text(
                                    text = "Cambiar",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DarkGray,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- BOTÓN GUARDAR ---
            Button(
                onClick = {
                    scope.launch {
                        isSaving = true
                        val uid = viewModel.userRepository.getCurrentUserUid()
                        if (uid != null) {
                            val success = viewModel.userRepository.updateUserData(uid, mapOf(
                                "nombre" to nombre,
                                "telefono" to telefono
                            ))
                            if (success) {
                                viewModel.loadUserData()
                                onBack()
                            }
                        }
                        isSaving = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Guardar Cambios",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// --- COMPONENTE REUTILIZABLE PARA LOS CAMPOS ---
@Composable
fun CustomEditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit = {},
    readOnly: Boolean = false,
    icon: ImageVector,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
        // Etiqueta superior
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MediumGray,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Caja del TextField
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ExtraLightGray, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DarkGray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))

            Box(modifier = Modifier.weight(1f)) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    readOnly = readOnly,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Si pasamos contenido extra (como el botón "Cambiar"), lo pinta a la derecha
            if (trailingContent != null) {
                Spacer(modifier = Modifier.width(8.dp))
                trailingContent()
            }
        }
    }
}