package com.example.fila_virtual.features.user.billetera

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fila_virtual.features.user.UserViewModel

// 1. SOLUCIÓN: Nombres únicos para evitar conflicto con BilleteraScreen
private val FormOrange = Color(0xFFEA5B1C)
private val FormBgGray = Color(0xFFF8F9FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioTarjetaScreen(
    viewModel: UserViewModel,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FormBgGray)
            .padding(24.dp)
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Regresar",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Nueva Tarjeta",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50)
            )
        }

        // --- FORMULARIO ---
        // 1. Número de Tarjeta
        OutlinedTextField(
            value = viewModel.numeroTarjeta,
            onValueChange = { viewModel.onNumeroTarjetaChange(it) },
            label = { Text("Número de la tarjeta") },
            leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            // 2. SOLUCIÓN: Usamos OutlinedTextFieldDefaults
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FormOrange,
                focusedLabelColor = FormOrange
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Nombre del Titular
        OutlinedTextField(
            value = viewModel.nombreTitular,
            onValueChange = { viewModel.onNombreTitularChange(it) },
            label = { Text("Nombre como aparece en la tarjeta") },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FormOrange,
                focusedLabelColor = FormOrange
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Fila para Fecha y CVV
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = viewModel.fechaExpiracion,
                onValueChange = { viewModel.onFechaExpiracionChange(it) },
                label = { Text("Vencimiento") },
                placeholder = { Text("MMAA") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FormOrange,
                    focusedLabelColor = FormOrange
                ),
                singleLine = true
            )

            OutlinedTextField(
                value = viewModel.cvv,
                onValueChange = { viewModel.onCvvChange(it) },
                label = { Text("CVV") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FormOrange,
                    focusedLabelColor = FormOrange
                ),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.weight(1f)) // Empuja el botón hasta abajo

        // --- BOTÓN DE GUARDAR/PAGAR ---
        Button(
            onClick = { viewModel.procesarPagoSeguro() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FormOrange),
            shape = RoundedCornerShape(28.dp),
            enabled = !viewModel.isLoading // Se desactiva si está cargando
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Vincular Tarjeta",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // --- MANEJO DE ERRORES ---
        if (viewModel.errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = viewModel.errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}