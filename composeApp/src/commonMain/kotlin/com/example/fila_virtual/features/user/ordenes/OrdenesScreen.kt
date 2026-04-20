package com.example.fila_virtual.features.user.pedidos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import fila_virtual.composeapp.generated.resources.Res
import fila_virtual.composeapp.generated.resources.*

// Importamos los colores de tu archivo theme
import com.example.fila_virtual.core.theme.PrimaryOrange
import com.example.fila_virtual.core.theme.LightBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdenesScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Activas", "Historial")

    // Estado para controlar la visibilidad del modal (BottomSheet)
    var showModal by remember { mutableStateOf(false) }

    // 1. CLAVE PARA FULL SCREEN: skipPartiallyExpanded = true
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
    ) {
        // TÍTULO ORIGINAL
        Text(
            text = stringResource(Res.string.orders_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 16.dp)
        )

        // PESTAÑAS (TABS)
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = PrimaryOrange
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTabIndex == index) PrimaryOrange else Color.Gray
                        )
                    }
                )
            }
        }

        // CONTENIDO DE LAS PESTAÑAS
        if (selectedTabIndex == 0) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp)
            ) {
                item {
                    Text(
                        text = "PEDIDO EN CURSO",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                item {
                    OrderCard(
                        restaurantName = "Pizzeria Napoli",
                        description = "1x Pizza Margherita",
                        price = "$12.50",
                        status = "EN CAMINO",
                        isHighlight = true,
                        onClick = { showModal = true }
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }

                item {
                    Text(
                        text = "PRÓXIMAS ENTREGAS",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                item {
                    OrderCard(
                        restaurantName = "The Burger Club",
                        description = "2x Bacon Burger, 1x Papas",
                        price = "$24.90",
                        status = "PREPARANDO",
                        isHighlight = false,
                        onClick = { showModal = true }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes pedidos en tu historial aún.", color = Color.Gray)
            }
        }
    }

    // --- MODAL BOTTOM SHEET MODIFICADO PARA FULL SCREEN ---
    // --- MODAL BOTTOM SHEET AJUSTADO ---
    if (showModal) {ModalBottomSheet(
        onDismissRequest = { showModal = false },
        sheetState = sheetState,
        containerColor = Color.White,
        // Eliminamos fillMaxHeight para que no sea pantalla completa forzada
        dragHandle = { BottomSheetDefaults.DragHandle() } // Restauramos la rayita para que se vea como modal
    ) {
        // Usamos fillMaxWidth y dejamos que la altura sea la del contenido (QRCodeModalContent)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp) // Espacio extra al final para que no pegue abajo
                .verticalScroll(rememberScrollState()) // Por seguridad si la pantalla es muy pequeña
        ) {
            QRCodeModalContent(
                turno = "42",
                pedidoId = "4829",
                onDownloadClick = { /* Acción para descargar */ },
                onCloseClick = { showModal = false }
            )
        }
    }
    }
}

// ... Resto del código de OrderCard igual que antes ...
/**
 * Componente de Tarjeta con la flechita y soporte para clic.
 */
@Composable
fun OrderCard(
    restaurantName: String,    description: String,
    price: String,
    status: String,
    isHighlight: Boolean,
    onClick: () -> Unit
) {
    val statusTextColor = if (isHighlight) PrimaryOrange else Color.Gray
    val statusBgColor = if (isHighlight) PrimaryOrange.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.3f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp) // Un pequeño respiro entre tarjetas
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min) // <-- CLAVE: Permite que los hijos usen fillMaxHeight
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 1. Imagen del producto
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Fastfood,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 2. Información central (Nombre, descripción, precio)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = restaurantName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = description,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = price,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PrimaryOrange
                )
            }

            // 3. SECCIÓN DERECHA: Estado arriba y Flecha al centro
            Box(
                modifier = Modifier
                    .fillMaxHeight() // Ocupa todo el alto de la Row
                    .padding(start = 8.dp)
            ) {
                // Estado "Subido" al tope derecho
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = statusBgColor,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = status,
                        color = statusTextColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Flechita "Centrada" verticalmente en el medio derecho
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Ver detalles",
                    tint = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}