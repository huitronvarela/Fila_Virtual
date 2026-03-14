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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fila_virtual.data.Usuario
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.stringResource
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
    
    val logoutSheetState = rememberModalBottomSheetState()
    val languageSheetState = rememberModalBottomSheetState()

    // Control de idioma local (Para persistencia real se necesita código por plataforma)
    var selectedLanguage by remember { mutableStateOf("Español") }

    val fotoUrl = usuario?.fotoUrl

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
                    Surface(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .border(3.dp, Color.White, CircleShape),
                        color = Color.LightGray,
                        shadowElevation = 8.dp,
                        shape = CircleShape
                    ) {
                        if (!fotoUrl.isNullOrEmpty() && fotoUrl.startsWith("https")) {
                            KamelImage(
                                resource = asyncPainterResource(data = fotoUrl),
                                contentDescription = "Foto",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                                onFailure = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White) }
                            )
                        } else {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(24.dp), tint = Color.White)
                        }
                    }
                    Surface(
                        modifier = Modifier.size(32.dp).clip(CircleShape).border(1.5.dp, Color.White, CircleShape),
                        color = Color.White,
                        tonalElevation = 4.dp
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.padding(7.dp), tint = orangeTheme)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(usuario?.nombre ?: "Usuario", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(usuario?.email ?: "email@ejemplo.com", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
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
            ProfileSectionTitle(stringResource(Res.string.profile_section_account))
            ProfileMenuItem(Icons.Default.Person, stringResource(Res.string.profile_personal_info))
            ProfileMenuItem(Icons.Default.LocationOn, stringResource(Res.string.profile_addresses))

            ProfileSectionTitle(stringResource(Res.string.profile_section_preferences))
            ProfileMenuItem(
                icon = Icons.Default.Language,
                title = stringResource(Res.string.profile_language),
                extraText = if (selectedLanguage == "Español") "🇲🇽 Español" else "🇺🇸 English",
                onClick = { showLanguageSheet = true }
            )
            ProfileMenuItem(Icons.Default.Notifications, stringResource(Res.string.profile_notifications))

            ProfileSectionTitle(stringResource(Res.string.profile_section_support))
            ProfileMenuItem(Icons.AutoMirrored.Filled.Help, stringResource(Res.string.profile_help))
            ProfileMenuItem(Icons.Default.Info, stringResource(Res.string.profile_about))

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
                    Text(stringResource(Res.string.profile_logout), color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // --- BOTTOM SHEETS UNIFORMES ---

    // Modal de Idioma
    if (showLanguageSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLanguageSheet = false },
            sheetState = languageSheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.size(72.dp).background(Color(0xFFFFF1EE), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = orangeTheme, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(stringResource(Res.string.profile_select_language), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))

                LanguageOption("🇲🇽", "Español", selectedLanguage == "Español") {
                    selectedLanguage = "Español"
                    showLanguageSheet = false
                }

                LanguageOption("🇺🇸", "English", selectedLanguage == "English") {
                    selectedLanguage = "English"
                    showLanguageSheet = false
                }

                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = { showLanguageSheet = false }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(Res.string.btn_cancel), color = Color.Gray)
                }
            }
        }
    }

    // Modal de Logout
    if (showLogoutSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLogoutSheet = false },
            sheetState = logoutSheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.size(72.dp).background(Color(0xFFFEF2F2), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(stringResource(Res.string.profile_logout_confirm), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Text(stringResource(Res.string.profile_logout_msg), color = Color.Gray, textAlign = TextAlign.Center, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { showLogoutSheet = false; onLogout() },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text(stringResource(Res.string.profile_logout), color = Color.White, fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = { showLogoutSheet = false }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(Res.string.btn_cancel), color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun LanguageOption(flag: String, name: String, isSelected: Boolean, onSelect: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelect() },
        color = if (isSelected) Color(0xFFFFF1EE) else Color.Transparent,
        border = if (isSelected) BorderStroke(1.dp, Color(0xFFFF5722)) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(flag, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Text(name, fontSize = 16.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) Color(0xFFFF5722) else Color.Black)
            Spacer(modifier = Modifier.weight(1f))
            if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFFFF5722), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        color = Color.Gray,
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    extraText: String? = null,
    onClick: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(40.dp).background(Color(0xFFFFF1EE), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = Color(0xFFFF5722), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, modifier = Modifier.weight(1f), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1C1E))
            if (extraText != null) {
                Text(text = extraText, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 8.dp))
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color(0xFFADB5BD), modifier = Modifier.size(16.dp))
        }
        HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = Color(0xFFF1F3F5), thickness = 1.dp)
    }
}
