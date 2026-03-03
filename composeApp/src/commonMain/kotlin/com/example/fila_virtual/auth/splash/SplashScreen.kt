package com.example.fila_virtual.auth.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource

// Importaciones de tus recursos
import fila_virtual.composeapp.generated.resources.Res
import fila_virtual.composeapp.generated.resources.logo

// Importaciones de tu nueva arquitectura
import com.example.fila_virtual.core.components.ActionButton
import com.example.fila_virtual.core.components.SocialLoginBlock
import com.example.fila_virtual.core.components.NavigationLink
import com.example.fila_virtual.core.theme.TrafficGreen

@Composable
fun SplashScreen(
    onGetStarted: () -> Unit,
    onSignUp: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Título de la app
        Row(modifier = Modifier.padding(top = 16.dp)) {
            Text(
                text = "Fila ",
                // Color automático: Negro de día, Blanco de noche
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
            Text(
                text = "Virtual",
                // Usamos el verde de tu semáforo (TrafficGreen)
                color = TrafficGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 2. Logo
        Image(
            painter = painterResource(Res.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.weight(1f))

        // 3. Botón de inicio de sesión / Comenzar
        ActionButton(
            text = "Comenzar",
            onClick = onGetStarted,
            isLoading = false
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 4. Bloque de Redes Sociales (Actualizado: Solo Google y Apple adaptable)
        SocialLoginBlock()

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Enlace al registro
        NavigationLink(
            textMain = "¿No tienes una cuenta? ",
            textLink = "Regístrate",
            onClick = onSignUp
        )
    }
}