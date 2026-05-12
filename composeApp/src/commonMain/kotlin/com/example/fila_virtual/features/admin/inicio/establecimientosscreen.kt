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
import com.example.fila_virtual.components.FormHeader
import com.example.fila_virtual.components.SearchBar
import com.example.fila_virtual.core.LocalWindowSize
import com.example.fila_virtual.core.theme.*

// Modelo de datos para la vista
data class EstablecimientoUi(
    val id: String,
    val nombre: String,
    val direccion: String,
    val isOpen: Boolean,
    val imageUrl: String? = null
)

@Composable
fun EstablecimientosScreen(
    onBack: () -> Unit,
    onSelectEstablecimiento: (String) -> Unit,
    onRegisterNew: () -> Unit
) {
    val windowSize = LocalWindowSize.current
    val horizontalPadding = windowSize.adaptiveDp(24).value.dp
    var searchQuery by remember { mutableStateOf("") }

    // Lista de ejemplo
    val listaEstablecimientos = listOf(
        EstablecimientoUi("1", "Al Toque Manzanillo", "Blvd. Miguel de la Madrid 123", true),
        EstablecimientoUi("2", "Al Toque Colima", "Av. Constitución 456", false),
        EstablecimientoUi("3", "Al Toque Villa de Álvarez", "Calle Tercer Anillo 789", true)
    ).filter { it.nombre.contains(searchQuery, ignoreCase = true) }

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

            // --- Buscador REUTILIZABLE (Mismo componente que Home y Empleados) ---
            Box(modifier = Modifier.padding(horizontal = horizontalPadding)) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Buscar sucursal...",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Subtítulo y Contador (Consistencia con screenempleados.kt) ---
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
                        text = "${listaEstablecimientos.size} TOTAL",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- SECCIÓN DESPLAZABLE: Lista de Establecimientos ---
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(
                    start = horizontalPadding,
                    end = horizontalPadding,
                    bottom = 100.dp,
                    top = 8.dp
                )
            ) {
                items(listaEstablecimientos) { local ->
                    EstablecimientoCard(
                        establecimiento = local,
                        onClick = { onSelectEstablecimiento(local.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun EstablecimientoCard(
    establecimiento: EstablecimientoUi,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del Local (Logo)
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(ExtraLightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Storefront,
                    contentDescription = null,
                    tint = MediumGray,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información del Local
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = establecimiento.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkGray
                )
                Text(
                    text = establecimiento.direccion,
                    style = MaterialTheme.typography.labelSmall,
                    color = MediumGray,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Badge de Estado (Abierto/Cerrado)
                Surface(
                    color = if (establecimiento.isOpen) TrafficGreen.copy(alpha = 0.1f) else TrafficRed.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    if (establecimiento.isOpen) TrafficGreen else TrafficRed,
                                    CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (establecimiento.isOpen) "Abierto" else "Cerrado",
                            color = if (establecimiento.isOpen) TrafficGreen else TrafficRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MediumGray
            )
        }
    }
}
