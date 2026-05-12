package com.example.fila_virtual.features.admin.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fila_virtual.components.SearchBar
import com.example.fila_virtual.core.theme.*

// Modelo de datos temporal para la vista
data class MenuItemUi(
    val id: String,
    val nombre: String,
    val precio: Double,
    var disponible: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenMenu(onNavigateToAdd: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val categorias = listOf("All", "Entradas", "Platos Fuertes", "Bebidas")
    var categoriaSeleccionada by remember { mutableStateOf(categorias[0]) }

    // Lista temporal
    val menuItems = remember { mutableStateListOf(
        MenuItemUi("1", "Ensalada de Verano", 12.50, true),
        MenuItemUi("2", "Hamburguesa Especial", 15.00, true)
    )}

    Scaffold(
        containerColor = LightBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd, // Llama al callback que definimos en AdminMainScreen
                containerColor = PrimaryOrange,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Platillo")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // --- TÍTULO UNIFICADO ---
            Text(
                text = "Gestión de Menú",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 16.dp)
            )

            // Componente SearchBar
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Buscar platillos, ingredientes...",
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Pestañas
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(categorias) { categoria ->
                    val isSelected = categoria == categoriaSeleccionada
                    Surface(
                        color = if (isSelected) PrimaryOrange else Color.White,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.clickable { categoriaSeleccionada = categoria },
                        shadowElevation = 0.dp
                    ) {
                        Text(
                            text = categoria,
                            color = if (isSelected) Color.White else DarkGray,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // Lista de Platillos
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(menuItems) { item ->
                    CardMenuItem(
                        item = item,
                        onCheckedChange = { isChecked ->
                            val index = menuItems.indexOf(item)
                            if (index != -1) {
                                menuItems[index] = item.copy(disponible = isChecked)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CardMenuItem(item: MenuItemUi, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Fastfood, contentDescription = null, tint = Color.White)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkGray
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$${item.precio}0",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = TrafficRed
                )
            }

            Switch(
                checked = item.disponible,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryOrange,
                    uncheckedThumbColor = MediumGray,
                    uncheckedTrackColor = ExtraLightGray,
                    uncheckedBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Editar platillo",
                tint = MediumGray
            )
        }
    }
}
