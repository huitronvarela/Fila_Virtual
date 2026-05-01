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
import com.example.fila_virtual.features.user.UserViewModel
import com.example.fila_virtual.core.LocalWindowSize
import com.example.fila_virtual.core.theme.*

// Colores específicos de marca (fuera del tema general)
private val MPBlue = Color(0xFF009EE3)
private val LightBlueBg = Color(0xFFE1F5FE)

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
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    // 1. Extraemos al usuario y sus métodos de pago directamente desde el ViewModel (Firebase)
    val usuario = viewModel.usuario
    val metodosPago = usuario?.metodosPago ?: emptyList()

    var selectedCard by remember { mutableStateOf("Mercado Pago") }

    // Controles del Bottom Sheet
    var showBottomSheet by remember { mutableStateOf(false) }
    var currentSheetView by remember { mutableStateOf(BottomSheetStateView.SELECCION_METODO) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Navegación */ }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack, 
                        contentDescription = "Regresar", 
                        modifier = Modifier.size(24.dp),
                        tint = colorScheme.onBackground
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Métodos de Pago", 
                    style = typography.titleLarge.copy(
                        fontSize = windowSize.adaptiveSp(20)
                    ),
                    color = colorScheme.onBackground
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)
            ) {
                item {
                    Text(
                        text = "Tus métodos guardados", 
                        style = typography.titleMedium.copy(
                            fontSize = windowSize.adaptiveSp(18),
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = colorScheme.onBackground
                    )
                }

                item {
                    // Esta opción siempre aparece
                    PaymentItem(
                        title = "Mercado Pago",
                        subtitle = "Pago rápido y seguro",
                        icon = Icons.Default.AccountBalanceWallet,
                        iconColor = MPBlue,
                        isSelected = selectedCard == "Mercado Pago",
                        onClick = { selectedCard = "Mercado Pago" }
                    )

                    // 2. MAGIA: Mostramos todas las tarjetas guardadas en el array metodosPago
                    metodosPago.forEach { tarjeta ->
                        Spacer(Modifier.height(12.dp))
                        PaymentItem(
                            title = tarjeta,
                            subtitle = "Tarjeta vinculada",
                            icon = Icons.Default.CreditCard,
                            iconColor = Color(0xFF1A1F71), // Color de tarjeta genérica
                            isSelected = selectedCard == tarjeta,
                            onClick = { selectedCard = tarjeta }
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(40.dp))
                    Button(
                        onClick = {
                            currentSheetView = BottomSheetStateView.SELECCION_METODO
                            showBottomSheet = true
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Icon(Icons.Default.Add, null, tint = colorScheme.onPrimary)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Añadir método de pago", 
                            style = typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = windowSize.adaptiveSp(16)
                            ),
                            color = colorScheme.onPrimary
                        )
                    }
                }
            }
        }

        // Lógica del Panel Desplegable (ModalBottomSheet)
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = colorScheme.surface
            ) {
                when (currentSheetView) {
                    BottomSheetStateView.SELECCION_METODO -> {
                        AddMPMethodContent(
                            onAddCard = {
                                currentSheetView = BottomSheetStateView.FORMULARIO_TARJETA
                            },
                            onConnectMP = {
                                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                    showBottomSheet = false
                                }
                            }
                        )
                    }
                    BottomSheetStateView.FORMULARIO_TARJETA -> {
                        FormularioTarjetaScreen(
                            viewModel = viewModel,
                            onSuccess = {
                                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                    showBottomSheet = false
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddMPMethodContent(onAddCard: () -> Unit, onConnectMP: () -> Unit) {
    val windowSize = LocalWindowSize.current
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 48.dp, top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¿Cómo quieres pagar?", 
            style = typography.titleLarge.copy(
                fontSize = windowSize.adaptiveSp(20)
            ), 
            modifier = Modifier.padding(bottom = 24.dp),
            color = colorScheme.onSurface
        )

        // Opción Mercado Pago
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onConnectMP() },
            colors = CardDefaults.cardColors(containerColor = LightBlueBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colorScheme.surface), 
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Bolt, null, tint = MPBlue)
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Mercado Pago", 
                        style = typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = windowSize.adaptiveSp(16)
                        ),
                        color = MPBlue
                    )
                    Text(
                        text = "Dinero en cuenta y cuotas", 
                        style = typography.bodySmall.copy(
                            fontSize = windowSize.adaptiveSp(12)
                        ),
                        color = MPBlue.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Opción Tarjeta
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onAddCard() },
            colors = CardDefaults.cardColors(containerColor = colorScheme.background),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colorScheme.surface), 
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CreditCard, null, tint = MediumGray)
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    text = "Nueva Tarjeta", 
                    style = typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = windowSize.adaptiveSp(16)
                    ),
                    color = colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun PaymentItem(title: String, subtitle: String, icon: ImageVector, iconColor: Color, isSelected: Boolean, onClick: () -> Unit) {
    val windowSize = LocalWindowSize.current
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorScheme.background), 
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = iconColor)
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = title, 
                        style = typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = windowSize.adaptiveSp(16)
                        ),
                        color = colorScheme.onSurface
                    )
                    Text(
                        text = subtitle, 
                        style = typography.bodyMedium.copy(
                            fontSize = windowSize.adaptiveSp(14)
                        ),
                        color = MediumGray
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) MPBlue.copy(alpha = 0.1f) else Color.Transparent)
                    .padding(4.dp), 
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(Modifier.size(12.dp).clip(CircleShape).background(MPBlue))
                } else {
                    Surface(
                        modifier = Modifier.fillMaxSize(), 
                        shape = CircleShape, 
                        color = Color.Transparent,
                        border = BorderStroke(2.dp, BorderGray)
                    ) { }
                }
            }
        }
    }
}
