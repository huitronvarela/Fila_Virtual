package com.example.fila_virtual.features.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fila_virtual.core.WindowSize
import com.example.fila_virtual.components.BottomNavigationBar
import com.example.fila_virtual.components.NavigationDefaults
import com.example.fila_virtual.components.ProfileComponent
import com.example.fila_virtual.features.user.home.HomeView
import com.example.fila_virtual.features.user.billetera.BilleteraScreen
import com.example.fila_virtual.features.user.pedidos.OrdenesScreen
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    viewModel: UserViewModel = remember { UserViewModel() },
    onLogout: () -> Unit
) {
    val usuario = viewModel.usuario
    val isLoading = viewModel.isLoading
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 4 })

    // CLAVE: Usamos BoxWithConstraints para obtener el tamaño real de la pantalla
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val windowSize = WindowSize(maxWidth, maxHeight)

        // Si es Tablet, calculamos un margen para centrar el contenido (máximo 550dp)
        val horizontalMargin = if (windowSize.isTablet) (maxWidth - 550.dp) / 2 else 0.dp

        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    items = NavigationDefaults.userItems(),
                    selectedIndex = pagerState.currentPage,
                    onItemSelected = { index ->
                        scope.launch { pagerState.animateScrollToPage(index) }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = horizontalMargin) // Responsividad aquí
                    .background(Color.White)
            ) {
                if (isLoading && usuario == null) {
                    CircularProgressIndicator(color = Color(0xFFFF5722))
                } else {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        userScrollEnabled = true
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
}