package com.example.fila_virtual.features.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.fila_virtual.perfil.ProfileComponent
import com.example.fila_virtual.components.BottomNavigationBar
import com.example.fila_virtual.components.NavigationDefaults
// AQUI ESTA LA IMPORTACION CORRECTA (SIN LA Ñ)
import com.example.fila_virtual.features.admin.empleados.AnadirEmpleadoScreen
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
    var isAddingEmployee by remember { mutableStateOf(false) }
    var isManagingEstablecimientos by remember { mutableStateOf(false) }
    var isAddingEstablecimiento by remember { mutableStateOf(false) }

    var selectedEstablecimientoId by remember { mutableStateOf("") }

    if (isAddingDish) {
        AgregarPlatilloScreen(
            establecimientoId = selectedEstablecimientoId,
            onBack = { isAddingDish = false }
        )
    } else if (isAddingEmployee) {
        // AQUI ESTA LA LLAMADA CORRECTA A LA FUNCION (SIN LA Ñ)
        AnadirEmpleadoScreen(
            establecimientoId = selectedEstablecimientoId,
            onBack = { isAddingEmployee = false }
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
                selectedEstablecimientoId = id
                isManagingEstablecimientos = false
                scope.launch { pagerState.animateScrollToPage(2) }
            },
            onRegisterNew = {
                isAddingEstablecimiento = true
            },
            onAddDish = { id ->
                selectedEstablecimientoId = id
                isAddingDish = true
                isManagingEstablecimientos = false
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
                    1 -> ScreenEmpleados(
                        onNavigateToAdd = {
                            isAddingEmployee = true
                        }
                    )
                    2 -> ScreenMenu(
                        establecimientoId = selectedEstablecimientoId,
                        onNavigateToAdd = {
                            isAddingDish = true
                        }
                    )
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