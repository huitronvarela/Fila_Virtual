package com.example.fila_virtual.features.user.ordenes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.fila_virtual.core.theme.*
import com.example.fila_virtual.repository.ProductoRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdenesScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Activas", "Historial")

    // Estados para el QR (Activas)
    var showQRModal by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Estado para el modal de Calificación (Historial)
    var showRatingModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
    ) {
        // --- TÍTULO UNIFICADO ---
        Text(
            text = "Mis Órdenes",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
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
                        color = MaterialTheme.colorScheme.primary
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
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTabIndex == index) MaterialTheme.colorScheme.primary else MediumGray
                        )
                    }
                )
            }
        }

        // CONTENIDO
        if (selectedTabIndex == 0) {
            // --- PESTAÑA: ACTIVAS ---
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
                        style = MaterialTheme.typography.labelSmall,
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
                        onClick = { showQRModal = true }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Text(
                        text = "PRÓXIMAS ENTREGAS",
                        color = MediumGray,
                        style = MaterialTheme.typography.labelSmall,
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
                        onClick = { showQRModal = true }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        } else {
            // --- PESTAÑA: HISTORIAL (CON EL BOTÓN DE CALIFICAR) ---
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
                item {
                    Text(
                        text = "ENTREGADOS RECIENTEMENTE",
                        color = MediumGray,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                item {
                    // Tarjeta especial para el historial que dispara las estrellitas
                    OrderHistoryCard(
                        restaurantName = "en buen pollos",
                        description = "1x tacos",
                        price = "$12.00",
                        status = "ENTREGADO",
                        onRateClick = { showRatingModal = true }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // MODAL DEL QR (Para órdenes activas)
    if (showQRModal) {
        ModalBottomSheet(
            onDismissRequest = { showQRModal = false },
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
                // Aquí asumo que tienes tu QRCodeModalContent en otro archivo
                // QRCodeModalContent(turno = "42", pedidoId = "4829", onDownloadClick = { }, onCloseClick = { showQRModal = false })
            }
        }
    }

    // MODAL DE CALIFICACIÓN TIPO BOTTOM SHEET (Para el historial)
    if (showRatingModal) {
        RatingModal(
            onDismiss = { showRatingModal = false },
            onRatingSubmitted = { showRatingModal = false }
        )
    }
}

// --- TARJETA PARA ÓRDENES ACTIVAS ---
@Composable
fun OrderCard(
    restaurantName: String,
    description: String,
    price: String,
    status: String,
    isHighlight: Boolean,
    onClick: () -> Unit
) {
    val statusTextColor = if (isHighlight) MaterialTheme.colorScheme.primary else MediumGray
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
                Text(
                    text = restaurantName,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    color = MediumGray,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = price,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Box(modifier = Modifier.fillMaxHeight().padding(start = 8.dp)) {
                Surface(shape = RoundedCornerShape(16.dp), color = statusBgColor, modifier = Modifier.align(Alignment.TopEnd)) {
                    Text(
                        text = status,
                        color = statusTextColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = LightGray,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}

// --- TARJETA PARA EL HISTORIAL ---
@Composable
fun OrderHistoryCard(
    restaurantName: String,
    description: String,
    price: String,
    status: String,
    onRateClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    Text(
                        text = restaurantName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = description,
                        color = MediumGray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = price,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Surface(shape = RoundedCornerShape(16.dp), color = LightGray.copy(alpha = 0.5f)) {
                    Text(
                        text = status,
                        color = MediumGray,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            // Botón de calificar
            OutlinedButton(
                onClick = onRateClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, PrimaryOrange),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryOrange)
            ) {
                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Calificar platillo", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- MODAL DE CALIFICACIÓN (BOTTOM SHEET) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingModal(onDismiss: () -> Unit, onRatingSubmitted: () -> Unit) {
    var selectedRating by remember { mutableIntStateOf(0) }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val productoRepo = remember { ProductoRepository() }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(60.dp).background(SoftOrangeBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ThumbUp, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(30.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("¿Qué te pareció?", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = DarkGray)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Tu opinión ayuda a otros estudiantes a elegir mejor.", style = MaterialTheme.typography.bodyMedium, color = MediumGray, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(24.dp))

            // Estrellitas
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in 1..5) {
                    Icon(
                        imageVector = if (i <= selectedRating) Icons.Default.Star else Icons.Outlined.StarOutline,
                        contentDescription = "Estrella $i",
                        tint = if (i <= selectedRating) TrafficYellow else BorderGray,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { if (!isSubmitting) selectedRating = i }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage != null) {
                Text(
                    text = "Error Firebase: $errorMessage",
                    color = TrafficRed,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    if (selectedRating > 0) {
                        isSubmitting = true
                        errorMessage = null
                        scope.launch {
                            val idTacos = "8nh8acT3yJi5Ys12xKxO"
                            val result = productoRepo.calificarProducto(idTacos, selectedRating)

                            isSubmitting = false
                            if (result.isSuccess) {
                                onRatingSubmitted()
                            } else {
                                errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                            }
                        }
                    }
                },
                enabled = selectedRating > 0 && !isSubmitting,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Enviar calificación", style = MaterialTheme.typography.titleMedium.copy(color = Color.White))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Cancelar",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MediumGray,
                modifier = Modifier.clickable { if (!isSubmitting) onDismiss() }
            )
        }
    }
}