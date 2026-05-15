package com.example.fila_virtual.features.admin.empleados

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fila_virtual.core.theme.*
import com.example.fila_virtual.components.SearchBar

data class EmpleadoUi(
    val uid: String,
    val nombre: String,
    val rol: String,
    val activo: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenEmpleados(onNavigateToAdd: () -> Unit) {
    val listaEjemplo = listOf(
        EmpleadoUi(uid = "1", nombre = "Carlos Ruiz", rol = "Cocinero", activo = true),
        EmpleadoUi(uid = "2", nombre = "Sofía Méndez", rol = "Cajera", activo = false),
        EmpleadoUi(uid = "3", nombre = "Luis Pérez", rol = "Repartidor", activo = true),
        EmpleadoUi(uid = "4", nombre = "Elena Torres", rol = "Cocinero", activo = true)
    )

    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        containerColor = LightBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = PrimaryOrange,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Empleado")
            }
        }
    ) { paddingValues -> // 👇 CORRECCIÓN 1: Recibir el paddingValues del Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // 👇 CORRECCIÓN 2: Aplicarlo aquí para evitar que el contenido se encime
                .padding(horizontal = 24.dp) // Un poco más de margen lateral para que coincida con tu diseño
        ) {
            Text(
                text = "Gestión de Empleados",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 24.dp)
            )

            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Buscar empleados...",
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Personal",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkGray
                )

                Surface(
                    color = BorderGray, // Simulando el fondo grisecito del badge
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "${listaEjemplo.size} TOTAL",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                // Padding inferior extra para que el último elemento no se esconda detrás del FAB
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                items(listaEjemplo) { empleado ->
                    CardEmpleado(empleado)
                }
            }
        }
    }
}

@Composable
fun CardEmpleado(empleado: EmpleadoUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Un toquecito sutil de sombra
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(56.dp)) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MediumGray)
                }

                // El puntito verde/gris de estado
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(16.dp)
                        .background(
                            color = if (empleado.activo) TrafficGreen else MediumGray,
                            shape = CircleShape
                        )
                        .border(2.dp, Color.White, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = empleado.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkGray
                )
                Text(
                    text = empleado.rol,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MediumGray
                )
                Spacer(modifier = Modifier.height(4.dp))

                // La etiqueta de ACTIVO / INACTIVO
                val backgroundColor = if (empleado.activo) Color(0xFFE8F5E9) else ExtraLightGray
                val textColor = if (empleado.activo) TrafficGreen else MediumGray
                Surface(color = backgroundColor, shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = if (empleado.activo) "ACTIVO" else "INACTIVO",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = "Ver detalles", tint = MediumGray)
        }
    }
}