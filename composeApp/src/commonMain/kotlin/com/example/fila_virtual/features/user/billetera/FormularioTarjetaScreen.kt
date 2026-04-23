package com.example.fila_virtual.features.user.billetera

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fila_virtual.features.user.UserViewModel

private val BrandOrange = Color(0xFFEA5B1C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioTarjetaScreen(
    viewModel: UserViewModel,
    onSuccess: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 32.dp, top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ingresa tu tarjeta",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Número de Tarjeta con máscara (separado de 4 en 4)
        OutlinedTextField(
            value = viewModel.numeroTarjeta,
            onValueChange = { viewModel.onNumeroTarjetaChange(it.filter { char -> char.isDigit() }) },
            label = { Text("Número de la tarjeta") },
            leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = CardNumberVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nombre del Titular
        OutlinedTextField(
            value = viewModel.nombreTitular,
            onValueChange = { viewModel.onNombreTitularChange(it.uppercase()) },
            label = { Text("Nombre como aparece en la tarjeta") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Fecha con máscara (MM/YY)
            OutlinedTextField(
                value = viewModel.fechaExpiracion,
                onValueChange = { viewModel.onFechaExpiracionChange(it.filter { char -> char.isDigit() }) },
                label = { Text("Vencimiento") },
                placeholder = { Text("MMAA") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = ExpirationDateVisualTransformation(),
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )

            // CVV
            OutlinedTextField(
                value = viewModel.cvv,
                onValueChange = { viewModel.onCvvChange(it.filter { char -> char.isDigit() }) },
                label = { Text("CVV") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Mensaje de error/éxito del ViewModel
        if (viewModel.errorMessage.isNotEmpty()) {
            Text(
                text = viewModel.errorMessage,
                color = if (viewModel.errorMessage.contains("correctamente")) Color(0xFF4CAF50) else Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                viewModel.procesarPagoSeguro()
                // Nota: Podrías llamar onSuccess() aquí si quieres cerrar el panel automáticamente al terminar
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
            shape = RoundedCornerShape(28.dp),
            enabled = !viewModel.isLoading && viewModel.numeroTarjeta.length == 16 && viewModel.cvv.isNotEmpty()
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Vincular Tarjeta", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

// ==============================================================================
// UTILIDADES: Transformaciones visuales para aplicar las máscaras en tiempo real
// ==============================================================================

class CardNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 16) text.text.substring(0..15) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i % 4 == 3 && i != 15) out += " " // Agrega espacio cada 4 números
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 3) return offset
                if (offset <= 7) return offset + 1
                if (offset <= 11) return offset + 2
                if (offset <= 16) return offset + 3
                return 19
            }
            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 4) return offset
                if (offset <= 9) return offset - 1
                if (offset <= 14) return offset - 2
                if (offset <= 19) return offset - 3
                return 16
            }
        }
        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

class ExpirationDateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 4) text.text.substring(0..3) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]

            // CORRECCIÓN: Si llegamos al segundo número (índice 1), SIEMPRE agregamos el slash.
            // Ya no le exigimos que la longitud sea mayor a 2.
            if (i == 1) out += "/"
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 4) return offset + 1
                return 5
            }
            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                return 4
            }
        }
        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}