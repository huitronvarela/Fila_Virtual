package com.example.fila_virtual.features.user.ordenes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fila_virtual.data.Pedido

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdenesScreen(viewModel: OrdenesViewModel = viewModel()) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Activas", "Historial")

    // Colectamos los datos de Firebase desde el ViewModel
    val pedidosActivos by viewModel.pedidosActivos.collectAsState()
    val pedidosHistorial by viewModel.pedidosHistorial.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Estados para el QR (Activas)
    var showQRModal by remember { mutableStateOf(false) }
    var selectedPedidoForQR by remember { mutableStateOf<Pedido?>(null) } // <-- Guardamos el pedido seleccionado
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Estado para el modal de Calificación (Historial)
    var showRatingModal by remember { mutableStateOf(false) }
    var isTacosRated by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
    ) {
        Text(
            text = "Mis Órdenes",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 16.dp)
        )

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

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (selectedTabIndex == 0) {
            // --- PESTAÑA: ACTIVAS ---
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
                if (pedidosActivos.isEmpty()) {
                    item {
                        Text("No tienes órdenes activas.", color = MediumGray, modifier = Modifier.padding(16.dp))
                    }
                } else {
                    items(pedidosActivos) { pedido ->
                        OrderCard(
                            restaurantName = pedido.establecimientoNombre,
                            description = "Orden #${pedido.id.takeLast(4)}",
                            price = "$${pedido.total}",
                            status = pedido.estado,
                            isHighlight = true, // Destacamos todas las activas por ahora
                            onClick = {
                                selectedPedidoForQR = pedido
                                showQRModal = true
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        } else {
            // --- PESTAÑA: HISTORIAL ---
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
                if (pedidosHistorial.isEmpty()) {
                    item {
                        Text("No hay historial de órdenes.", color = MediumGray, modifier = Modifier.padding(16.dp))
                    }
                } else {
                    items(pedidosHistorial) { pedido ->
                        OrderHistoryCard(
                            restaurantName = pedido.establecimientoNombre,
                            description = "Orden #${pedido.id.takeLast(4)}",
                            price = "$${pedido.total}",
                            status = pedido.estado,
                            yaCalificado = isTacosRated,
                            onRateClick = { showRatingModal = true }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    // MODAL DEL QR (Arreglado para evitar crasheos)
    if (showQRModal && selectedPedidoForQR != null) {
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
                // Llamamos a QRCodeModalContent que ya tienes en tu archivo QRCode.kt
                QRCodeModalContent(
                    turno = selectedPedidoForQR!!.turno.toString(),
                    pedidoId = selectedPedidoForQR!!.id.takeLast(5),
                    descripcion = selectedPedidoForQR!!.descripcion, // <--- PASAMOS LOS PRODUCTOS
                    onDownloadClick = { /* Futura función */ },
                    onCloseClick = { showQRModal = false }
                )
            }
        }
    }

    // MODAL DE CALIFICACIÓN (Para el historial)
    if (showRatingModal) {
        RatingModal(
            onDismiss = { showRatingModal = false },
            onRatingSubmitted = {
                showRatingModal = false
                isTacosRated = true
            }
        )
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

@Composable
fun OrderHistoryCard(
    restaurantName: String,
    description: String,
    price: String,
    status: String,
    yaCalificado: Boolean = false,
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

            OutlinedButton(
                onClick = onRateClick,
                enabled = !yaCalificado,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, if (yaCalificado) LightGray else PrimaryOrange),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryOrange,
                    disabledContentColor = MediumGray
                )
            ) {
                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (yaCalificado) "Platillo calificado" else "Calificar platillo",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingModal(onDismiss: () -> Unit, onRatingSubmitted: () -> Unit) {
    var selectedRating by remember { mutableIntStateOf(0) }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessMessage by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val productoRepo = remember { ProductoRepository() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        if (showSuccessMessage) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFFE8F5E9), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ThumbUp, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(30.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("¡Gracias por tu calificación!", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = DarkGray)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tu opinión nos ayuda a mantener el mejor servicio en AlToque.", style = MaterialTheme.typography.bodyMedium, color = MediumGray, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        showSuccessMessage = false
                        onRatingSubmitted()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Aceptar", style = MaterialTheme.typography.titleMedium.copy(color = Color.White))
                }
            }
        } else {
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
                                    showSuccessMessage = true
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
}