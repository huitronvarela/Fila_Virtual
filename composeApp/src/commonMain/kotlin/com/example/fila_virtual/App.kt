package com.example.fila_virtual

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import kotlinx.coroutines.launch

// Importaciones de Firebase (GitLive)
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore // <-- Importación de Firestore

// Importación para el empaquetador de datos
import kotlinx.serialization.Serializable

// Importación de tu logo
import fila_virtual.composeapp.generated.resources.Res
import fila_virtual.composeapp.generated.resources.logo

// --- COLORES DEL DISEÑO ---
object AppColors {
    val primaryOrange = Color(0xFFF15A24)
    val darkGray = Color(0xFF333333)
    val foodGreen = Color(0xFF2E7D32)
}

// --- MODELO DE DATOS DE FIRESTORE ---
@Serializable
data class Usuario(
    val nombre: String,
    val telefono: String,
    val email: String,
    val tipoUsuario: String,
    val billetera: String = "",
    val fechaRegistro: String
)

// --- GESTIÓN DE PANTALLAS ---
enum class Screens {
    Splash,
    Login,
    Register,
    Home
}

@Composable
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf(Screens.Splash) }

        Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
            when (currentScreen) {
                Screens.Splash -> SplashScreen(
                    onGetStarted = { currentScreen = Screens.Login },
                    onSignUp = { currentScreen = Screens.Register }
                )
                Screens.Login -> LoginScreen(onNavigate = { screen -> currentScreen = screen })
                Screens.Register -> RegisterScreen(onNavigate = { screen -> currentScreen = screen })
                Screens.Home -> HomeScreen(onLogout = { currentScreen = Screens.Splash })
            }
        }
    }
}

// ====================================================
// 0. PANTALLA DE INICIO (HOME)
// ====================================================
@Composable
fun HomeScreen(onLogout: () -> Unit) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("¡Bienvenido a Fila Virtual!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AppColors.primaryOrange)
        Spacer(modifier = Modifier.height(16.dp))

        val currentUser = Firebase.auth.currentUser
        Text("Sesión iniciada como:", color = Color.Gray)
        Text(text = currentUser?.email ?: "Usuario desconocido", fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        Firebase.auth.signOut()
                        onLogout()
                    } catch (e: Exception) {
                        println("Error al cerrar sesión: ${e.message}")
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.darkGray)
        ) {
            Text("Cerrar Sesión", color = Color.White)
        }
    }
}

// ====================================================
// 1. PANTALLA DE BIENVENIDA (SPLASH)
// ====================================================
@Composable
fun SplashScreen(onGetStarted: () -> Unit, onSignUp: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.padding(top = 16.dp)) {
            Text(text = "Fila ", color = AppColors.darkGray, fontWeight = FontWeight.Bold, fontSize = 28.sp)
            Text(text = "Virtual", color = AppColors.foodGreen, fontWeight = FontWeight.Bold, fontSize = 28.sp)
        }
        Spacer(modifier = Modifier.height(32.dp))
        Image(painter = painterResource(Res.drawable.logo), contentDescription = "Logo", modifier = Modifier.fillMaxWidth().height(250.dp), contentScale = ContentScale.Fit)
        Spacer(modifier = Modifier.weight(1f))
        ActionButton(text = "Comenzar", onClick = onGetStarted, isLoading = false)
        Spacer(modifier = Modifier.height(32.dp))
        SocialLoginBlock("f", "G", "a")
        Spacer(modifier = Modifier.height(24.dp))
        NavigationLink(textMain = "¿No tienes una cuenta? ", textLink = "Regístrate", onClick = onSignUp)
    }
}

// ====================================================
// 2. PANTALLA DE INICIO DE SESIÓN (LOGIN)
// ====================================================
@Composable
fun LoginScreen(onNavigate: (Screens) -> Unit) {
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(AppColors.primaryOrange)) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
            Image(painter = painterResource(Res.drawable.logo), contentDescription = "Logo", modifier = Modifier.size(160.dp))
        }

        Surface(modifier = Modifier.fillMaxWidth().weight(2.5f), shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp), color = Color.White) {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {

                InputField(label = "Correo Electrónico", value = email, onValueChange = { email = it; errorMessage = "" }, placeholder = "Ingresa tu correo")
                Spacer(modifier = Modifier.height(16.dp))

                PasswordInputField(label = "Contraseña", value = password, onValueChange = { password = it; errorMessage = "" }, passwordVisible = passwordVisible, onVisibilityChange = { passwordVisible = it }, placeholder = "Ingresa tu contraseña")
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "¿Olvidaste tu contraseña?", color = AppColors.primaryOrange, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.End).clickable { /* Recuperar contraseña */ })
                Spacer(modifier = Modifier.height(16.dp))

                TermsCheckbox(termsAccepted = termsAccepted, onAcceptedChange = { termsAccepted = it })

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp))
                } else {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                ActionButton(text = "Iniciar sesión", isLoading = isLoading) {
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
                SocialLoginBlock("f", "G", "a")
                Spacer(modifier = Modifier.weight(1f))
                NavigationLink(textMain = "¿No tienes una cuenta? ", textLink = "Regístrate") { onNavigate(Screens.Register) }
            }
        }
    }
}

