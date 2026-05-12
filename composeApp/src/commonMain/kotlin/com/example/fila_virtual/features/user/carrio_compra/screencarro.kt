package com.example.fila_virtual.features.user.carrio_compra

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Importamos el ViewModel que está un nivel arriba
import com.example.fila_virtual.features.user.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: UserViewModel,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F7))
    ) {
        TopAppBar(
            title = { Text("Tu Pedido en AlToque", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        Column(modifier = Modifier.padding(24.dp)) {
            // TICKET DE COMPRA
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Fastfood, contentDescription = null, tint = Color(0xFFEA5B1C))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cafetería El Naranjo", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("1x Orden de Chilaquiles", color = Color.DarkGray)
                        Text("$45.00", fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("1x Coca-Cola 600ml", color = Color.DarkGray)
                        Text("$20.00", fontWeight = FontWeight.Medium)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total a pagar", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("$65.00", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFEA5B1C))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // BOTÓN DE COBRO AL SERVIDOR
            Button(
                onClick = { viewModel.realizarCobroConCloudFunction(65.0, "Pedido El Naranjo") },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA5B1C)),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Procesando...", color = Color.White)
                } else {
                    Icon(Icons.Default.Receipt, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirmar y Pagar $65.00", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (viewModel.errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (viewModel.errorMessage.contains("exitoso")) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
                        .padding(12.dp)
                ) {
                    Text(
                        text = viewModel.errorMessage,
                        color = if (viewModel.errorMessage.contains("exitoso")) Color(0xFF2E7D32) else Color(0xFFC62828),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}