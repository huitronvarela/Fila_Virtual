package com.example.fila_virtual.features.user.carrio_compra

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fila_virtual.core.LocalWindowSize
import com.example.fila_virtual.core.theme.*
import com.example.fila_virtual.data.ProductoCarrito
import com.example.fila_virtual.data.TarjetaGuardada

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
            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onBackground)
        }
        Text("Carrito", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center)
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
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.Info, contentDescription = "Info", tint = TrafficRed, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Tu pedido generará un turno de atención",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = windowSize.adaptiveSp(16)),
                    color = DarkGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Acércate al mostrador cuando tu turno aparezca en pantalla.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = windowSize.adaptiveSp(14)),
                    color = MediumGray
                )
            }
        }
    }
}

@Composable
fun CartItemsList(items: List<ProductoCarrito>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items.forEach { item -> CartItemCard(item = item) }
    }
}

@Composable
fun CartItemCard(item: ProductoCarrito) {
    val windowSize = LocalWindowSize.current
    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)).background(ExtraLightGray), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Fastfood, contentDescription = null, tint = MediumGray)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.nombre, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = windowSize.adaptiveSp(16)))
                Spacer(modifier = Modifier.height(4.dp))
                Text("$${item.precio}", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = TrafficRed, fontSize = windowSize.adaptiveSp(16)))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                QuantityButton(icon = Icons.Default.Remove, onClick = { })
                Text(item.cantidad.toString(), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = windowSize.adaptiveSp(16)), modifier = Modifier.padding(horizontal = 12.dp))
                QuantityButton(icon = Icons.Default.Add, onClick = { })
            }
        }
    }
}

@Composable
fun QuantityButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Surface(shape = CircleShape, color = PrimaryOrange, modifier = Modifier.size(32.dp).clickable { onClick() }) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.padding(4.dp))
    }
}

@Composable
fun PaymentMethodSection(tarjeta: TarjetaGuardada?, onEditClick: () -> Unit) {
    val windowSize = LocalWindowSize.current
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Método de Pago", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = windowSize.adaptiveSp(18)))
            Text("Editar", color = TrafficRed, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = windowSize.adaptiveSp(14)), modifier = Modifier.clickable { onEditClick() })
        }
        Spacer(modifier = Modifier.height(12.dp))

        Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth().clickable { onEditClick() }) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(6.dp), border = BorderStroke(1.dp, BorderGray), color = MaterialTheme.colorScheme.surface, modifier = Modifier.size(40.dp, 28.dp)) {
                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = DarkGray, modifier = Modifier.padding(4.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    if (tarjeta != null) {
                        Text("${tarjeta.marca} terminada en ${tarjeta.ultimos4}", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium, fontSize = windowSize.adaptiveSp(16)))
                        Text("Expira ${tarjeta.expiracion}", style = MaterialTheme.typography.bodyMedium.copy(fontSize = windowSize.adaptiveSp(14)), color = MediumGray)
                    } else {
                        Text("Sin método de pago", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = TrafficRed, fontSize = windowSize.adaptiveSp(16)))
                        Text("Toca para seleccionar o agregar", style = MaterialTheme.typography.bodyMedium.copy(fontSize = windowSize.adaptiveSp(14)), color = MediumGray)
                    }
                }
                if (tarjeta != null) Icon(Icons.Default.CheckCircle, contentDescription = "Seleccionado", tint = PrimaryOrange)
            }
        }
    }
}

@Composable
fun SummarySection(subtotal: Double, tarifa: Double, total: Double) {
    val windowSize = LocalWindowSize.current
    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Resumen", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = windowSize.adaptiveSp(18)), modifier = Modifier.padding(bottom = 16.dp))
            SummaryRow(label = "Subtotal", amount = "$${subtotal}")
            Spacer(modifier = Modifier.height(8.dp))
            SummaryRow(label = "Tarifa de servicio", amount = "$${tarifa}")
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = BorderGray.copy(alpha = 0.5f))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = windowSize.adaptiveSp(16)))
                Text("$${total}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = TrafficRed, fontSize = windowSize.adaptiveSp(20)))
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, amount: String) {
    val windowSize = LocalWindowSize.current
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium.copy(fontSize = windowSize.adaptiveSp(14)), color = DarkGray)
        Text(amount, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium, fontSize = windowSize.adaptiveSp(14)))
    }
}

@Composable
fun CartBottomBar(isProcessing: Boolean, errorMessage: String, isEnabled: Boolean, onPayClick: () -> Unit) {
    val windowSize = LocalWindowSize.current
    Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = TrafficRed, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth(), textAlign = TextAlign.Center)
            }
            Button(
                onClick = { onPayClick() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange, disabledContainerColor = MediumGray),
                shape = RoundedCornerShape(16.dp),
                enabled = !isProcessing && isEnabled
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Text("Pagar y Generar Turno", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White, fontSize = windowSize.adaptiveSp(16)))
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorTarjetasModal(
    tarjetas: List<TarjetaGuardada>,
    tarjetaActual: TarjetaGuardada?,
    onTarjetaSelected: (TarjetaGuardada) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 48.dp)) {
            Text("Selecciona método de pago", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(bottom = 16.dp))

            if (tarjetas.isEmpty()) {
                Text("No tienes tarjetas vinculadas. Agrega una desde la pestaña Billetera.", color = MediumGray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(tarjetas.size) { index ->
                        val tarjeta = tarjetas[index]
                        val isSelected = tarjeta.ultimos4 == tarjetaActual?.ultimos4

                        Card(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { onTarjetaSelected(tarjeta) },
                            colors = CardDefaults.cardColors(containerColor = if (isSelected) PrimaryOrange.copy(alpha = 0.1f) else ExtraLightGray),
                            border = if (isSelected) BorderStroke(2.dp, PrimaryOrange) else null
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CreditCard, contentDescription = null, tint = if (isSelected) PrimaryOrange else DarkGray)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "${tarjeta.marca} terminada en ${tarjeta.ultimos4}", fontWeight = FontWeight.Bold)
                                    Text(text = "Expira ${tarjeta.expiracion}", color = MediumGray, style = MaterialTheme.typography.bodySmall)
                                }
                                if (isSelected) Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryOrange)
                            }
                        }
                    }
                }
            }
        }
    }
}