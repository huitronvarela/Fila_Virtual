package com.example.fila_virtual.features.user.billetera

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.fila_virtual.features.user.UserViewModel
import com.example.fila_virtual.core.LocalWindowSize

// Colores
private val MPBlue = Color(0xFF009EE3)
private val LightBlueBg = Color(0xFFE1F5FE)
private val OrangeGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFE94E1B), Color(0xFFF26522))
)

enum class BottomSheetStateView {
    SELECCION_METODO,
    FORMULARIO_TARJETA
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BilleteraScreen(
    viewModel: UserViewModel
) {
    val windowSize = LocalWindowSize.current
    val typography = MaterialTheme.typography

    val usuario = viewModel.usuario
    val metodosPago = usuario?.metodosPago ?: emptyList()

    var showBottomSheet by remember { mutableStateOf(false) }
    var currentSheetView by remember { mutableStateOf(BottomSheetStateView.SELECCION_METODO) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    // 1. SOLUCIÓN NAVBAR DOBLE: Quitamos el Scaffold simulado y dejamos solo un Column principal
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Métodos de Pago",
                    style = typography.titleLarge.copy(
                        fontSize = windowSize.adaptiveSp(20),
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.Black
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                // Tarjetas vinculadas
                items(metodosPago) { tarjeta ->
                    CreditCardView(
                        cardNumber = tarjeta.ultimos4,
                        cardHolder = tarjeta.nombreTitular,
                        expiryDate = tarjeta.expiracion,
                        cardBrand = tarjeta.marca
                    )
                    Spacer(Modifier.height(16.dp))
                }

                if (metodosPago.isEmpty()) {
                    item {
                        Text("No tienes tarjetas vinculadas", color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))
                    }
                }

                // Mercado Pago
                item {
                    LinkedMethodView(
                        icon = Icons.Default.AccountBalanceWallet,
                        title = "Mercado Pago",
                        status = "Cuenta vinculada"
                    )
                    Spacer(Modifier.height(40.dp))
                }

                // Botón Naranja
                item {
                    Button(
                        onClick = {
                            currentSheetView = BottomSheetStateView.SELECCION_METODO
                            showBottomSheet = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(OrangeGradient, RoundedCornerShape(12.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Añadir nuevo método de pago",
                                style = typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = windowSize.adaptiveSp(16)
                                ),
                                color = Color.White
                            )
                        }
                    }
                    Spacer(Modifier.height(100.dp)) // Espacio extra al final para que no lo tape la navbar de tu app
                }
            }
        }

        // Modal Bottom Sheet
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                when (currentSheetView) {
                    BottomSheetStateView.SELECCION_METODO -> {
                        AddMPMethodContent(
                            onAddCard = { currentSheetView = BottomSheetStateView.FORMULARIO_TARJETA },
                            onConnectMP = {
                                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion { showBottomSheet = false }
                            }
                        )
                    }
                    BottomSheetStateView.FORMULARIO_TARJETA -> {
                        FormularioTarjetaScreen(
                            viewModel = viewModel,
                            onSuccess = {
                                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion { showBottomSheet = false }
                            }
                        )
                    }
                }
            }
        }
    }
}

// ==============================================================================
// 2. SOLUCIÓN HALO DE LA TARJETA
// ==============================================================================

@Composable
fun CreditCardView(cardNumber: String, cardHolder: String, expiryDate: String, cardBrand: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.58f), // <-- Aquí quitamos el .background() que rompía la sombra
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        // Envolvemos el contenido en un Box para pintar el degradado por dentro
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(OrangeGradient) // <-- El degradado va aquí adentro
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Fila superior: Chip y Logo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CreditCard, contentDescription = "Chip", tint = Color.White, modifier = Modifier.size(32.dp))
                    Text(text = cardBrand, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, letterSpacing = 1.sp)
                }

                // Fila central: Número de tarjeta
                Text(
                    text = "••••  ••••  ••••  $cardNumber",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    letterSpacing = 3.sp
                )

                // Fila inferior: Nombre y Expiración
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(text = "CARDHOLDER", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = cardHolder.uppercase(), color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "EXPIRES", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = expiryDate, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun LinkedMethodView(icon: ImageVector, title: String, status: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MPBlue),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).background(Color.White, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = MPBlue, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = status, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.White)
        }
    }
}

@Composable
fun AddMPMethodContent(onAddCard: () -> Unit, onConnectMP: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "¿Cómo quieres pagar?", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(bottom = 24.dp))

        Card(
            modifier = Modifier.fillMaxWidth().clickable { onConnectMP() },
            colors = CardDefaults.cardColors(containerColor = LightBlueBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Bolt, null, tint = MPBlue)
                Spacer(Modifier.width(16.dp))
                Text(text = "Mercado Pago", fontWeight = FontWeight.Bold, color = MPBlue)
            }
        }

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth().clickable { onAddCard() },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CreditCard, null, tint = Color.Gray)
                Spacer(Modifier.width(16.dp))
                Text(text = "Nueva Tarjeta", fontWeight = FontWeight.Medium)
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}