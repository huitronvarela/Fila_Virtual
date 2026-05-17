package com.example.fila_virtual.features.user.carrio_compra

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import com.example.fila_virtual.core.LocalWindowSize
import com.example.fila_virtual.core.theme.*
import com.example.fila_virtual.repository.ProductoRepository // Inyectamos tu repositorio

// --- MODELOS DE DATOS DE PRUEBA ---
data class CartItem(val name: String, val description: String, val price: String, val quantity: Int)

@Composable
fun CartScreen(onBackClick: () -> Unit) {
    val windowSize = LocalWindowSize.current
    val padding = windowSize.adaptiveDp(16).value.dp

    // 👇 1. EL INTERRUPTOR DEL MODAL
    var showRatingModal by remember { mutableStateOf(false) }

    // Datos simulados
    val cartItems = listOf(
        CartItem("Doble Smash Burger", "Sin cebolla", "$8.500", 1),
        CartItem("Pizza Margarita", "Masa madre", "$12.000", 1)
    )

    // 👇 3. EL MODAL SE DIBUJA SI EL INTERRUPTOR ESTÁ ENCENDIDO
    if (showRatingModal) {
        RatingModal(
            onDismiss = { showRatingModal = false },
            onRatingSubmitted = {
                // Aquí podrías agregar una navegación a la pantalla de éxito después de calificar
                showRatingModal = false
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { CartTopBar(onBackClick = onBackClick) },
        // 👇 2. EL DISPARADOR: Le pasamos la orden de encender el modal al botón de pagar
        bottomBar = { CartBottomBar(onPayClick = { showRatingModal = true }) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = padding),
            verticalArrangement = Arrangement.spacedBy(windowSize.adaptiveDp(24).value.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { InfoBanner() }
            item {
                Text(
                    text = "Tu Pedido",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = windowSize.adaptiveSp(18)
                    ),
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                CartItemsList(cartItems)
            }
            item { PaymentMethodSection() }
            item { SummarySection() }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

// --- 👇 NUEVO COMPONENTE: EL MODAL DE CALIFICACIÓN ---
@Composable
fun RatingModal(onDismiss: () -> Unit, onRatingSubmitted: () -> Unit) {
    var selectedRating by remember { mutableIntStateOf(0) }
    var isSubmitting by remember { mutableStateOf(false) }
    // 👇 NUEVO: Estado para guardar y mostrar el error real de Firebase
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val productoRepo = remember { ProductoRepository() }

    Dialog(onDismissRequest = { if (!isSubmitting) onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(60.dp).background(SoftOrangeBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ThumbUp, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(30.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("¡Tu orden está lista!", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = DarkGray)
                Spacer(modifier = Modifier.height(8.dp))
                Text("¿Qué te pareció la comida? Tu opinión ayuda a otros a elegir.", style = MaterialTheme.typography.bodyMedium, color = MediumGray, textAlign = TextAlign.Center)
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

                // 👇 AQUI PINTAMOS EL ERROR EN ROJO SI ALGO FALLA
                if (errorMessage != null) {
                    Text(
                        text = "Error Firebase: $errorMessage",
                        color = TrafficRed,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Botón
                Button(
                    onClick = {
                        if (selectedRating > 0) {
                            isSubmitting = true
                            errorMessage = null // Limpiamos el error previo
                            scope.launch {
                                val idTacos = "8nh8acT3yJi5Ys12xKxO"
                                // 👇 ATRAPAMOS EL RESULTADO DE LA FUNCIÓN
                                val result = productoRepo.calificarProducto(idTacos, selectedRating)

                                isSubmitting = false
                                if (result.isSuccess) {
                                    onRatingSubmitted()
                                } else {
                                    // SI FALLA, LO MOSTRAMOS EN LA PANTALLA
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
                Text("Omitir por ahora", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = MediumGray, modifier = Modifier.clickable { if (!isSubmitting) onDismiss() })
            }
        }
    }
}


@Composable
fun CartTopBar(onBackClick: () -> Unit) {
    val windowSize = LocalWindowSize.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = windowSize.adaptiveDp(24), bottom = windowSize.adaptiveDp(16)),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        Text(
            text = "Carrito",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun InfoBanner() {
    val windowSize = LocalWindowSize.current
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = BorderGray.copy(alpha = 0.3f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info",
                tint = TrafficRed,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Tu pedido generará un turno de atención",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = windowSize.adaptiveSp(16)
                    ),
                    color = DarkGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Acércate al mostrador cuando tu turno aparezca en pantalla.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = windowSize.adaptiveSp(14)
                    ),
                    color = MediumGray
                )
            }
        }
    }
}

@Composable
fun CartItemsList(items: List<CartItem>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items.forEach { item ->
            CartItemCard(item = item)
        }
    }
}

@Composable
fun CartItemCard(item: CartItem) {
    val windowSize = LocalWindowSize.current
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ExtraLightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Fastfood, contentDescription = null, tint = MediumGray)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = windowSize.adaptiveSp(16)
                    )
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = windowSize.adaptiveSp(14)
                    ),
                    color = MediumGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.price,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TrafficRed,
                        fontSize = windowSize.adaptiveSp(16)
                    )
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                QuantityButton(icon = Icons.Default.Remove, onClick = { /* Restar */ })
                Text(
                    text = item.quantity.toString(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = windowSize.adaptiveSp(16)
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                QuantityButton(icon = Icons.Default.Add, onClick = { /* Sumar */ })
            }
        }
    }
}

@Composable
fun QuantityButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Surface(
        shape = CircleShape,
        color = PrimaryOrange,
        modifier = Modifier
            .size(32.dp)
            .clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
fun PaymentMethodSection() {
    val windowSize = LocalWindowSize.current
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Método de Pago",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = windowSize.adaptiveSp(18)
                )
            )
            Text(
                text = "Editar",
                color = TrafficRed,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = windowSize.adaptiveSp(14)
                ),
                modifier = Modifier.clickable { /* Acción editar */ }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(40.dp, 28.dp)
                ) {
                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = DarkGray, modifier = Modifier.padding(4.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Visa terminada en 1234",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = windowSize.adaptiveSp(16)
                        )
                    )
                    Text(
                        text = "Expira 12/25",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = windowSize.adaptiveSp(14)
                        ),
                        color = MediumGray
                    )
                }

                Icon(Icons.Default.CheckCircle, contentDescription = "Seleccionado", tint = PrimaryOrange)
            }
        }
    }
}

@Composable
fun SummarySection() {
    val windowSize = LocalWindowSize.current
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Resumen",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = windowSize.adaptiveSp(18)
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SummaryRow(label = "Subtotal", amount = "$20.500")
            Spacer(modifier = Modifier.height(8.dp))
            SummaryRow(label = "Tarifa de servicio", amount = "$800")

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = BorderGray.copy(alpha = 0.5f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = windowSize.adaptiveSp(16)
                    )
                )
                Text(
                    text = "$21.300",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TrafficRed,
                        fontSize = windowSize.adaptiveSp(20)
                    )
                )
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, amount: String) {
    val windowSize = LocalWindowSize.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = windowSize.adaptiveSp(14)
            ),
            color = DarkGray
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = windowSize.adaptiveSp(14)
            )
        )
    }
}

// 👇 MODIFICAMOS EL BOTÓN PARA RECIBIR LA ACCIÓN DEL CLIC
@Composable
fun CartBottomBar(onPayClick: () -> Unit) {
    val windowSize = LocalWindowSize.current
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Button(
                onClick = { onPayClick() }, // Ejecutamos el disparo del modal
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Pagar y Generar Turno",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = windowSize.adaptiveSp(16)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}