package com.example.fila_virtual.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

// Importaciones de Firebase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

// Importaciones de tu Logo
import fila_virtual.composeapp.generated.resources.Res
import fila_virtual.composeapp.generated.resources.*

// Importaciones de tu nueva arquitectura
import com.example.fila_virtual.core.components.*
import com.example.fila_virtual.core.navigation.Screens

@Composable
fun LoginScreen(onNavigate: (Screens) -> Unit) {
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
            Image(painter = painterResource(Res.drawable.logo), contentDescription = "Logo", modifier = Modifier.size(160.dp))
        }

        Surface(
            modifier = Modifier.fillMaxWidth().weight(2.5f), 
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp), 
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {

                InputField(
                    label = stringResource(Res.string.label_email), 
                    value = email, 
                    onValueChange = { email = it; errorMessage = "" }, 
                    placeholder = stringResource(Res.string.placeholder_email)
                )
                Spacer(modifier = Modifier.height(16.dp))

                PasswordInputField(
                    label = stringResource(Res.string.label_password), 
                    value = password, 
                    onValueChange = { password = it; errorMessage = "" }, 
                    passwordVisible = passwordVisible, 
                    onVisibilityChange = { passwordVisible = it }, 
                    placeholder = stringResource(Res.string.placeholder_password)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(Res.string.forgot_password), 
                    color = MaterialTheme.colorScheme.primary, 
                    fontWeight = FontWeight.Bold, 
                    modifier = Modifier.align(Alignment.End).clickable { /* Recuperar contraseña */ }
                )
                Spacer(modifier = Modifier.height(16.dp))

                TermsCheckbox(termsAccepted = termsAccepted, onCheckedChange = { termsAccepted = it })

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp))
                } else {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                ActionButton(text = stringResource(Res.string.btn_login), isLoading = isLoading) {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Por favor llena todos los campos"
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
                            Firebase.auth.signInWithEmailAndPassword(email.trim(), password.trim())
                            onNavigate(Screens.Home)
                        } catch (e: Exception) {
                            errorMessage = "Error: Correo o contraseña incorrectos."
                            println("Login Error: ${e.message}")
                        } finally {
                            isLoading = false
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                SocialLoginBlock()
                Spacer(modifier = Modifier.weight(1f))
                NavigationLink(
                    textMain = stringResource(Res.string.no_account), 
                    textLink = stringResource(Res.string.btn_register)
                ) { onNavigate(Screens.Register) }
            }
        }
    }
}