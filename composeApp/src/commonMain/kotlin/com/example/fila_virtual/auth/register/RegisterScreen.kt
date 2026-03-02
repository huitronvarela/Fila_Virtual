package com.example.fila_virtual.auth.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

// Importaciones de Firebase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

// Importaciones de tu Logo
import fila_virtual.composeapp.generated.resources.Res
import fila_virtual.composeapp.generated.resources.logo

// Importaciones de tu nueva arquitectura
import com.example.fila_virtual.core.theme.AppColors
import com.example.fila_virtual.core.components.*
import com.example.fila_virtual.Screens
import com.example.fila_virtual.Usuario // Traemos tu modelo de datos

@Composable
fun RegisterScreen(onNavigate: (Screens) -> Unit) {
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(AppColors.primaryOrange)) {
        Box(modifier = Modifier.fillMaxWidth().weight(0.8f), contentAlignment = Alignment.Center) {
            Image(painter = painterResource(Res.drawable.logo), contentDescription = "Logo", modifier = Modifier.size(120.dp))
        }

        Surface(modifier = Modifier.fillMaxWidth().weight(3.2f), shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp), color = Color.White) {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 24.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {

                InputField(label = "Nombre Completo", value = nombre, onValueChange = { nombre = it; errorMessage = "" }, placeholder = "Ej. María López")
                Spacer(modifier = Modifier.height(16.dp))

                InputField(label = "Teléfono", value = telefono, onValueChange = { telefono = it; errorMessage = "" }, placeholder = "Ej. 3141234567")
                Spacer(modifier = Modifier.height(16.dp))

                InputField(label = "Correo Electrónico", value = email, onValueChange = { email = it; errorMessage = "" }, placeholder = "Ingresa tu correo")
                Spacer(modifier = Modifier.height(16.dp))

                PasswordInputField(label = "Contraseña", value = password, onValueChange = { password = it; errorMessage = "" }, passwordVisible = passwordVisible, onVisibilityChange = { passwordVisible = it }, placeholder = "Mínimo 6 caracteres")
                Spacer(modifier = Modifier.height(16.dp))

                PasswordInputField(label = "Confirmar Contraseña", value = confirmPassword, onValueChange = { confirmPassword = it; errorMessage = "" }, passwordVisible = confirmPasswordVisible, onVisibilityChange = { confirmPasswordVisible = it }, placeholder = "Confirma tu contraseña")
                Spacer(modifier = Modifier.height(16.dp))

                TermsCheckbox(termsAccepted = termsAccepted, onAcceptedChange = { termsAccepted = it })

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp))
                } else {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                ActionButton(text = "Registrarse", isLoading = isLoading) {
                    if (nombre.isBlank() || telefono.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                        errorMessage = "Por favor llena todos los campos"
                        return@ActionButton
                    }
                    if (password != confirmPassword) {
                        errorMessage = "Las contraseñas no coinciden"
                        return@ActionButton
                    }
                    if (password.length < 6) {
                        errorMessage = "La contraseña debe tener al menos 6 caracteres"
                        return@ActionButton
                    }
                    if (!termsAccepted) {
                        errorMessage = "Debes aceptar los términos y condiciones"
                        return@ActionButton
                    }

                    scope.launch {
                        isLoading = true
                        errorMessage = ""
                        try {
                            val authResult = Firebase.auth.createUserWithEmailAndPassword(email.trim(), password.trim())
                            val uid = authResult.user?.uid

                            if (uid != null) {
                                val nuevoUsuario = Usuario(
                                    nombre = nombre.trim(),
                                    telefono = telefono.trim(),
                                    email = email.trim(),
                                    tipoUsuario = "ALUMNO",
                                    billetera = "",
                                    fechaRegistro = "01 de marzo de 2026"
                                )
                                Firebase.firestore.collection("usuarios").document(uid).set(nuevoUsuario)
                            }
                            onNavigate(Screens.Home)

                        } catch (e: Exception) {
                            errorMessage = e.message ?: "Ocurrió un error desconocido."
                        } finally {
                            isLoading = false
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                NavigationLink(textMain = "¿Ya tienes una cuenta? ", textLink = "Inicia sesión") { onNavigate(Screens.Login) }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}