package com.example.fila_virtual.features.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.fila_virtual.components.BottomNavigationBar
import com.example.fila_virtual.components.NavigationDefaults
import com.example.fila_virtual.components.ProfileComponent
import com.example.fila_virtual.features.user.home.HomeView
import com.example.fila_virtual.features.user.ordenes.OrdenesScreen
import com.example.fila_virtual.features.user.billetera.BilleteraScreen
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    viewModel: UserViewModel = remember { UserViewModel() },
    onLogout: () -> Unit
) {
    val usuario = viewModel.usuario
    val isLoading = viewModel.isLoading
    val scope = rememberCoroutineScope()

    // Configuración del Pager para permitir deslizar entre pestañas
    val pagerState = rememberPagerState(pageCount = { 4 })

    // Sincronizar el estado del Pager con el BottomNavigationBar
    // Cuando el Pager cambia (por deslizar), no necesitamos hacer nada especial
    // porque el BottomNavigationBar usará pagerState.currentPage

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = NavigationDefaults.userItems(),
                selectedIndex = pagerState.currentPage,
                onItemSelected = { index ->
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading && usuario == null) {
                CircularProgressIndicator(color = Color(0xFFFF5722))
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = true // Permite el deslizamiento
                ) { page ->
                    when (page) {
                        0 -> HomeView(usuario)
                        1 -> OrdenesScreen()
                        2 -> BilleteraScreen(usuario?.billetera ?: "$0.00")
                        3 -> ProfileComponent(
                            usuario = usuario, 
                            onLogout = { viewModel.signOut(onLogout) }
                        )
                    }
                }
            }
        }
    }
}
