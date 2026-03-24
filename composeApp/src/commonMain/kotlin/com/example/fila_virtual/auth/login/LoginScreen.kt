package com.example.fila_virtual.auth.login

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

// Importaciones de Firebase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

// Importaciones de tu Logo y recursos
import fila_virtual.composeapp.generated.resources.Res
import fila_virtual.composeapp.generated.resources.*

// Importaciones de tu arquitectura
import com.example.fila_virtual.core.components.*
import com.example.fila_virtual.core.navigation.Screens
import com.example.fila_virtual.core.mapFirebaseError
import com.example.fila_virtual.core.isValidEmail

// Importación de tu nuevo Repositorio
import com.example.fila_virtual.data.repository.AuthRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigate: (Screens) -> Unit,
    onGoogleSignIn: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // Instanciamos el repositorio para la magia del OTP
    val authRepository = remember { AuthRepository() }

    // Estados de Login Normal
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Estados para el Modal de recuperación de contraseña
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var recoveryEmail by remember { mutableStateOf("") }
    var recoveryMessage by remember { mutableStateOf("") }
    var isRecovering by remember { mutableStateOf(false) }

    // Variables para los 3 pasos de recuperación
    var recoveryStep by remember { mutableStateOf(1) } // 1: Correo, 2: OTP, 3: Nueva Pass
    var inputOtp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
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

        // --- INPUT DE CORREO ORIGINAL RESTAURADO ---
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

        // --- INPUT DE CONTRASEÑA ORIGINAL RESTAURADO ---
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
                .clickable { showBottomSheet = true } // ¡Abre el Modal!
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
        }

        // --- BOTÓN DE LOGIN ORIGINAL RESTAURADO ---
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

        Spacer(modifier = Modifier.weight(1f))

        NavigationLink(
            textMain = stringResource(Res.string.no_account),
            textLink = stringResource(Res.string.btn_register)
        ) { onNavigate(Screens.Register) }

        Spacer(modifier = Modifier.height(32.dp))
    }

    // --- BOTTOM SHEET DE RECUPERACIÓN (FLUJO DE 3 PASOS) ---
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                recoveryMessage = ""
                recoveryStep = 1
                inputOtp = ""
                newPassword = ""
            },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(24.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- PASO 1: PEDIR CORREO ---
                AnimatedVisibility(visible = recoveryStep == 1) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Recuperar contraseña", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(
                            text = "Te enviaremos un código de 6 dígitos para validar tu identidad.",
                            fontSize = 14.sp,
                            color = Color(0xFF666666),
                            modifier = Modifier.padding(vertical = 12.dp),
                            textAlign = TextAlign.Center
                        )

                        InputField(
                            label = "Correo electrónico",
                            value = recoveryEmail,
                            onValueChange = { recoveryEmail = it; recoveryMessage = "" },
                            placeholder = "ejemplo@correo.com",
                            leadingIcon = Icons.Filled.Email,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                        )

                        if (recoveryMessage.isNotEmpty()) {
                            Text(recoveryMessage, color = MaterialTheme.colorScheme.error, fontSize = 13.sp, modifier = Modifier.padding(top = 12.dp))
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        ActionButton(text = "Enviar Código", isLoading = isRecovering) {
                            if (recoveryEmail.isBlank() || !isValidEmail(recoveryEmail)) {
                                recoveryMessage = "Ingresa un correo válido"
                                return@ActionButton
                            }
                            scope.launch {
                                isRecovering = true
                                // Llamamos al Repositorio
                                val enviado = authRepository.sendPasswordResetOtp(recoveryEmail.trim())

                                if (enviado) {
                                    recoveryMessage = ""
                                    recoveryStep = 2 // Transición chula al paso 2
                                } else {
                                    recoveryMessage = "Error al enviar el código. Revisa tu conexión."
                                }
                                isRecovering = false
                            }
                        }
                    }
                }

                // --- PASO 2: INGRESAR EL CÓDIGO (OTP) ---
                AnimatedVisibility(visible = recoveryStep == 2) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Verifica tu identidad", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(
                            text = "Ingresa el código de 6 dígitos enviado a:\n$recoveryEmail",
                            fontSize = 14.sp,
                            color = Color(0xFF666666),
                            modifier = Modifier.padding(vertical = 12.dp),
                            textAlign = TextAlign.Center
                        )

                        OutlinedTextField(
                            value = inputOtp,
                            onValueChange = { if (it.length <= 6) inputOtp = it; recoveryMessage = "" },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 24.sp, textAlign = TextAlign.Center, letterSpacing = 8.sp),
                            modifier = Modifier.fillMaxWidth(0.8f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.LightGray
                            )
                        )

                        if (recoveryMessage.isNotEmpty()) {
                            Text(recoveryMessage, color = MaterialTheme.colorScheme.error, fontSize = 13.sp, modifier = Modifier.padding(top = 12.dp))
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        ActionButton(text = "Verificar Código", isLoading = isRecovering) {
                            if (inputOtp.length < 6) {
                                recoveryMessage = "Ingresa los 6 dígitos"
                                return@ActionButton
                            }
                            scope.launch {
                                isRecovering = true
                                // Comparamos con la base de datos usando el Repositorio
                                val esValido = authRepository.verifyOtpCode(recoveryEmail.trim(), inputOtp)

                                if (esValido) {
                                    recoveryMessage = ""
                                    recoveryStep = 3 // ¡Éxito! Pasamos a la nueva contraseña
                                } else {
                                    recoveryMessage = "Código incorrecto."
                                }
                                isRecovering = false
                            }
                        }

                        Text(
                            text = "¿No lo recibiste? Cancelar e intentar de nuevo",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .clickable { recoveryStep = 1; inputOtp = "" }
                        )
                    }
                }

                // --- PASO 3: NUEVA CONTRASEÑA ---
                AnimatedVisibility(visible = recoveryStep == 3) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Crear nueva contraseña", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(
                            text = "Asegúrate de que sea segura y no la olvides.",
                            fontSize = 14.sp,
                            color = Color(0xFF666666),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        PasswordInputField(
                            label = "Nueva Contraseña",
                            value = newPassword,
                            onValueChange = { newPassword = it; recoveryMessage = "" },
                            passwordVisible = newPasswordVisible,
                            onVisibilityChange = { newPasswordVisible = it },
                            placeholder = "Mínimo 8 caracteres",
                            leadingIcon = Icons.Filled.Lock,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                        )

                        if (recoveryMessage.isNotEmpty()) {
                            Text(
                                text = recoveryMessage,
                                color = if (recoveryMessage.contains("Éxito")) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        ActionButton(text = "Actualizar Contraseña", isLoading = isRecovering) {
                            if (newPassword.length < 6) {
                                recoveryMessage = "La contraseña debe tener al menos 6 caracteres"
                                return@ActionButton
                            }

                            scope.launch {
                                isRecovering = true
                                // Simulamos el éxito visual por ahora
                                delay(1500)
                                recoveryMessage = "¡Éxito! Contraseña actualizada."
                                delay(1000)
                                showBottomSheet = false
                                recoveryStep = 1
                                isRecovering = false
                            }
                        }
                    }
                }
            }
        }
    }
}