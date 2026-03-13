package com.example.fila_virtual.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fila_virtual.data.Usuario
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun ProfileComponent(
    usuario: Usuario?,
    onLogout: () -> Unit
) {
    val orangeGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFFF5722), Color(0xFFFF8A65))
    )
    
    // Estado para mostrar el diálogo de confirmación
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // --- HEADER NARANJA ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(orangeGradient)
                .padding(top = 40.dp, bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Avatar con imagen real o placeholder
                Box(contentAlignment = Alignment.BottomEnd) {
                    Surface(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape),
                        color = Color.LightGray
                    ) {
                        if (!usuario?.fotoUrl.isNullOrEmpty()) {
                            KamelImage(
                                resource = asyncPainterResource(usuario!!.fotoUrl!!),
                                contentDescription = "Foto de perfil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.padding(20.dp),
                                tint = Color.White
                            )
                        }
                    }
                    // Botoncito de cámara
                    Surface(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.White, CircleShape),
                        color = Color.White,
                        tonalElevation = 4.dp
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.padding(6.dp),
                            tint = Color(0xFFFF5722)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = usuario?.nombre ?: "Nombre de Usuario",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = usuario?.email ?: "usuario@email.com",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "MIEMBRO DESDE 2024", 
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // --- CUERPO DEL PERFIL ---
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            
            ProfileSectionTitle("MI CUENTA")
            ProfileMenuItem(Icons.Default.Person, "Información Personal")
            ProfileMenuItem(Icons.Default.LocationOn, "Direcciones Guardadas")
            ProfileMenuItem(Icons.Default.Payment, "Métodos de Pago")

            ProfileSectionTitle("PREFERENCIAS")
            ProfileMenuItem(Icons.Default.Notifications, "Notificaciones")
            ProfileMenuItem(Icons.Default.Language, "Idioma", "Español")

            ProfileSectionTitle("SOPORTE Y LEGAL")
            ProfileMenuItem(Icons.AutoMirrored.Filled.Help, "Ayuda y Soporte")
            ProfileMenuItem(Icons.Default.Gavel, "Términos y Condiciones")
            ProfileMenuItem(Icons.Default.Info, "Acerca de Fila Virtual")

            Spacer(modifier = Modifier.height(32.dp))

            // Botón Cerrar Sesión con Borde Rojo y Confirmación
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(1.dp, Color(0xFFD32F2F), RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showLogoutDialog = true }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cerrar Sesión",
                    color = Color(0xFFD32F2F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // --- DIÁLOGO DE CONFIRMACIÓN ---
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(text = "Cerrar Sesión") },
            text = { Text(text = "¿Estás seguro de que quieres salir de tu cuenta?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Sí, salir", color = Color(0xFFFF5722), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        color = Color.Gray,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    extraText: String? = null
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Acción */ }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color(0xFFFF5722),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            if (extraText != null) {
                Text(
                    text = extraText,
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color(0xFFEEEEEE),
                modifier = Modifier.size(14.dp)
            )
        }
        HorizontalDivider(color = Color(0xFFF5F5F7), thickness = 1.dp)
    }
}
