package com.example.fila_virtual.features.admin.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fila_virtual.components.FormHeader
import com.example.fila_virtual.components.SearchBar
import com.example.fila_virtual.core.LocalWindowSize
import com.example.fila_virtual.core.theme.*
import com.example.fila_virtual.data.Establecimiento
import com.example.fila_virtual.features.admin.EstablecimientoViewModel

@Composable
fun EstablecimientosScreen(
    onBack: () -> Unit,
    onSelectEstablecimiento: (String) -> Unit,
    onRegisterNew: () -> Unit,
    onAddDish: (String) -> Unit,
    viewModel: EstablecimientoViewModel = viewModel()
) {
    val windowSize = LocalWindowSize.current
    val horizontalPadding = windowSize.adaptiveDp(24).value.dp
    var searchQuery by remember { mutableStateOf("") }

    // Obtenemos la lista real desde el ViewModel
    val establecimientos by viewModel.establecimientos.collectAsState()

    // Filtramos por búsqueda
    val listaFiltrada = establecimientos.filter { 
        it.nombre.contains(searchQuery, ignoreCase = true) 
    }

    Scaffold(
        containerColor = LightBackground,
        topBar = {
            FormHeader(
                title = "Mis Establecimientos",
                onBack = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onRegisterNew,
                containerColor = PrimaryOrange,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar Nuevo Local")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.padding(horizontal = horizontalPadding)) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Buscar sucursal...",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Sucursales",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkGray
                )

                Surface(
                    color = BorderGray,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "${listaFiltrada.size} TOTAL",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (listaFiltrada.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isEmpty()) "No hay establecimientos registrados" else "No se encontraron resultados",
                        color = MediumGray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(
                        start = horizontalPadding,
                        top = 8.dp,
                        end = horizontalPadding,
                        bottom = 100.dp
                    )
                ) {
                    items(listaFiltrada) { local ->
                        EstablecimientoCard(
                            establecimiento = local,
                            onClick = { onSelectEstablecimiento(local.id) },
                            onAddDish = { onAddDish(local.id) },
                            onToggleActive = { viewModel.actualizarEstado(local.id, it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EstablecimientoCard(
    establecimiento: Establecimiento,
    onClick: () -> Unit,
    onAddDish: () -> Unit,
    onToggleActive: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // 1. Imagen y Badge Abierto/Cerrado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BorderGray) // Fondo gris claro como placeholder
            ) {
                // Icono por defecto si no hay imagen
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = MediumGray,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                )

                // Etiqueta "ABIERTO" (basado en si está activo por ahora)
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = if (establecimiento.activo) PrimaryOrange else MediumGray,
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (establecimiento.activo) "ABIERTO" else "CERRADO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Información Principal
            Text(
                text = establecimiento.nombre.ifEmpty { "Sin Nombre" },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = DarkGray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MediumGray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = establecimiento.ubicacion.direccion.ifEmpty { "Sin dirección configurada" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MediumGray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Switch Activo/Inactivo y Botones de Acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = establecimiento.activo,
                        onCheckedChange = { onToggleActive(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryOrange,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = MediumGray,
                            uncheckedBorderColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (establecimiento.activo) "Activo" else "Inactivo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (establecimiento.activo) PrimaryOrange else MediumGray,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row {
                    // Botón rápido para agregar platillo (estilo similar al de agregarmenu)
                    Surface(
                        onClick = onAddDish,
                        shape = RoundedCornerShape(12.dp),
                        color = PrimaryOrange.copy(alpha = 0.1f)
                    ) {
                        Box(modifier = Modifier.padding(12.dp)) {
                            Icon(
                                imageVector = Icons.Default.PlaylistAdd,
                                contentDescription = "Añadir Platillo",
                                tint = PrimaryOrange,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Botón para Editar / Ver Detalles
                    Surface(
                        onClick = onClick,
                        shape = RoundedCornerShape(12.dp),
                        color = PrimaryOrange.copy(alpha = 0.1f)
                    ) {
                        Box(modifier = Modifier.padding(12.dp)) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = PrimaryOrange,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
