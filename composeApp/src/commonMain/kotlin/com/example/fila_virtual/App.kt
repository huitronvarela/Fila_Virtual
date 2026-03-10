package com.example.fila_virtual

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

// Importaciones de tu nueva estructura
import com.example.fila_virtual.core.theme.FilaVirtualTheme
import com.example.fila_virtual.core.navigation.Screens
import com.example.fila_virtual.auth.animacion.AuthContainer
import com.example.fila_virtual.features.user.home.HomeScreen

// Firebase Auth para saber si ya hay una sesión activa
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun App(onGoogleSignIn: () -> Unit = {}) {
    FilaVirtualTheme {
        // Estado para la pantalla actual
        var currentScreen by remember { mutableStateOf<Screens?>(null) }
        val scope = rememberCoroutineScope()

        // Observar cambios en el estado de autenticación en tiempo real
        LaunchedEffect(Unit) {
            Firebase.auth.authStateChanged.collectLatest { user ->
                currentScreen = if (user != null) {
                    Screens.Home
                } else {
                    Screens.Login
                }
            }
        }

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            // Solo mostramos la UI cuando ya sabemos el estado de la sesión (evita saltos)
            currentScreen?.let { screen ->
                Crossfade(targetState = screen, label = "NavegacionPrincipal") { targetScreen ->
                    when (targetScreen) {
                        Screens.Home -> {
                            HomeScreen(
                                onLogout = {
                                    scope.launch {
                                        Firebase.auth.signOut()
                                        // No hace falta cambiar currentScreen aquí, 
                                        // authStateChanged lo detectará automáticamente.
                                    }
                                }
                            )
                        }
                        Screens.Splash -> { /* Tu splash screen si existe */ }
                        else -> {
                            AuthContainer(
                                currentScreen = targetScreen,
                                onNavigate = { newScreen -> currentScreen = newScreen },
                                onGoogleSignIn = onGoogleSignIn
                            )
                        }
                    }
                }
            }
        }
    }
}