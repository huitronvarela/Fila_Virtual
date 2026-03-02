package com.example.fila_virtual

import com.example.fila_virtual.auth.register.RegisterScreen
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import kotlinx.coroutines.launch

// Importaciones de Firebase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

// Importación para el empaquetador de datos
import kotlinx.serialization.Serializable

// Importación de tu logo
import fila_virtual.composeapp.generated.resources.Res
import fila_virtual.composeapp.generated.resources.logo

// --- IMPORTACIONES DE TUS NUEVAS CARPETAS ---
import com.example.fila_virtual.core.theme.AppColors
import com.example.fila_virtual.core.components.*
import com.example.fila_virtual.auth.login.LoginScreen // <-- IMPORTANDO TU PANTALLA DE LOGIN

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

