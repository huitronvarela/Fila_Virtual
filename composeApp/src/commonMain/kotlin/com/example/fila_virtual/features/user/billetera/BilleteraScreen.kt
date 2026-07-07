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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.fila_virtual.features.user.UserViewModel
import com.example.fila_virtual.core.LocalWindowSize
import com.example.fila_virtual.core.theme.*
import androidx.compose.runtime.saveable.rememberSaveable

// IMPORTS PARA LOS RECURSOS DE TRADUCCIÓN
import org.jetbrains.compose.resources.stringResource
import fila_virtual.composeapp.generated.resources.* // <-- Asegúrate de que este sea tu import de Res

// Colores de marca
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
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val usuario = viewModel.usuario
    val metodosPago = usuario?.metodosPago ?: emptyList()

    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    var currentSheetView by rememberSaveable { mutableStateOf(BottomSheetStateView.SELECCION_METODO) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            // Header centrado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(Res.string.wallet_payment_methods_title),
                    style = typography.titleLarge.copy(
                        fontSize = windowSize.adaptiveSp(20),
                        fontWeight = FontWeight.Bold
                    ),
                    color = colorScheme.onBackground
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                // Tarjetas vinculadas como CreditCardView
                items(metodosPago) { tarjeta ->
                    CreditCardView(
                        cardNumber = tarjeta.ultimos4,
                        cardHolder = tarjeta.nombreTitular,
                        expiryDate = tarjeta.expiracion,
                        cardBrand = tarjeta.marca
                    )
                    Spacer(Modifier.height(16.dp))
                }

                // Mensaje si no hay tarjetas
                if (metodosPago.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(Res.string.wallet_no_linked_cards),
                            style = typography.bodyMedium.copy(
                                fontSize = windowSize.adaptiveSp(14)
                            ),
                            color = MediumGray,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }

                // Card de Mercado Pago vinculado
                item {
                    LinkedMethodView(
                        icon = Icons.Default.AccountBalanceWallet,
                        title = "Mercado Pago",
                        status = stringResource(Res.string.wallet_linked_account)
                    )
                    Spacer(Modifier.height(40.dp))
                }

                // Botón con degradado naranja
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
                                text = stringResource(Res.string.wallet_add_new_method),
                                style = typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = windowSize.adaptiveSp(16)
                                ),
                                color = Color.White
                            )
                        }
                    }
                    Spacer(Modifier.height(100.dp))
                }
            }
        }

        // Modal Bottom Sheet
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = colorScheme.surface
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

@Composable
fun CreditCardView(cardNumber: String, cardHolder: String, expiryDate: String, cardBrand: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.58f),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(OrangeGradient)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CreditCard,
                        contentDescription = "Chip",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = cardBrand.ifBlank { "CARD" },
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "••••  ••••  ••••  $cardNumber",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    letterSpacing = 3.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "CARDHOLDER",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 10.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = cardHolder.uppercase().ifBlank { "—" },
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "EXPIRES",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 10.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = expiryDate.ifBlank { "—" },
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LinkedMethodView(icon: ImageVector, title: String, status: String) {
    val windowSize = LocalWindowSize.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MPBlue),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = MPBlue, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = windowSize.adaptiveSp(18)
                    )
                    Text(
                        text = status,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = windowSize.adaptiveSp(14)
                    )
                }
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.White)
        }
    }
}

@Composable
fun AddMPMethodContent(onAddCard: () -> Unit, onConnectMP: () -> Unit) {
    val windowSize = LocalWindowSize.current
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 48.dp, top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.wallet_how_to_pay),
            style = typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = windowSize.adaptiveSp(20)
            ),
            modifier = Modifier.padding(bottom = 24.dp),
            color = colorScheme.onSurface
        )

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
                        text = stringResource(Res.string.wallet_mp_subtitle),
                        style = typography.bodySmall.copy(
                            fontSize = windowSize.adaptiveSp(12)
                        ),
                        color = MPBlue.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

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
                    text = stringResource(Res.string.wallet_new_card),
                    style = typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = windowSize.adaptiveSp(16)
                    ),
                    color = colorScheme.onSurface
                )
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}