package com.example.fila_virtual

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
import com.example.fila_virtual.features.user.UserViewModel

// Firebase Auth
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

@Composable
fun App(
    onGoogleSignIn: () -> Unit = {},
    onSignOut: () -> Unit = {},
    googleLoginSuccess: Boolean = false // <-- Recibimos el aviso de MainActivity
) {
    // MEJORA: Verificamos si Firebase ya tiene tu sesión guardada. Si sí, saltamos directo al Home.
    var currentScreen by remember {
        mutableStateOf<Screens>(
            if (Firebase.auth.currentUser != null) Screens.Home else Screens.Login
        )
    }
    val userViewModel = remember { UserViewModel() }

    // MAGIA: Si MainActivity nos avisa en vivo que el login fue exitoso, abrimos la puerta.
    LaunchedEffect(googleLoginSuccess) {
        if (googleLoginSuccess) {
            currentScreen = Screens.Home
        }
    }

    FilaVirtualTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

            when (currentScreen) {

                Screens.Login -> {
                    AuthContainer(
                        currentScreen = currentScreen,
                        onNavigate = { nuevaPantalla ->
                            currentScreen = nuevaPantalla
                        },
                        onGoogleSignIn = onGoogleSignIn
                    )
                }

                Screens.Home -> {
                    MainScreen(
                        viewModel = userViewModel,
                        onLogout = {
                            onSignOut()
                            currentScreen = Screens.Login
                        }
                    )
                }

                else -> { /* Manejo de otras pantallas */ }
            }
        }
    }
}