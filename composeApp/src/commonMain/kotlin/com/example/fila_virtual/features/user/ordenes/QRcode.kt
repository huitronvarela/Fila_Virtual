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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fila_virtual.core.theme.*

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
            .background(LightSurface)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // 2. Número de Turno
        Text(
            text = "TURNO #$turno",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 3. Subtítulo
        Text(
            text = "CÓDIGO DE RECOGIDA",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MediumGray,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Contenedor del QR
        Box(
            modifier = Modifier
                .size(180.dp)
                .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Aquí en un futuro puedes poner una Image con el QR real
            // Por ahora usamos un ícono gigante para simularlo
            Icon(
                imageVector = Icons.Default.QrCode2,
                contentDescription = "Código QR",
                modifier = Modifier.fillMaxSize(),
                tint = DarkGray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Número de Pedido
        Text(
            text = "Pedido #$pedidoId",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
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
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "Descargar",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Descargar Comprobante",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 8. Botón Cerrar
        Button(
            onClick = { onCloseClick() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "Cerrar",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
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
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)) // Fondo muy clarito
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cuadrito para el icono
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}