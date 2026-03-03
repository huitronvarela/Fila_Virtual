package com.example.fila_virtual.core.components

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

// Importaciones de recursos
import fila_virtual.composeapp.generated.resources.Res
import fila_virtual.composeapp.generated.resources.*

@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label, 
            fontWeight = FontWeight.Bold, 
            color = MaterialTheme.colorScheme.onBackground, 
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value, 
            onValueChange = onValueChange, 
            placeholder = { Text(placeholder, color = Color.Gray) }, 
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(12.dp), 
            singleLine = true
        )
    }
}

@Composable
fun PasswordInputField(label: String, value: String, onValueChange: (String) -> Unit, passwordVisible: Boolean, onVisibilityChange: (Boolean) -> Unit, placeholder: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label, 
            fontWeight = FontWeight.Bold, 
            color = MaterialTheme.colorScheme.onBackground, 
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value, 
            onValueChange = onValueChange, 
            placeholder = { Text(placeholder, color = Color.Gray) }, 
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(12.dp), 
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Ocultar" else "Mostrar"
                IconButton(onClick = { onVisibilityChange(!passwordVisible) }) { 
                    Icon(imageVector = image, contentDescription = description, tint = Color.Gray) 
                }
            }
        )
    }
}

@Composable
fun TermsCheckbox(termsAccepted: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = termsAccepted, 
            onCheckedChange = onCheckedChange, 
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
        )
        val annotatedString = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Gray)) { 
                append(stringResource(Res.string.terms_accept)) 
            }
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) { 
                append(stringResource(Res.string.terms_user_agreement)) 
            }
            withStyle(style = SpanStyle(color = Color.Gray)) { 
                append(stringResource(Res.string.terms_and)) 
            }
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) { 
                append(stringResource(Res.string.terms_privacy_policy)) 
            }
        }
        Text(text = annotatedString, fontSize = 13.sp, modifier = Modifier.padding(start = 4.dp), lineHeight = 18.sp)
    }
}

@Composable
fun ActionButton(text: String, isLoading: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(25.dp),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
        } else {
            Text(text = text, fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SocialLoginBlock() {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
            Text(
                text = stringResource(Res.string.social_login_label), 
                modifier = Modifier.padding(horizontal = 16.dp), 
                color = MaterialTheme.colorScheme.onBackground, 
                fontWeight = FontWeight.Bold, 
                fontSize = 14.sp
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .border(1.dp, Color.LightGray, CircleShape)
                    .clickable { /* Lógica Google */ }
                    .padding(12.dp), 
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_google_logo),
                    contentDescription = "Google"
                )
            }
            
            Spacer(modifier = Modifier.width(24.dp))
            
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .border(1.dp, Color.LightGray, CircleShape)
                    .clickable { /* Lógica Apple */ }
                    .padding(12.dp), 
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_appel_logo),
                    contentDescription = "Apple",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )
            }
        }
    }
}

@Composable
fun SocialButton(text: String, color: Color) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .background(MaterialTheme.colorScheme.surface, CircleShape)
            .border(1.dp, Color.LightGray, CircleShape)
            .clickable { /* Lógica social */ }, 
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = color, fontSize = 26.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun NavigationLink(textMain: String, textLink: String, onClick: () -> Unit) {
    Row(modifier = Modifier.padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = textMain, color = Color.Gray)
        Text(
            text = textLink, 
            color = MaterialTheme.colorScheme.primary, 
            fontWeight = FontWeight.Bold, 
            fontSize = 16.sp, 
            modifier = Modifier.clickable(onClick = onClick).padding(start = 4.dp)
        )
    }
}