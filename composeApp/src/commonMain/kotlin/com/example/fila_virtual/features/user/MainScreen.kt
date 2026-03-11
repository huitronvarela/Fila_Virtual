package com.example.fila_virtual.features.user

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch

import com.example.fila_virtual.core.components.BottomNavigationBar
import com.example.fila_virtual.core.components.NavigationDefaults
import com.example.fila_virtual.core.components.ProfileComponent
import com.example.fila_virtual.core.data.Usuario
import com.example.fila_virtual.features.user.home.HomeView
import com.example.fila_virtual.features.user.ordenes.OrdenesScreen
import com.example.fila_virtual.features.user.billetera.BilleteraScreen

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(onLogout: () -> Unit) {
    var usuario by remember { mutableStateOf<Usuario?>(null) }

    // 1. Configuramos el estado del paginador (carrusel) para tus 4 pantallas
    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            try {
                val document = Firebase.firestore.collection("usuarios").document(currentUser.uid).get()
                if (document.exists) usuario = document.data<Usuario>()
            } catch (e: Exception) {
                println("🔥 ERROR FIRESTORE: ${e.message}")
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = NavigationDefaults.userItems(),
                selectedIndex = pagerState.currentPage, // 2. La barra se sincroniza con el deslizamiento
                onItemSelected = { index ->
                    // 3. Cuando tocas un ícono en la barra, se desliza automáticamente a esa pantalla
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    ) { padding ->
        // 4. Cambiamos el "Box" por un "HorizontalPager" que detecta los gestos de tu dedo
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) { page ->
            when (page) {
                0 -> HomeView(usuario)
                1 -> OrdenesScreen()
                2 -> BilleteraScreen(usuario?.billetera ?: "$0.00")
                3 -> ProfileComponent(usuario = usuario, onLogout = onLogout)
            }
        }
    }
}