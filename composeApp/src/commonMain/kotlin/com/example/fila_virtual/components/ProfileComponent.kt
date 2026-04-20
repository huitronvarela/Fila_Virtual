package com.example.fila_virtual.components

import androidx.compose.foundation.BorderStroke
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
import io.ktor.http.Url
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fila_virtual.data.Usuario
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import fila_virtual.composeapp.generated.resources.Res
import fila_virtual.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileComponent(
    usuario: Usuario?,
    onLogout: () -> Unit
) {
    val orangeTheme = Color(0xFFFF5722)
    val orangeGradient = Brush.verticalGradient(
        colors = listOf(orangeTheme, Color(0xFFFF8A65))
    )

    // Estados para los Bottom Sheets
    var showLogoutSheet by remember { mutableStateOf(false) }
    var showLanguageSheet by remember { mutableStateOf(false) }
    var showEditProfileSheet by remember { mutableStateOf(false) }
    
    val logoutSheetState = rememberModalBottomSheetState()
    val languageSheetState = rememberModalBottomSheetState()
    val editProfileSheetState = rememberModalBottomSheetState()

    var selectedLanguage by remember { mutableStateOf("Español") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
    ) {
        // --- HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(orangeGradient)
                .padding(top = 40.dp, bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    // Implementación de ProfileHeader solicitada
                    ProfileHeader(usuario)

                    // BOTÓN DE ACCESO RÁPIDO PARA EDITAR PERFIL
                    Surface(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { showEditProfileSheet = true },
                        color = Color.White,
                        tonalElevation = 6.dp
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar perfil",
                            modifier = Modifier.padding(8.dp),
                            tint = orangeTheme
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                // Información básica centrada
                Text(
                    text = usuario?.nombre ?: "Usuario",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = usuario?.email ?: "email@ejemplo.com",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp
                )

                // Mostrar teléfono si existe
                if (!usuario?.telefono.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = usuario?.telefono ?: "",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp
                        )
                    }
                }

                // Mostrar tipo de usuario como un badge pequeño
                if (usuario != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = usuario.tipoUsuario,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // --- CUERPO ---
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .offset(y = (-20).dp)
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(bottom = 16.dp)
        ) {
            ProfileSectionTitle("Ajustes")
            ProfileMenuItem(
                icon = Icons.Default.Translate,
                title = "Traducción",
                extraText = if (selectedLanguage == "Español") "ES" else "EN",
                onClick = { showLanguageSheet = true }
            )
            ProfileMenuItem(Icons.Default.Notifications, "Notificaciones")

            ProfileSectionTitle("Soporte y Legal")
            ProfileMenuItem(Icons.AutoMirrored.Filled.Help, "Ayuda")
            ProfileMenuItem(Icons.Default.Gavel, "Términos y condiciones")
            ProfileMenuItem(Icons.Default.PrivacyTip, "Privacidad")
            ProfileMenuItem(Icons.Default.Info, "Acerca de")

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Salir
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { showLogoutSheet = true },
                color = Color(0xFFFEF2F2),
                border = BorderStroke(1.dp, Color(0xFFFEE2E2))
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Cerrar sesión", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // --- BOTTOM SHEETS ---

    // Bottom Sheet para Editar Perfil
    if (showEditProfileSheet) {
        ModalBottomSheet(
            onDismissRequest = { showEditProfileSheet = false },
            sheetState = editProfileSheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Editar Perfil", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))
                
                // Estos campos son demostrativos para la edición
                OutlinedTextField(
                    value = usuario?.nombre ?: "",
                    onValueChange = {},
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = usuario?.telefono ?: "",
                    onValueChange = {},
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { showEditProfileSheet = false },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = orangeTheme)
                ) {
                    Text("Guardar Cambios", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showLanguageSheet) {
        ModalBottomSheet(onDismissRequest = { showLanguageSheet = false }, sheetState = languageSheetState, containerColor = Color.White) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(72.dp).background(Color(0xFFFFF1EE), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Translate, contentDescription = null, tint = orangeTheme, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text("Seleccionar idioma", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))
                LanguageOption("🇲🇽", "Español", selectedLanguage == "Español") { selectedLanguage = "Español"; showLanguageSheet = false }
                LanguageOption("🇺🇸", "English", selectedLanguage == "English") { selectedLanguage = "English"; showLanguageSheet = false }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = { showLanguageSheet = false }, modifier = Modifier.fillMaxWidth()) { Text("Cancelar", color = Color.Gray) }
            }
        }
    }

    if (showLogoutSheet) {
        ModalBottomSheet(onDismissRequest = { showLogoutSheet = false }, sheetState = logoutSheetState, containerColor = Color.White) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(72.dp).background(Color(0xFFFEF2F2), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text("¿Cerrar sesión?", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { showLogoutSheet = false; onLogout() },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Cerrar sesión", color = Color.White, fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = { showLogoutSheet = false }, modifier = Modifier.fillMaxWidth()) { Text("Cancelar", color = Color.Black) }
            }
        }
    }
}

@Composable
fun ProfileHeader(usuario: Usuario?) {
    val fotoUrl = usuario?.fotoUrl
    
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(Color(0xFFFFF1EE)),
        contentAlignment = Alignment.Center
    ) {
        if (!fotoUrl.isNullOrBlank()) {
            KamelImage(
                resource = asyncPainterResource(fotoUrl),
                contentDescription = "Foto de perfil",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onFailure = { exception ->
                    println("ERROR DE KAMEL: ${exception.message}")
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = Color(0xFFFF5722)
                    )
                }
            )
        } else {
            // Si NO hay URL, mostramos el icono por defecto directamente
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(50.dp),
                tint = Color(0xFFFF5722)
            )
        }
    }
}

@Composable
fun LanguageOption(flag: String, name: String, isSelected: Boolean, onSelect: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(12.dp)).clickable { onSelect() },
        color = if (isSelected) Color(0xFFFFF1EE) else Color.Transparent,
        border = if (isSelected) BorderStroke(1.dp, Color(0xFFFF5722)) else null
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(flag, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Text(name, fontSize = 16.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) Color(0xFFFF5722) else Color.Black)
            Spacer(modifier = Modifier.weight(1f))
            if (isSelected) Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFFFF5722), modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun ProfileSectionTitle(title: String) {
    Text(text = title, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp))
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, extraText: String? = null, onClick: () -> Unit = {}) {
    Column(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(Color(0xFFFFF1EE), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = Color(0xFFFF5722), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, modifier = Modifier.weight(1f), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1C1E))
            if (extraText != null) Text(text = extraText, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color(0xFFADB5BD), modifier = Modifier.size(16.dp))
        }
        HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = Color(0xFFF1F3F5), thickness = 1.dp)
    }
}
