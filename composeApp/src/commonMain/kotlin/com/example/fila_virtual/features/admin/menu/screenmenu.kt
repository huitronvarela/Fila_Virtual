package com.example.fila_virtual.features.admin.menu

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fila_virtual.components.SearchBar
import com.example.fila_virtual.core.theme.*
import com.example.fila_virtual.data.Producto
import com.example.fila_virtual.features.admin.ProductoViewModel

@Composable
fun ScreenMenu(
    establecimientoId: String,
    onNavigateToAdd: () -> Unit,
    viewModel: ProductoViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val categorias = listOf("Todos", "Entradas", "Platos Fuertes", "Bebidas", "Postres")
    var categoriaSeleccionada by remember { mutableStateOf(categorias[0]) }

    // Sincronizar el establecimientoId con el ViewModel para cargar los productos reales
    LaunchedEffect(establecimientoId) {
        if (establecimientoId.isNotEmpty()) {
            viewModel.setEstablecimientoId(establecimientoId)
        }
    }

    // Observamos los productos del establecimiento desde Firebase
    val productos by viewModel.productos.collectAsState()

    // Filtrado dinámico por categoría y búsqueda
    val productosFiltrados = productos.filter { producto ->
        val matchesCategory = categoriaSeleccionada == "Todos" || producto.categoria == categoriaSeleccionada
        val matchesSearch = producto.nombre.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Scaffold(
        containerColor = LightBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
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
            // Título de la sección
            Text(
                text = "Gestión de Menú",
                style = MaterialTheme.typography.titleLarge,
                color = DarkGray,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 16.dp)
            )

            // Buscador funcional
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Buscar platillos...",
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Chips de categorías horizontal
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
                        border = if (!isSelected) BorderStroke(1.dp, BorderGray) else null
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

            // Lista de Platillos o Estado Vacío
            if (productosFiltrados.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (establecimientoId.isEmpty()) 
                            "Selecciona un establecimiento en Inicio para ver su menú" 
                        else if (searchQuery.isEmpty()) 
                            "No hay platillos registrados aún" 
                        else 
                            "No se encontraron resultados para '$searchQuery'",
                        color = MediumGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(productosFiltrados) { producto ->
                        CardMenuItem(
                            producto = producto,
                            onToggleDisponibilidad = { disponible ->
                                viewModel.actualizarDisponibilidad(producto.id, disponible)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CardMenuItem(
    producto: Producto,
    onToggleDisponibilidad: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder de imagen
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ExtraLightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Fastfood, contentDescription = null, tint = MediumGray)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info del platillo
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkGray
                )
                Text(
                    text = producto.categoria,
                    style = MaterialTheme.typography.labelSmall,
                    color = MediumGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${producto.precio}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryOrange
                )
            }

            // Switch de disponibilidad
            Switch(
                checked = producto.disponible,
                onCheckedChange = onToggleDisponibilidad,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryOrange,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = MediumGray,
                    uncheckedBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Editar",
                tint = MediumGray
            )
        }
    }
}
