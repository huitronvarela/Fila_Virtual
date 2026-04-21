package com.example.fila_virtual.features.user

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fila_virtual.Perfil.ProfileHeader
import com.example.fila_virtual.data.Usuario
import com.example.fila_virtual.core.theme.*
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    usuario: Usuario?,
    viewModel: UserViewModel,
    onBack: () -> Unit
) {
    var nombre by remember { mutableStateOf(usuario?.nombre ?: "") }
    var telefono by remember { mutableStateOf(if (usuario?.telefono == "Sin registrar") "" else (usuario?.telefono ?: "")) }
    val email = usuario?.email ?: ""

    val scope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
    ) {
        // --- ENCABEZADO UNIFICADO ---
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Editar Perfil",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 16.dp)
            )

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Regresar",
                    tint = Color.Black
                )
            }
        }

        // --- CONTENIDO FIJO (Sin scroll) ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- FOTO DE PERFIL ---
            Box(contentAlignment = Alignment.BottomEnd) {
                ProfileHeader(usuario)
                Surface(
                    modifier = Modifier.size(34.dp).offset(x = 2.dp, y = 2.dp),
                    shape = CircleShape,
                    color = PrimaryOrange,
                    border = BorderStroke(3.dp, Color.White)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.padding(8.dp), tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- FORMULARIO EN TARJETA ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    CustomEditField(
                        label = "NOMBRE COMPLETO",
                        value = nombre,
                        onValueChange = { nombre = it },
                        icon = Icons.Default.Person
                    )
                    CustomEditField(
                        label = "CORREO ELECTRÓNICO",
                        value = email,
                        readOnly = true,
                        icon = Icons.Default.Email
                    )
                    CustomEditField(
                        label = "NÚMERO DE TELÉFONO",
                        value = telefono,
                        onValueChange = { telefono = it },
                        icon = Icons.Default.Phone
                    )
                    CustomEditField(
                        label = "CONTRASEÑA",
                        value = "••••••••",
                        readOnly = true,
                        icon = Icons.Default.Lock,
                        trailingContent = {
                            Surface(
                                color = LightGray,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { /* Lógica cambiar pass */ }
                            ) {
                                Text(
                                    text = "Cambiar",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkGray,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

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
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "Guardar Cambios", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

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
        Text(text = label, fontSize = 11.sp, color = MediumGray, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth().background(LightGray, RoundedCornerShape(16.dp)).padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MediumGray, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.weight(1f)) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    readOnly = readOnly,
                    textStyle = TextStyle(color = if (readOnly) MediumGray else Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Medium),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (trailingContent != null) {
                Spacer(modifier = Modifier.width(8.dp))
                trailingContent()
            }
        }
    }
}
