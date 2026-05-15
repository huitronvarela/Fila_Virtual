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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocationOn
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
import com.example.fila_virtual.features.admin.EstablecimientoViewModel

@Composable
fun ScreenMenu(
    establecimientoId: String,
    ownerUid: String,
    onNavigateToAdd: () -> Unit,
    viewModel: ProductoViewModel = viewModel(),
    establecimientoViewModel: EstablecimientoViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val categorias = listOf("Todos", "Entradas", "Platos Fuertes", "Bebidas", "Postres")
    var categoriaSeleccionada by remember { mutableStateOf(categorias[0]) }
    
    var currentEstablecimientoId by remember { mutableStateOf(establecimientoId) }
    var showSucursalSelector by remember { mutableStateOf(false) }

    // Sincronizar el establecimientoId con el ViewModel para cargar los productos reales
    LaunchedEffect(currentEstablecimientoId, ownerUid) {
        viewModel.setOwnerUid(ownerUid)
        viewModel.setEstablecimientoId(currentEstablecimientoId)
        establecimientoViewModel.setOwnerUid(ownerUid)
    }

    // Observamos los productos y establecimientos
    val productos by viewModel.productos.collectAsState()
    val establecimientos by establecimientoViewModel.establecimientos.collectAsState()
    
    val sucursalActual = if (currentEstablecimientoId.isEmpty()) {
        "Todas las Sucursales"
    } else {
        establecimientos.find { it.id == currentEstablecimientoId }?.nombre ?: "Sucursal desconocida"
    }

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
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Gestión de Menú",
                    style = MaterialTheme.typography.headlineSmall,
                    color = DarkGray,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                )
                
                Box {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.clickable { showSucursalSelector = true },
                        border = BorderStroke(1.dp, ExtraLightGray)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                                tint = PrimaryOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = sucursalActual,
                                color = PrimaryOrange,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = PrimaryOrange,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showSucursalSelector,
                        onDismissRequest = { showSucursalSelector = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todas las Sucursales") },
                            onClick = {
                                currentEstablecimientoId = ""
                                showSucursalSelector = false
                            }
                        )
                        establecimientos.forEach { sucursal ->
                            DropdownMenuItem(
                                text = { Text(sucursal.nombre) },
                                onClick = {
                                    currentEstablecimientoId = sucursal.id
                                    showSucursalSelector = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

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
                        text = if (ownerUid.isEmpty())
                            "Inicia sesión para ver tu menú" 
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
                        val sucursalNombre = establecimientos.find { it.id == producto.establecimientoId }?.nombre ?: "Sin Sucursal"
                        CardMenuItem(
                            producto = producto,
                            sucursalNombre = sucursalNombre,
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
    sucursalNombre: String,
    onToggleDisponibilidad: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Navegar a editar o detalles */ },
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
            // Imagen
            Box(
                modifier = Modifier
                    .size(80.dp)
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = ExtraLightGray,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = sucursalNombre.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkGray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$${producto.precio}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryOrange
                )
            }

            // Acciones (Derecha)
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Editar",
                    tint = PrimaryOrange,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { /* TODO: Navegar a editar */ }
                )
                
                Spacer(modifier = Modifier.weight(1f))

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
            }
        }
    }
}
