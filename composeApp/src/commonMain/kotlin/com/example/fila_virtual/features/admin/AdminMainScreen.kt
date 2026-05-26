package com.example.fila_virtual.features.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fila_virtual.components.BottomNavigationBar
import com.example.fila_virtual.components.NavigationDefaults
import com.example.fila_virtual.core.WindowSize
import com.example.fila_virtual.features.admin.empleados.AnadirEmpleadoScreen
import com.example.fila_virtual.features.admin.empleados.ScreenEmpleados
import com.example.fila_virtual.features.admin.inicio.AñadirEstablecimientoScreen
import com.example.fila_virtual.features.admin.inicio.EstablecimientosScreen
import com.example.fila_virtual.features.admin.inicio.InicioAdminScreen
import com.example.fila_virtual.features.admin.menu.AgregarPlatilloScreen
import com.example.fila_virtual.features.admin.menu.ScreenMenu
import com.example.fila_virtual.features.user.EditProfileScreen
import com.example.fila_virtual.features.user.UserViewModel
import com.example.fila_virtual.perfil.ProfileComponent
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
    
    var productoToEdit by remember { mutableStateOf<com.example.fila_virtual.data.Producto?>(null) }
    var empleadoToEdit by remember { mutableStateOf<com.example.fila_virtual.data.Empleado?>(null) }
    var establecimientoToEdit by remember { mutableStateOf<com.example.fila_virtual.data.Establecimiento?>(null) }

    if (isAddingDish) {
        AgregarPlatilloScreen(
            establecimientoId = selectedEstablecimientoId,
            ownerUid = usuario.uid,
            productoToEdit = productoToEdit,
            onBack = { 
                isAddingDish = false
                productoToEdit = null
            }
        )
    } else if (isAddingEmployee) {
        AnadirEmpleadoScreen(
            establecimientoId = selectedEstablecimientoId,
            empleadoToEdit = empleadoToEdit,
            onBack = { 
                isAddingEmployee = false
                empleadoToEdit = null
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
            ownerUid = usuario.uid,
            establecimientoToEdit = establecimientoToEdit,
            onBack = { 
                isAddingEstablecimiento = false
                establecimientoToEdit = null
            }
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
            onEditEstablecimiento = { est ->
                establecimientoToEdit = est
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
                        establecimientoId = selectedEstablecimientoId,
                        onNavigateToAdd = { 
                            isAddingEmployee = true
                        },
                        onEditEmpleado = { emp ->
                            empleadoToEdit = emp
                            isAddingEmployee = true
                        }
                    )
                    2 -> ScreenMenu(
                        establecimientoId = selectedEstablecimientoId,
                        ownerUid = usuario.uid,
                        onNavigateToAdd = {
                            isAddingDish = true
                        },
                        onNavigateToEdit = { prod ->
                            productoToEdit = prod
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
