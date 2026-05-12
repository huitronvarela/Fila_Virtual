package com.example.fila_virtual.features.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fila_virtual.Perfil.ProfileComponent
import com.example.fila_virtual.components.BottomNavigationBar
import com.example.fila_virtual.components.NavigationDefaults
import com.example.fila_virtual.core.WindowSize
import com.example.fila_virtual.features.admin.empleados.ScreenEmpleados
import com.example.fila_virtual.features.admin.inicio.AñadirEstablecimientoScreen
import com.example.fila_virtual.features.admin.inicio.EstablecimientosScreen
import com.example.fila_virtual.features.admin.inicio.InicioAdminScreen
import com.example.fila_virtual.features.admin.menu.AgregarPlatilloScreen
import com.example.fila_virtual.features.admin.menu.ScreenMenu
import com.example.fila_virtual.features.user.EditProfileScreen
import com.example.fila_virtual.features.user.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun AdminMainScreen(
    viewModel: UserViewModel,
    onLogout: () -> Unit
) {
    val usuario = viewModel.usuario ?: return
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 4 })

    var isEditingProfile by remember { mutableStateOf(false) }
    var isAddingDish by remember { mutableStateOf(false) }
    var isManagingEstablecimientos by remember { mutableStateOf(false) }
    var isAddingEstablecimiento by remember { mutableStateOf(false) }

    // 1. Decidimos qué pantalla mostrar a nivel raíz
    if (isAddingDish) {
        AgregarPlatilloScreen(
            onBack = { isAddingDish = false },
            onSave = { nombre, desc, precio, cat ->
                // Aquí irá tu lógica de Firebase más adelante
                isAddingDish = false
            }
        )
    } else if (isEditingProfile) {
        EditProfileScreen(
            usuario = usuario,
            viewModel = viewModel,
            onBack = { isEditingProfile = false }
        )
    } else if (isAddingEstablecimiento) {
        AñadirEstablecimientoScreen(
            onBack = { isAddingEstablecimiento = false }
        )
    } else if (isManagingEstablecimientos) {
        EstablecimientosScreen(
            onBack = { isManagingEstablecimientos = false },
            onSelectEstablecimiento = { id ->
                // Lógica para seleccionar establecimiento
            },
            onRegisterNew = {
                isAddingEstablecimiento = true
            }
        )
    } else {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    items = NavigationDefaults.adminItems(),
                    selectedIndex = pagerState.currentPage,
                    onItemSelected = { index ->
                        scope.launch { pagerState.animateScrollToPage(index) }
                    }
                )
            }
        ) { padding ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.padding(padding)
            ) { page ->
                when (page) {
                    0 -> InicioAdminScreen(
                        onNavigateToManage = { isManagingEstablecimientos = true }
                    )
                    1 -> ScreenEmpleados()
                    2 -> ScreenMenu(onNavigateToAdd = { isAddingDish = true })
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