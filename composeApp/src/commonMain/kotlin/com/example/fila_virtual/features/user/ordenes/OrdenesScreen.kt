package com.example.fila_virtual.features.user.ordenes

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
// IMPORT NECESARIO PARA EL INDICADOR
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fila_virtual.core.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdenesScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Activas", "Historial")
    var showModal by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
    ) {
        // --- TÍTULO UNIFICADO ---
        Text(
            text = "Mis Órdenes",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 16.dp)
        )

        // PESTAÑAS (TABS)
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            indicator = { tabPositions ->
                if (selectedTabIndex < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = PrimaryOrange
                    )
                }
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
                            color = if (selectedTabIndex == index) PrimaryOrange else MediumGray
                        )
                    }
                )
            }
        }

        // CONTENIDO
        if (selectedTabIndex == 0) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
                item {
                    Text(
                        text = "PEDIDO EN CURSO",
                        color = MediumGray,
                        fontSize = 13.sp,
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
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Text(
                        text = "PRÓXIMAS ENTREGAS",
                        color = MediumGray,
                        fontSize = 13.sp,
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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tienes pedidos en tu historial aún.", color = MediumGray)
            }
        }
    }

    if (showModal) {
        ModalBottomSheet(
            onDismissRequest = { showModal = false },
            sheetState = sheetState,
            containerColor = LightSurface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                QRCodeModalContent(
                    turno = "42",
                    pedidoId = "4829",
                    onDownloadClick = { /* Acción */ },
                    onCloseClick = { showModal = false }
                )
            }
        }
    }
}

@Composable
fun OrderCard(
    restaurantName: String,
    description: String,
    price: String,
    status: String,
    isHighlight: Boolean,
    onClick: () -> Unit
) {
    val statusTextColor = if (isHighlight) PrimaryOrange else MediumGray
    val statusBgColor = if (isHighlight) SoftOrangeBg else LightGray.copy(alpha = 0.5f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LightBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Fastfood, contentDescription = null, tint = MediumGray, modifier = Modifier.size(28.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = restaurantName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                Text(text = description, color = MediumGray, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = price, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PrimaryOrange)
            }

            Box(modifier = Modifier.fillMaxHeight().padding(start = 8.dp)) {
                Surface(shape = RoundedCornerShape(16.dp), color = statusBgColor, modifier = Modifier.align(Alignment.TopEnd)) {
                    Text(text = status, color = statusTextColor, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = LightGray, modifier = Modifier.align(Alignment.CenterEnd))
            }
        }
    }
}
