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
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    onToggleActive: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Imagen y Badge (Izquierda)
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BorderGray) // Placeholder de la imagen
            ) {
                // Icono por defecto
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = MediumGray,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(36.dp)
                )

                // Etiqueta "ABIERTO" / "CERRADO"
                val badgeBgColor = if (establecimiento.activo) TrafficGreen.copy(alpha = 0.15f) else ExtraLightGray
                val badgeTextColor = if (establecimiento.activo) TrafficGreen else MediumGray

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = badgeBgColor
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    color = badgeTextColor,
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (establecimiento.activo) "ABIERTO" else "CERRADO",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = badgeTextColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 2. Información Principal (Centro)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = establecimiento.nombre.ifEmpty { "Sin Nombre" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkGray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MediumGray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = establecimiento.ubicacion.direccion.ifEmpty { "Sin dirección" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MediumGray,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 3. Acciones (Derecha) - Consistencia con Menu
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
                )

                Spacer(modifier = Modifier.weight(1f))

                Switch(
                    checked = establecimiento.activo,
                    onCheckedChange = onToggleActive,
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
