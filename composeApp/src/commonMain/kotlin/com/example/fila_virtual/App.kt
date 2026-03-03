package com.example.fila_virtual

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

// Importaciones de tu nueva estructura
import com.example.fila_virtual.core.theme.FilaVirtualTheme
import com.example.fila_virtual.core.navigation.Screens
import com.example.fila_virtual.auth.login.LoginScreen
import com.example.fila_virtual.auth.register.RegisterScreen
import com.example.fila_virtual.auth.splash.SplashScreen
import com.example.fila_virtual.features.user.home.HomeScreen

// import com.example.fila_virtual.features.splash.SplashScreen
// import com.example.fila_virtual.features.home.HomeScreen

@Composable
fun App() {
    // Aplicamos tu nuevo tema que reacciona al sistema
    FilaVirtualTheme {
        // Un Surface toma el color de fondo correcto (claro u oscuro)
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            var currentScreen by remember { mutableStateOf(Screens.Splash) }

            // Navegador limpio
            when (currentScreen) {
                Screens.Splash -> SplashScreen(
                    onGetStarted = { currentScreen = Screens.Login },
                    onSignUp = { currentScreen = Screens.Register }
                )
                Screens.Login -> LoginScreen(
                    onNavigate = { screen -> currentScreen = screen }
                )
                Screens.Register -> RegisterScreen(
                    onNavigate = { screen -> currentScreen = screen }
                )
                Screens.Home -> HomeScreen(
                    onLogout = { currentScreen = Screens.Splash }
                )
            }
        }
    }
}