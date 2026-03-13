package com.example.fila_virtual.features.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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

@Composable
fun MainScreen(
    viewModel: UserViewModel = remember { UserViewModel() },
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    val usuario = viewModel.usuario
    val isLoading = viewModel.isLoading

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = NavigationDefaults.userItems(),
                selectedIndex = selectedTab,
                onItemSelected = { index -> selectedTab = index }
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
                when (selectedTab) {
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
