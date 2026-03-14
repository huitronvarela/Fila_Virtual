package com.example.fila_virtual.auth.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fila_virtual.components.ActionButton
import com.example.fila_virtual.components.InputField
import com.example.fila_virtual.components.NavigationLink
import com.example.fila_virtual.components.PasswordInputField
import com.example.fila_virtual.components.SocialLoginBlock
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

// Importaciones de Firebase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

// Importaciones de tu Logo
import fila_virtual.composeapp.generated.resources.Res
import fila_virtual.composeapp.generated.resources.*

// Importaciones de tu nueva arquitectura
import com.example.fila_virtual.navigation.Screens
import com.example.fila_virtual.core.mapFirebaseError
import com.example.fila_virtual.core.isValidEmail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigate: (Screens) -> Unit,
    onGoogleSignIn: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Estado para el Modal de recuperación de contraseña
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var recoveryEmail by remember { mutableStateOf("") }
    var recoveryMessage by remember { mutableStateOf("") }
    var isRecovering by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.btn_login),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = stringResource(Res.string.login_welcome),
            fontSize = 16.sp,
            color = Color(0xFF666666),
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        InputField(
            label = stringResource(Res.string.label_email),
            value = email,
            onValueChange = { email = it; errorMessage = "" },
            placeholder = stringResource(Res.string.placeholder_email),
            leadingIcon = Icons.Filled.Email,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            isError = email.isNotEmpty() && !isValidEmail(email)
        )

        if (email.isNotEmpty() && !isValidEmail(email)) {
            Text("Formato de correo inválido", color = MaterialTheme.colorScheme.error, fontSize = 10.sp, modifier = Modifier.align(Alignment.Start))
        }

        Spacer(modifier = Modifier.height(16.dp))

        PasswordInputField(
            label = stringResource(Res.string.label_password),
            value = password,
            onValueChange = { password = it; errorMessage = "" },
            passwordVisible = passwordVisible,
            onVisibilityChange = { passwordVisible = it },
            placeholder = stringResource(Res.string.placeholder_password),
            leadingIcon = Icons.Filled.Lock,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            )
        )

        Text(
            text = stringResource(Res.string.forgot_password),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 8.dp)
                .clickable { showBottomSheet = true }
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
        }

        ActionButton(text = stringResource(Res.string.btn_login), isLoading = isLoading) {
            if (email.isBlank() || password.isBlank()) {
                errorMessage = "Por favor llena todos los campos"
                return@ActionButton
            }
            scope.launch {
                isLoading = true
                errorMessage = ""
                try {
                    Firebase.auth.signInWithEmailAndPassword(email.trim(), password.trim())
                    onNavigate(Screens.Home)
                } catch (e: Exception) {
                    errorMessage = mapFirebaseError(e.message)
                } finally {
                    isLoading = false
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        SocialLoginBlock(onGoogleClick = onGoogleSignIn)

        Spacer(modifier = Modifier.height(40.dp))

        NavigationLink(
            textMain = stringResource(Res.string.no_account),
            textLink = stringResource(Res.string.btn_register)
        ) { onNavigate(Screens.Register) }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { 
                showBottomSheet = false
                recoveryMessage = ""
            },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Recuperar contraseña",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Enviaremos un enlace a tu correo para restablecer tu contraseña.",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                InputField(
                    label = "Correo electrónico",
                    value = recoveryEmail,
                    onValueChange = { recoveryEmail = it; recoveryMessage = "" },
                    placeholder = "ejemplo@correo.com",
                    leadingIcon = Icons.Filled.Email,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                )

                if (recoveryMessage.isNotEmpty()) {
                    Text(
                        text = recoveryMessage,
                        color = if (recoveryMessage.contains("enviado")) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                ActionButton(text = "Enviar enlace", isLoading = isRecovering) {
                    if (recoveryEmail.isBlank() || !isValidEmail(recoveryEmail)) {
                        recoveryMessage = "Ingresa un correo válido"
                        return@ActionButton
                    }
                    scope.launch {
                        isRecovering = true
                        try {
                            Firebase.auth.sendPasswordResetEmail(recoveryEmail.trim())
                            recoveryMessage = "Enlace enviado con éxito. Revisa tu correo."
                        } catch (e: Exception) {
                            recoveryMessage = mapFirebaseError(e.message)
                        } finally {
                            isRecovering = false
                        }
                    }
                }
            }
        }
    }
}
