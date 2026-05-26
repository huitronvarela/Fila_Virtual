package com.example.fila_virtual.features.user.billetera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fila_virtual.features.user.UserViewModel
import com.example.fila_virtual.core.LocalWindowSize
import com.example.fila_virtual.core.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioTarjetaScreen(
    viewModel: UserViewModel,
    onSuccess: () -> Unit
) {
    val windowSize = LocalWindowSize.current
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    // Detectamos si el mensaje del ViewModel indica que fue un éxito
    val isSuccessMsg = viewModel.errorMessage.contains("correctamente", ignoreCase = true) ||
            viewModel.errorMessage.contains("éxito", ignoreCase = true)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 32.dp, top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isSuccessMsg) {
            // --- PANTALLA DE ÉXITO ESTANDARIZADA ---
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color(0xFFE8F5E9), CircleShape), // Fondo verde claro
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "¡Tarjeta vinculada!",
                style = typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = DarkGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tu método de pago se ha guardado correctamente y está listo para usarse en AlToque.",
                style = typography.bodyMedium,
                color = MediumGray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    // 1. Recargamos los datos del usuario ahora que estamos listos
                    viewModel.loadUserData()
                    // 2. Limpiamos el mensaje en el ViewModel para el futuro

                    // 3. Cerramos el Bottom Sheet
                    onSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Aceptar", style = typography.titleMedium.copy(color = Color.White))
            }
        } else {
            // --- FORMULARIO ORIGINAL DE CAPTURA ---
            Text(
                text = "Ingresa tu tarjeta",
                style = typography.titleLarge.copy(
                    fontSize = windowSize.adaptiveSp(20)
                ),
                color = colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Número de Tarjeta con máscara (separado de 4 en 4)
            OutlinedTextField(
                value = viewModel.numeroTarjeta,
                onValueChange = { viewModel.onNumeroTarjetaChange(it.filter { char -> char.isDigit() }) },
                label = { Text("Número de la tarjeta", style = typography.bodyMedium) },
                leadingIcon = {
                    Icon(
                        Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = MediumGray
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = CardNumberVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.primary,
                    unfocusedBorderColor = BorderGray,
                    focusedLabelColor = colorScheme.primary,
                    cursorColor = colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre del Titular
            OutlinedTextField(
                value = viewModel.nombreTitular,
                onValueChange = { viewModel.onNombreTitularChange(it.uppercase()) },
                label = { Text("Nombre como aparece en la tarjeta", style = typography.bodyMedium) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.primary,
                    unfocusedBorderColor = BorderGray,
                    focusedLabelColor = colorScheme.primary,
                    cursorColor = colorScheme.primary
                )
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
                    label = { Text("Vencimiento", style = typography.bodyMedium) },
                    placeholder = { Text("MMAA", style = typography.bodyMedium) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = ExpirationDateVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorScheme.primary,
                        unfocusedBorderColor = BorderGray,
                        focusedLabelColor = colorScheme.primary,
                        cursorColor = colorScheme.primary
                    )
                )

                // CVV
                OutlinedTextField(
                    value = viewModel.cvv,
                    onValueChange = { viewModel.onCvvChange(it.filter { char -> char.isDigit() }) },
                    label = { Text("CVV", style = typography.bodyMedium) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorScheme.primary,
                        unfocusedBorderColor = BorderGray,
                        focusedLabelColor = colorScheme.primary,
                        cursorColor = colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Mensaje de error (solo visible si no es un mensaje exitoso)
            if (viewModel.errorMessage.isNotEmpty()) {
                Text(
                    text = viewModel.errorMessage,
                    color = colorScheme.error,
                    style = typography.bodyMedium.copy(
                        fontSize = windowSize.adaptiveSp(14)
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = {
                    viewModel.procesarPagoSeguro()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !viewModel.isLoading && viewModel.numeroTarjeta.length == 16 && viewModel.cvv.isNotEmpty()
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        color = colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Vincular Tarjeta",
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
            if (i % 4 == 3 && i != 15) out += " "
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