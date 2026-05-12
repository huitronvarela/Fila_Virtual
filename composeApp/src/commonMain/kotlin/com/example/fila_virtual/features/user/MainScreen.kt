package com.example.fila_virtual.features.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fila_virtual.core.WindowSize
import com.example.fila_virtual.components.BottomNavigationBar
import com.example.fila_virtual.components.NavigationDefaults
import com.example.fila_virtual.Perfil.ProfileComponent
import com.example.fila_virtual.features.user.home.HomeView
import com.example.fila_virtual.features.user.ordenes.OrdenesScreen
import com.example.fila_virtual.features.user.billetera.BilleteraScreen
import com.example.fila_virtual.features.user.carrio_compra.CartScreen
import kotlinx.coroutines.launch

// Importamos el tema para los colores estandarizados
import com.example.fila_virtual.core.theme.*

@Composable
fun MainScreen(
    viewModel: UserViewModel = remember { UserViewModel() },
    onLogout: () -> Unit
) {
    val usuario = viewModel.usuario
    val isLoading = viewModel.isLoading
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 4 })

    // ESTADOS PARA NAVEGACIÓN
    var isEditingProfile by remember { mutableStateOf(false) }
    var showCart by remember { mutableStateOf(false) }

    // Prioridad de navegación: Edición > Carrito > Pantalla Principal
    if (isEditingProfile) {
        EditProfileScreen(
            usuario = usuario,
            viewModel = viewModel,
            onBack = { isEditingProfile = false }
        )
    } else if (showCart) {
        // AQUÍ ESTABA EL ERROR: Ya le pasamos el viewModel al Carrito
        CartScreen(
            viewModel = viewModel,
            onBackClick = { showCart = false }
        )
    } else {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val windowSize = WindowSize(maxWidth, maxHeight)
            val horizontalMargin = if (windowSize.isTablet) (maxWidth - 550.dp) / 2 else 0.dp

            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
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
                        .padding(horizontal = horizontalMargin)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    if (isLoading && usuario == null) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize(),
                            userScrollEnabled = true
                        ) { page ->
                            when (page) {
                                0 -> HomeView(
                                    usuario = usuario,
                                    onCartClick = { showCart = true }
                                )
                                1 -> OrdenesScreen()
                                2 -> BilleteraScreen(viewModel)
                                3 -> ProfileComponent(
                                    usuario = usuario,
                                    viewModel = viewModel,
                                    onLogout = { viewModel.signOut(onLogout) },
                                    onNavigateToEdit = { isEditingProfile = true }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}