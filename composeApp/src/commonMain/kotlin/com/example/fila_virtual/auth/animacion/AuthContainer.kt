package com.example.fila_virtual.auth.animacion

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

// Importaciones de tus pantallas
import com.example.fila_virtual.auth.login.LoginScreen
import com.example.fila_virtual.auth.register.RegisterScreen
import com.example.fila_virtual.navigation.Screens
import fila_virtual.composeapp.generated.resources.Res
import fila_virtual.composeapp.generated.resources.*

@Composable
fun AuthContainer(
    currentScreen: Screens,
    onNavigate: (Screens) -> Unit,
    onGoogleSignIn: () -> Unit = {}
) {
    val isLogin = currentScreen == Screens.Login

    // Aumentamos un poco el padding superior para centrar mejor el contenido naranja
    val headerHeight = 200.dp
    
    val topPadding by animateDpAsState(
        targetValue = if (isLogin) headerHeight else 0.dp,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "animacion_altura"
    )

    val cornerRadius by animateDpAsState(
        targetValue = if (isLogin) 40.dp else 0.dp,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "animacion_bordes"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        // Cabecera centrada en el área naranja
        if (isLogin) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.logoblancot),
                    contentDescription = "Logo",
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(Res.string.app_name),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Tarjeta blanca animada (Formularios)
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topPadding),
            shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius),
            color = Color.White
        ) {
            Crossfade(
                targetState = isLogin,
                animationSpec = tween(600),
                modifier = Modifier.fillMaxSize(),
                label = "animacion_formularios"
            ) { showLogin ->
                if (showLogin) {
                    LoginScreen(
                        onNavigate = onNavigate,
                        onGoogleSignIn = onGoogleSignIn
                    )
                } else {
                    RegisterScreen(
                        onNavigate = onNavigate
                    )
                }
            }
        }
    }
}