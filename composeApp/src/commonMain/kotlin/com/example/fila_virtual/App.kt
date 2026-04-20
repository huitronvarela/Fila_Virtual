package com.example.fila_virtual

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier


// Importaciones de tu estructura
import com.example.fila_virtual.core.theme.FilaVirtualTheme
import com.example.fila_virtual.navigation.Screens
import com.example.fila_virtual.auth.animacion.AuthContainer
import com.example.fila_virtual.features.user.MainScreen
import com.example.fila_virtual.core.LocalWindowSize
import com.example.fila_virtual.core.WindowSize

// Firebase Auth
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun App(
    onGoogleSignIn: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    FilaVirtualTheme {
        BoxWithConstraints {
            val windowSize = WindowSize(maxWidth, maxHeight)
            
            CompositionLocalProvider(LocalWindowSize provides windowSize) {
                // Mantenemos tu lógica de inicio persistente
                val startScreen = remember { if (Firebase.auth.currentUser != null) Screens.Home else Screens.Login }
                var currentScreen by remember { mutableStateOf(startScreen) }

                val scope = rememberCoroutineScope()

                // FIX PARA GOOGLE: Escuchamos el cambio de sesión
                LaunchedEffect(Unit) {
                    Firebase.auth.authStateChanged.collectLatest { user ->
                        if (user != null && currentScreen == Screens.Login) {
                            currentScreen = Screens.Home
                        }
                    }
                }

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    when (currentScreen) {
                        Screens.Login, Screens.Register -> {
                            AuthContainer(
                                currentScreen = currentScreen,
                                onNavigate = { newScreen -> currentScreen = newScreen },
                                onGoogleSignIn = onGoogleSignIn
                            )
                        }
                        Screens.Home -> {
                            MainScreen(
                                onLogout = {
                                    scope.launch {
                                        onSignOut()
                                        currentScreen = Screens.Login
                                    }
                                }
                            )
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}