package com.example.fila_virtual.features.user.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun HomeScreen(onLogout: () -> Unit) {
    Text("Bienvenido a la Home")
    // Aquí puedes llamar a onLogout() cuando quieras salir
}