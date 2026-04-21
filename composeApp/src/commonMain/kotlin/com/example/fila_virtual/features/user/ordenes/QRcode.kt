package com.example.fila_virtual.features.user.ordenes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fila_virtual.core.theme.PrimaryOrange

@Composable
fun QRCodeModalContent(
    turno: String,
    pedidoId: String,
    onDownloadClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // 2. Número de Turno
        Text(
            text = "TURNO #$turno",
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = PrimaryOrange
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 3. Subtítulo
        Text(
            text = "CÓDIGO DE RECOGIDA",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Contenedor del QR
        Box(
            modifier = Modifier
                .size(180.dp)
                .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Aquí en un futuro puedes poner una Image con el QR real
            // Por ahora usamos un ícono gigante para simularlo
            Icon(
                imageVector = Icons.Default.QrCode2,
                contentDescription = "Código QR",
                modifier = Modifier.fillMaxSize(),
                tint = Color.DarkGray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Número de Pedido
        Text(
            text = "Pedido #$pedidoId",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 6. Lista de Productos (Ejemplo estático basado en tu imagen)
        OrderItemRow(icon = Icons.Default.Fastfood, text = "1x Burger King Special")
        Spacer(modifier = Modifier.height(8.dp))
        OrderItemRow(icon = Icons.Default.LocalDrink, text = "1x Refresco Mediano")

        Spacer(modifier = Modifier.height(24.dp))

        // 7. Botón Descargar Comprobante
        OutlinedButton(
            onClick = { onDownloadClick() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, PrimaryOrange),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryOrange)
        ) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "Descargar",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Descargar Comprobante", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 8. Botón Cerrar
        Button(
            onClick = { onCloseClick() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
        ) {
            Text(text = "Cerrar", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Sub-componente para dibujar cada fila de producto de forma bonita
@Composable
fun OrderItemRow(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PrimaryOrange.copy(alpha = 0.05f)) // Fondo muy clarito
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cuadrito para el icono
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PrimaryOrange.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryOrange,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 15.sp,
            color = Color(0xFF333333), // DarkGray
            fontWeight = FontWeight.Medium
        )
    }
}