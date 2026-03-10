package com.example.fila_virtual

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

// Importaciones de tu estructura
import com.example.fila_virtual.core.theme.FilaVirtualTheme
import com.example.fila_virtual.core.navigation.Screens
import com.example.fila_virtual.auth.animacion.AuthContainer // Asegúrate de que esta ruta sea correcta
import com.example.fila_virtual.features.user.home.HomeScreen

// Firebase Auth
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun App(onGoogleSignIn: () -> Unit = {}) {
    FilaVirtualTheme {
        // Mantenemos tu lógica de inicio persistente
        val startScreen = remember { if (Firebase.auth.currentUser != null) Screens.Home else Screens.Login }
        var currentScreen by remember { mutableStateOf(startScreen) }

        val scope = rememberCoroutineScope()

        // FIX PARA GOOGLE: Escuchamos el cambio de sesión
        LaunchedEffect(Unit) {
            Firebase.auth.authStateChanged.collectLatest { user ->
                // Solo redirigimos automáticamente si el usuario se loguea Y estamos en la pantalla de Login.
                if (user != null && currentScreen == Screens.Login) {
                    currentScreen = Screens.Home
                }
            }
        }

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

            // ¡ELIMINAMOS EL CROSSFADE GLOBAL!
            // Usamos un simple 'when'. Como Login y Register comparten el mismo bloque,
            // el AuthContainer no se destruye y la animación de la tarjeta vuelve a funcionar.
            when (currentScreen) {
                Screens.Login, Screens.Register -> {
                    AuthContainer(
                        currentScreen = currentScreen,
                        onNavigate = { newScreen -> currentScreen = newScreen },
                        onGoogleSignIn = onGoogleSignIn
                    )
                }
                Screens.Home -> {
                    HomeScreen(
                        onLogout = {
                            scope.launch {
                                Firebase.auth.signOut()
                                currentScreen = Screens.Login
                            }
                        }
                    )
                }
                Screens.Splash -> {
                    /* Aquí iría tu Splash Screen en el futuro */
                }
                else -> {}
            }
        }
    }
}