// ====================================================
// 3. PANTALLA DE REGISTRO (SIGN UP)
// ====================================================
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
                            // 1. Crear el usuario en Authentication
                            val authResult = Firebase.auth.createUserWithEmailAndPassword(email.trim(), password.trim())
                            val uid = authResult.user?.uid

                            if (uid != null) {
                                // 2. Armar nuestro "paquete" con los datos del usuario
                                val nuevoUsuario = Usuario(
                                    nombre = nombre.trim(),
                                    telefono = telefono.trim(),
                                    email = email.trim(),
                                    tipoUsuario = "ALUMNO",
                                    billetera = "",
                                    fechaRegistro = "01 de marzo de 2026"
                                )

                                // 3. Aventarlo a Firestore
                                Firebase.firestore.collection("usuarios").document(uid).set(nuevoUsuario)
                            }

                            // 4. Mandarlo a la pantalla de inicio
                            onNavigate(Screens.Home)

                        } catch (e: Exception) {
                            // AQUÍ ESTÁ EL CAMBIO IMPORTANTE PARA VER EL ERROR:
                            errorMessage = e.message ?: "Ocurrió un error desconocido."
                            println("Register Error: ${e.message}")
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

// ====================================================
// COMPONENTES REUTILIZABLES
// ====================================================

@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.Bold, color = AppColors.darkGray, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(value = value, onValueChange = onValueChange, placeholder = { Text(placeholder, color = Color.LightGray) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true)
    }
}

@Composable
fun PasswordInputField(label: String, value: String, onValueChange: (String) -> Unit, passwordVisible: Boolean, onVisibilityChange: (Boolean) -> Unit, placeholder: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.Bold, color = AppColors.darkGray, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value, onValueChange = onValueChange, placeholder = { Text(placeholder, color = Color.LightGray) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Ocultar" else "Mostrar"
                IconButton(onClick = { onVisibilityChange(!passwordVisible) }) { Icon(imageVector = image, contentDescription = description, tint = Color.Gray) }
            }
        )
    }
}

@Composable
fun TermsCheckbox(termsAccepted: Boolean, onAcceptedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = termsAccepted, onCheckedChange = onAcceptedChange, colors = CheckboxDefaults.colors(checkedColor = AppColors.primaryOrange))
        val annotatedString = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Gray)) { append("He leído y acepto el ") }
            withStyle(style = SpanStyle(color = AppColors.primaryOrange, fontWeight = FontWeight.Bold)) { append("Acuerdo de Usuario") }
            withStyle(style = SpanStyle(color = Color.Gray)) { append(" y la\n") }
            withStyle(style = SpanStyle(color = AppColors.primaryOrange, fontWeight = FontWeight.Bold)) { append("Política de Privacidad") }
        }
        Text(text = annotatedString, fontSize = 13.sp, modifier = Modifier.padding(start = 4.dp), lineHeight = 18.sp)
    }
}

@Composable
fun ActionButton(text: String, isLoading: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AppColors.primaryOrange),
        shape = RoundedCornerShape(25.dp),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
        } else {
            Text(text = text, fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SocialLoginBlock(social1: String, social2: String, social3: String) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
            Text(text = "Ingresar con", modifier = Modifier.padding(horizontal = 16.dp), color = AppColors.darkGray, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            SocialButton(social1, Color(0xFF1877F2))
            Spacer(modifier = Modifier.width(24.dp))
            SocialButton(social2, Color(0xFFDB4437))
            Spacer(modifier = Modifier.width(24.dp))
            SocialButton(social3, Color.Black)
        }
    }
}

@Composable
fun SocialButton(text: String, color: Color) {
    Box(modifier = Modifier.size(54.dp).background(Color.White, CircleShape).border(1.dp, Color.LightGray, CircleShape).clickable { /* Lógica social */ }, contentAlignment = Alignment.Center) {
        Text(text = text, color = color, fontSize = 26.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun NavigationLink(textMain: String, textLink: String, onClick: () -> Unit) {
    Row(modifier = Modifier.padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = textMain, color = Color.Gray)
        Text(text = textLink, color = AppColors.primaryOrange, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.clickable(onClick = onClick))
    }
}