package com.example.fila_virtual.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Importamos los colores de tu archivo Theme
import com.example.fila_virtual.core.theme.AppColors

@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.Bold, color = AppColors.darkGray, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(value = value, onValueChange = onValueChange, placeholder = { Text(placeholder, color = Color.LightGray) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true)
    }
}

@Composable
fun PasswordInputField(label: String, value: String, onValueChange: (String) -> Unit, passwordVisible: Boolean, onVisibilityChange: (Boolean) -> Unit, placeholder: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.Bold, color = AppColors.darkGray, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value, onValueChange = onValueChange, placeholder = { Text(placeholder, color = Color.LightGray) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Ocultar" else "Mostrar"
                IconButton(onClick = { onVisibilityChange(!passwordVisible) }) { Icon(imageVector = image, contentDescription = description, tint = Color.Gray) }
            }
        )
    }
}

@Composable
fun TermsCheckbox(termsAccepted: Boolean, onAcceptedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = termsAccepted, onCheckedChange = onAcceptedChange, colors = CheckboxDefaults.colors(checkedColor = AppColors.primaryOrange))
        val annotatedString = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Gray)) { append("He leído y acepto el ") }
            withStyle(style = SpanStyle(color = AppColors.primaryOrange, fontWeight = FontWeight.Bold)) { append("Acuerdo de Usuario") }
            withStyle(style = SpanStyle(color = Color.Gray)) { append(" y la\n") }
            withStyle(style = SpanStyle(color = AppColors.primaryOrange, fontWeight = FontWeight.Bold)) { append("Política de Privacidad") }
        }
        Text(text = annotatedString, fontSize = 13.sp, modifier = Modifier.padding(start = 4.dp), lineHeight = 18.sp)
    }
}

@Composable
fun ActionButton(text: String, isLoading: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AppColors.primaryOrange),
        shape = RoundedCornerShape(25.dp),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
        } else {
            Text(text = text, fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SocialLoginBlock(social1: String, social2: String, social3: String) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
            Text(text = "Ingresar con", modifier = Modifier.padding(horizontal = 16.dp), color = AppColors.darkGray, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            SocialButton(social1, Color(0xFF1877F2))
            Spacer(modifier = Modifier.width(24.dp))
            SocialButton(social2, Color(0xFFDB4437))
            Spacer(modifier = Modifier.width(24.dp))
            SocialButton(social3, Color.Black)
        }
    }
}

@Composable
fun SocialButton(text: String, color: Color) {
    Box(modifier = Modifier.size(54.dp).background(Color.White, CircleShape).border(1.dp, Color.LightGray, CircleShape).clickable { /* Lógica social */ }, contentAlignment = Alignment.Center) {
        Text(text = text, color = color, fontSize = 26.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun NavigationLink(textMain: String, textLink: String, onClick: () -> Unit) {
    Row(modifier = Modifier.padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = textMain, color = Color.Gray)
        Text(text = textLink, color = AppColors.primaryOrange, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.clickable(onClick = onClick))
    }
}