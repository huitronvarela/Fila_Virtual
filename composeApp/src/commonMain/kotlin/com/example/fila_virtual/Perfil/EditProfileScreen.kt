package com.example.fila_virtual.features.user

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fila_virtual.Perfil.ProfileHeader
import com.example.fila_virtual.components.InputField
import com.example.fila_virtual.core.PhoneVisualTransformation
import com.example.fila_virtual.core.isValidName
import com.example.fila_virtual.core.isValidPhone
import com.example.fila_virtual.core.theme.*
import com.example.fila_virtual.data.Usuario
import com.example.fila_virtual.core.LocalWindowSize
import com.example.fila_virtual.core.PermissionType
import com.example.fila_virtual.core.rememberPermissionsManager
import fila_virtual.composeapp.generated.resources.Res
import fila_virtual.composeapp.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    usuario: Usuario?,
    viewModel: UserViewModel,
    onBack: () -> Unit
) {
    val windowSize = LocalWindowSize.current
    val focusManager = LocalFocusManager.current
    val permissions = rememberPermissionsManager()

    var nombre by remember { mutableStateOf(usuario?.nombre ?: "") }
    var telefono by remember { mutableStateOf(if (usuario?.telefono == "Sin registrar") "" else (usuario?.telefono ?: "")) }
    val email = usuario?.email ?: ""
    
    val scope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }
    var showConfirmSheet by remember { mutableStateOf(false) }
    var showPhotoSheet by remember { mutableStateOf(false) }

    // MODAL DE CONFIRMACIÓN (Desde abajo)
    if (showConfirmSheet) {
        ModalBottomSheet(
            onDismissRequest = { showConfirmSheet = false },
            containerColor = LightSurface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Confirmar cambios", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text("¿Estás seguro de que deseas guardar los cambios realizados?", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        showConfirmSheet = false
                        scope.launch {
                            isSaving = true
                            viewModel.updateProfile(nombre, telefono, usuario?.fotoUrl) { success ->
                                if (success) onBack()
                                isSaving = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    shape = RoundedCornerShape(16.dp)
                ) { Text("Confirmar y Guardar", color = Color.White, fontWeight = FontWeight.Bold) }
            }
        }
    }

    // MODAL DE FOTO (Cámara/Galería)
    if (showPhotoSheet) {
        ModalBottomSheet(onDismissRequest = { showPhotoSheet = false }) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
                ListItem(
                    headlineContent = { Text("Tomar foto") },
                    leadingContent = { Icon(Icons.Default.CameraAlt, contentDescription = null, tint = PrimaryOrange) },
                    modifier = Modifier.clickable {
                        permissions.askPermission(PermissionType.CAMERA) { granted ->
                            if (granted) { /* Lógica Cámara */ }
                            showPhotoSheet = false
                        }
                    }
                )
                ListItem(
                    headlineContent = { Text("Elegir de la galería") },
                    leadingContent = { Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = PrimaryOrange) },
                    modifier = Modifier.clickable {
                        permissions.askPermission(PermissionType.GALLERY) { granted ->
                            if (granted) { /* Lógica Galería */ }
                            showPhotoSheet = false
                        }
                    }
                )
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Editar Perfil", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = TrafficRed) }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // FOTO CON BOTÓN EDITAR
            Box(contentAlignment = Alignment.BottomEnd) {
                ProfileHeader(usuario)
                Surface(
                    onClick = { showPhotoSheet = true },
                    modifier = Modifier.size(36.dp).offset(x = 4.dp, y = 4.dp),
                    shape = CircleShape,
                    color = PrimaryOrange,
                    border = BorderStroke(2.dp, Color.White)
                ) { Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp)) }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // INPUTS
            InputField(
                label = "Nombre completo",
                value = nombre,
                onValueChange = { if (isValidName(it)) nombre = it },
                placeholder = stringResource(Res.string.placeholder_name),
                leadingIcon = Icons.Filled.Person,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                isError = nombre.isNotEmpty() && nombre.length < 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            InputField(
                label = stringResource(Res.string.label_phone),
                value = telefono,
                onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) telefono = it },
                placeholder = stringResource(Res.string.placeholder_phone),
                leadingIcon = Icons.Filled.Phone,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                visualTransformation = PhoneVisualTransformation(),
                isError = telefono.isNotEmpty() && !isValidPhone(telefono)
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // EMAIL BLOQUEADO (Solo ver)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Correo electrónico", style = MaterialTheme.typography.labelMedium, color = MediumGray, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email, onValueChange = {}, readOnly = true, enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = BorderGray,
                        disabledContainerColor = ExtraLightGray,
                        disabledTextColor = MediumGray
                    ),
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MediumGray) }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
            
            Button(
                onClick = { showConfirmSheet = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                enabled = !isSaving
            ) {
                if (isSaving) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("Guardar Cambios", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
