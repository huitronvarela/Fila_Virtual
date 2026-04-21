package com.example.fila_virtual.features.user.billetera

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// Agregamos 'private' para evitar conflictos con FormularioTarjetaScreen
private val BrandOrange = Color(0xFFEA5B1C)
private val MPBlue = Color(0xFF009EE3)
private val LightBlueBg = Color(0xFFE1F5FE)
private val BackgroundGray = Color(0xFFF8F9FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BilleteraScreen(
    onNavigateToFormulario: () -> Unit = {} // <-- 1. Recibimos la instrucción de navegación
) {
    var selectedCard by remember { mutableStateOf("Mercado Pago") }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ArrowBack, "Regresar", Modifier.size(24.dp).clickable { })
                Spacer(Modifier.width(16.dp))
                Text("Métodos de Pago", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)
            ) {
                item {
                    Text("Tus métodos guardados", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp))
                }

                item {
                    // Opción de Mercado Pago ya integrada
                    PaymentItem(
                        title = "Mercado Pago",
                        subtitle = "Pago rápido y seguro",
                        icon = Icons.Default.AccountBalanceWallet,
                        iconColor = MPBlue,
                        isSelected = selectedCard == "Mercado Pago",
                        onClick = { selectedCard = "Mercado Pago" }
                    )
                    Spacer(Modifier.height(12.dp))
                    PaymentItem(
                        title = "Visa •••• 4582",
                        subtitle = "Expira 12/26",
                        icon = Icons.Default.CreditCard,
                        iconColor = Color(0xFF1A1F71),
                        isSelected = selectedCard == "Visa",
                        onClick = { selectedCard = "Visa" }
                    )
                }

                item {
                    Spacer(Modifier.height(40.dp))
                    Button(
                        onClick = { showBottomSheet = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Icon(Icons.Default.Add, null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Añadir método de pago", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                AddMPMethodContent(
                    onAddCard = {
                        // <-- 2. Cerramos la ventana modal y llamamos a la pantalla del formulario
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            showBottomSheet = false
                            onNavigateToFormulario()
                        }
                    },
                    onConnectMP = {
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            showBottomSheet = false
                            // Aquí lanzaremos el Checkout Pro después
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AddMPMethodContent(onAddCard: () -> Unit, onConnectMP: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 48.dp, top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("¿Cómo quieres pagar?", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 24.dp))

        // Opción Mercado Pago (Branding oficial)
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onConnectMP() },
            colors = CardDefaults.cardColors(containerColor = LightBlueBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(Color.White), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Bolt, null, tint = MPBlue) // Ícono de rayo para "Pago rápido"
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Mercado Pago", fontWeight = FontWeight.Bold, color = MPBlue)
                    Text("Dinero en cuenta y cuotas", fontSize = 12.sp, color = MPBlue.copy(alpha = 0.7f))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Opción Tarjeta
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onAddCard() },
            colors = CardDefaults.cardColors(containerColor = BackgroundGray),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(Color.White), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.CreditCard, null, tint = Color.Gray)
                }
                Spacer(Modifier.width(16.dp))
                Text("Nueva Tarjeta", fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun PaymentItem(title: String, subtitle: String, icon: ImageVector, iconColor: Color, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(BackgroundGray), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = iconColor)
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Bold)
                    Text(subtitle, color = Color.Gray, fontSize = 14.sp)
                }
            }
            // Selector circular
            Box(Modifier.size(24.dp).clip(CircleShape).background(if (isSelected) MPBlue.copy(alpha = 0.1f) else Color.Transparent).padding(4.dp), contentAlignment = Alignment.Center) {
                if (isSelected) Box(Modifier.size(12.dp).clip(CircleShape).background(MPBlue))
                else Surface(Modifier.fillMaxSize(), shape = CircleShape, color = Color.Transparent, border = BorderStroke(2.dp, Color.LightGray)) { }
            }
        }
    }
}