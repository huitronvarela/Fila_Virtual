package com.example.fila_virtual.Perfil

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fila_virtual.data.Usuario
import com.example.fila_virtual.features.user.EditProfileScreen
import com.example.fila_virtual.features.user.UserViewModel
import com.example.fila_virtual.core.theme.*
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileComponent(
    usuario: Usuario?,
    viewModel: UserViewModel,
    onLogout: () -> Unit
) {
    // Estados para los Bottom Sheets
    var showLogoutSheet by remember { mutableStateOf(false) }
    var showLanguageSheet by remember { mutableStateOf(false) }
    var showEditProfileScreen by remember { mutableStateOf(false) }

    val logoutSheetState = rememberModalBottomSheetState()
    val languageSheetState = rememberModalBottomSheetState()

    var selectedLanguage by remember { mutableStateOf("Español") }

    if (showEditProfileScreen) {
        EditProfileScreen(
            usuario = usuario,
            viewModel = viewModel,
            onBack = { showEditProfileScreen = false }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
    ) {
        Text(
            text = "Perfil",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 32.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ProfileHeader(usuario)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = usuario?.nombre ?: "Usuario",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1A1C1E)
                    )
                    Text(
                        text = usuario?.email ?: "email@ejemplo.com",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        color = LightBackground,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, LightGray)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cuenta Verificada", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ProfileSectionCard(icon = Icons.Default.PersonOutline, title = "Información Personal", iconTint = PrimaryOrange) {
                ProfileOptionItem(
                    icon = Icons.Default.Edit,
                    title = "Editar Perfil",
                    onClick = { showEditProfileScreen = true }
                )
            }

            ProfileSectionCard(icon = Icons.Default.Security, title = "Seguridad", iconTint = PrimaryOrange) {
                ProfileOptionItem(
                    icon = Icons.Default.Lock,
                    title = "Configuración de Seguridad"
                )
            }

            ProfileSectionCard(icon = Icons.Default.MoreHoriz, title = "Otros", iconTint = PrimaryOrange) {
                ProfileOptionItem(
                    icon = Icons.AutoMirrored.Filled.Help,
                    title = "Centro de Ayuda"
                )
                ProfileOptionItem(
                    icon = Icons.Default.Description,
                    title = "Términos y Condiciones"
                )
                ProfileOptionItem(
                    icon = Icons.Default.Translate,
                    title = "Idioma",
                    extraText = if (selectedLanguage == "Español") "ES" else "EN",
                    onClick = { showLanguageSheet = true }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showLogoutSheet = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TrafficRed)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showLanguageSheet) {
        ModalBottomSheet(onDismissRequest = { showLanguageSheet = false }, sheetState = languageSheetState, containerColor = LightSurface) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
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
        ModalBottomSheet(onDismissRequest = { showLogoutSheet = false }, sheetState = logoutSheetState, containerColor = LightSurface) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(72.dp).background(SoftRedBg, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = TrafficRed, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text("¿Cerrar sesión?", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { showLogoutSheet = false; onLogout() }, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = TrafficRed)) {
                    Text("Sí, cerrar sesión", color = Color.White, fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = { showLogoutSheet = false }, modifier = Modifier.fillMaxWidth()) { Text("Cancelar", color = Color.Black) }
            }
        }
    }
}

@Composable
fun ProfileSectionCard(icon: ImageVector, title: String, iconTint: Color, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = LightSurface)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
            }
            content()
        }
    }
}

@Composable
fun ProfileOptionItem(icon: ImageVector, title: String, extraText: String? = null, onClick: () -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { onClick() }.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(40.dp).background(LightBackground, CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, modifier = Modifier.weight(1f), fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        if (extraText != null) Text(text = extraText, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 8.dp))
        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
    }
}

@Composable
fun ProfileHeader(usuario: Usuario?) {
    val fotoUrl = usuario?.fotoUrl
    Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(SoftOrangeBg), contentAlignment = Alignment.Center) {
        if (!fotoUrl.isNullOrBlank()) {
            KamelImage(resource = asyncPainterResource(fotoUrl), contentDescription = "Foto", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop, onFailure = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(50.dp), tint = PrimaryOrange) })
        } else {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(50.dp), tint = PrimaryOrange)
        }
    }
}

@Composable
fun LanguageOption(flag: String, name: String, isSelected: Boolean, onSelect: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(12.dp)).clickable { onSelect() }, color = if (isSelected) SoftOrangeBg else Color.Transparent, border = if (isSelected) BorderStroke(1.dp, PrimaryOrange) else null) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(flag, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Text(name, fontSize = 16.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) PrimaryOrange else Color.Black)
            Spacer(modifier = Modifier.weight(1f))
            if (isSelected) Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(20.dp))
        }
    }
}
