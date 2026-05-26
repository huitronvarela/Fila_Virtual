package com.example.fila_virtual.features.user.carrio_compra

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.fila_virtual.core.LocalWindowSize
import com.example.fila_virtual.core.theme.*
import com.example.fila_virtual.features.user.UserViewModel

// --- MODELOS DE DATOS DE PRUEBA ---
data class CartItem(val name: String, val description: String, val price: String, val quantity: Int)

@Composable
fun CartScreen(
    onBackClick: () -> Unit,
    onOrderSuccess: () -> Unit,
    viewModel: UserViewModel
) {
    val windowSize = LocalWindowSize.current
    val padding = windowSize.adaptiveDp(16).value.dp

    // Datos simulados
    val cartItems = listOf(
        CartItem("Doble Smash Burger", "Sin cebolla", "$8.500", 1),
        CartItem("Pizza Margarita", "Masa madre", "$12.000", 1)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { CartTopBar(onBackClick = onBackClick) },
        bottomBar = {
            CartBottomBar(
                isProcessing = viewModel.isLoading,
                errorMessage = viewModel.errorMessage, // <-- AQUÍ FALTABA ESTO
                onPayClick = {
                    // Evita doble clic si ya está cargando
                    if (viewModel.isLoading) return@CartBottomBar

                    // LLAMADA REAL AL BACKEND
                    viewModel.crearPedidoYCobrar(
                        montoTotal = 21.30, // Monto de prueba
                        descripcion = "Orden en AlToque",
                        establecimientoId = "local_prueba_123",
                        establecimientoNombre = "Pizzería Napoli",
                        onSuccess = {
                            onOrderSuccess()
                        }
                    )
                }
            )
        }
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

@Composable // <-- AQUÍ ESTABA EL DOBLE @Composable, YA LO QUITÉ
fun CartBottomBar(isProcessing: Boolean, errorMessage: String, onPayClick: () -> Unit) {
    val windowSize = LocalWindowSize.current
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // --- AQUÍ MOSTRAMOS EL ERROR EN ROJO SI EXISTE ---
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = TrafficRed,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = { onPayClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(16.dp),
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
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
}