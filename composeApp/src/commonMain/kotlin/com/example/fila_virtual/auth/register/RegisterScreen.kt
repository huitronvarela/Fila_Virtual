package com.example.fila_virtual.auth.register

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

// Importaciones de Firebase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

// Importaciones de tu Logo y Recursos
import fila_virtual.composeapp.generated.resources.Res
import fila_virtual.composeapp.generated.resources.*

// Importaciones de tu arquitectura
import com.example.fila_virtual.core.components.*
import com.example.fila_virtual.core.data.Usuario
import com.example.fila_virtual.core.navigation.Screens
import com.example.fila_virtual.core.*
import com.example.fila_virtual.data.repository.AuthRepository // IMPORTANTE AÑADIR ESTO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onNavigate: (Screens) -> Unit) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val authRepository = remember { AuthRepository() } // Instancia del repositorio

    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Estado para controlar la animación de éxito
    var isSuccess by remember { mutableStateOf(false) }

    // --- ESTADOS PARA LOS BOTTOM SHEETS LEGALES ---
    var showPrivacyPolicy by remember { mutableStateOf(false) }
    var showTermsAndConditions by remember { mutableStateOf(false) }

    // --- NUEVOS ESTADOS PARA EL OTP ---
    var showOtpSheet by remember { mutableStateOf(false) }
    var otpCode by remember { mutableStateOf("") }
    var isVerifyingOtp by remember { mutableStateOf(false) }
    var otpErrorMessage by remember { mutableStateOf("") }

    // DIBUJAMOS EL FORMULARIO DE REGISTRO NORMAL
    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Image(
                painter = painterResource(Res.drawable.logot),
                contentDescription = "Logo",
                modifier = Modifier.size(70.dp)
            )
            Text(
                text = stringResource(Res.string.app_name),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(Res.string.create_account),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = stringResource(Res.string.register_welcome),
                fontSize = 16.sp,
                color = Color(0xFF666666),
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            InputField(
                label = stringResource(Res.string.label_name),
                value = nombre,
                onValueChange = {
                    if (isValidName(it)) {
                        nombre = it
                        errorMessage = ""
                    }
                },
                placeholder = stringResource(Res.string.placeholder_name),
                leadingIcon = Icons.Filled.Person,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                isError = nombre.isNotEmpty() && nombre.length < 3
            )
            Spacer(modifier = Modifier.height(16.dp))

            InputField(
                label = stringResource(Res.string.label_phone),
                value = telefono,
                onValueChange = {
                    if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                        telefono = it
                        errorMessage = ""
                    }
                },
                placeholder = stringResource(Res.string.placeholder_phone),
                leadingIcon = Icons.Filled.Phone,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                visualTransformation = PhoneVisualTransformation(),
                isError = telefono.isNotEmpty() && !isValidPhone(telefono)
            )

            if (telefono.isNotEmpty() && !isValidPhone(telefono)) {
                Text("Se requieren 10 dígitos", color = MaterialTheme.colorScheme.error, fontSize = 10.sp, modifier = Modifier.align(Alignment.Start))
            }

            Spacer(modifier = Modifier.height(16.dp))

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

            val requirements = checkPasswordRequirements(password)
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
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                isError = password.isNotEmpty() && !requirements.isAllMet
            )

            if (password.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    PasswordRequirementItem("Mínimo 9 caracteres", requirements.hasMinLength)
                    PasswordRequirementItem("Una mayúscula", requirements.hasUpperCase)
                    PasswordRequirementItem("Un número", requirements.hasDigit)
                    PasswordRequirementItem("Un carácter especial", requirements.hasSpecialChar)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PasswordInputField(
                label = stringResource(Res.string.label_confirm_password),
                value = confirmPassword,
                onValueChange = { confirmPassword = it; errorMessage = "" },
                passwordVisible = confirmPasswordVisible,
                onVisibilityChange = { confirmPasswordVisible = it },
                placeholder = stringResource(Res.string.placeholder_confirm_password),
                leadingIcon = Icons.Filled.Lock,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                isError = confirmPassword.isNotEmpty() && confirmPassword != password
            )

            if (confirmPassword.isNotEmpty() && confirmPassword != password) {
                Text("Las contraseñas no coinciden", color = MaterialTheme.colorScheme.error, fontSize = 10.sp, modifier = Modifier.align(Alignment.Start))
            }

            Spacer(modifier = Modifier.height(16.dp))

            TermsCheckbox(
                termsAccepted = termsAccepted,
                onCheckedChange = { termsAccepted = it },
                onTermsClick = { showTermsAndConditions = true },
                onPrivacyClick = { showPrivacyPolicy = true }
            )

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp))
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- MODIFICADO: EL BOTÓN AHORA ENVÍA EL OTP EN LUGAR DE REGISTRAR DIRECTO ---
            ActionButton(text = stringResource(Res.string.btn_register), isLoading = isLoading) {
                if (nombre.isBlank() || telefono.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                    errorMessage = "Por favor llena todos los campos"
                    return@ActionButton
                }
                if (!isValidEmail(email)) {
                    errorMessage = "Revisa el formato de tu correo"
                    return@ActionButton
                }
                if (!isValidPhone(telefono)) {
                    errorMessage = "El teléfono debe tener 10 dígitos"
                    return@ActionButton
                }
                if (!isStrongPassword(password)) {
                    errorMessage = "La contraseña no cumple con los requisitos"
                    return@ActionButton
                }
                if (password != confirmPassword) {
                    errorMessage = "Las contraseñas no coinciden"
                    return@ActionButton
                }
                if (!termsAccepted) {
                    errorMessage = "Debes aceptar los términos y condiciones"
                    return@ActionButton
                }

                // Disparamos el envío del OTP
                scope.launch {
                    isLoading = true
                    errorMessage = ""
                    val otpSent = authRepository.sendRegistrationOtp(email.trim())
                    isLoading = false

                    if (otpSent) {
                        otpCode = "" // Limpiamos si había algo antes
                        otpErrorMessage = ""
                        showOtpSheet = true // Levantamos el BottomSheet
                    } else {
                        errorMessage = "Error al enviar el código de verificación. Intenta de nuevo."
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            NavigationLink(
                textMain = stringResource(Res.string.already_have_account),
                textLink = stringResource(Res.string.btn_login)
            ) { onNavigate(Screens.Login) }

            Spacer(modifier = Modifier.height(48.dp))
        }

        // Vista de éxito
        AnimatedVisibility(
            visible = isSuccess,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(600)) + fadeIn()
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(Res.drawable.logot),
                        contentDescription = "Success Logo",
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = stringResource(Res.string.app_name),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "¡Registro exitoso!",
                        color = Color.Gray,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }

    // --- LÓGICA DE LOS BOTTOM SHEETS LEGALES (SIN CAMBIOS) ---
    if (showPrivacyPolicy) {
        LegalBottomSheet(
            title = "Aviso de Privacidad",
            content = "...", // Aquí va tu texto original que acorté para no hacer gigante el bloque, pégalo de vuelta
            onDismiss = { showPrivacyPolicy = false }
        )
    }

    if (showTermsAndConditions) {
        LegalBottomSheet(
            title = "Términos y Condiciones",
            content = "...", // Aquí va tu texto original
            onDismiss = { showTermsAndConditions = false }
        )
    }

    // --- NUEVO: BOTTOM SHEET DEL OTP ---
    if (showOtpSheet) {
        OtpBottomSheet(
            email = email,
            otpCode = otpCode,
            onOtpChange = {
                if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                    otpCode = it
                    otpErrorMessage = ""
                }
            },
            isVerifying = isVerifyingOtp,
            errorMessage = otpErrorMessage,
            onVerifyClick = {
                if (otpCode.length < 6) {
                    otpErrorMessage = "Ingresa los 6 dígitos"
                    return@OtpBottomSheet
                }

                scope.launch {
                    isVerifyingOtp = true
                    val isValid = authRepository.verifyOtpCode(email.trim(), otpCode)

                    if (isValid) {
                        // CÓDIGO CORRECTO: AHORA SÍ CREAMOS LA CUENTA
                        try {
                            val authResult = Firebase.auth.createUserWithEmailAndPassword(email.trim(), password.trim())
                            val uid = authResult.user?.uid

                            if (uid != null) {
                                val nuevoUsuario = Usuario(
                                    nombre = nombre.trim(),
                                    telefono = telefono.trim(),
                                    email = email.trim(),
                                    tipoUsuario = "ALUMNO",
                                    billetera = "",
                                    fechaRegistro = "30 de marzo de 2026" // O usa Clock.System si prefieres
                                )
                                Firebase.firestore.collection("usuarios").document(uid).set(nuevoUsuario)
                            }

                            showOtpSheet = false
                            isVerifyingOtp = false
                            isSuccess = true
                            delay(1500)
                            onNavigate(Screens.Home)

                        } catch (e: Exception) {
                            showOtpSheet = false
                            isVerifyingOtp = false
                            errorMessage = mapFirebaseError(e.message) // Muestra el error de Firebase en la pantalla principal
                        }
                    } else {
                        isVerifyingOtp = false
                        otpErrorMessage = "Código incorrecto o expirado"
                    }
                }
            },
            onCancelClick = {
                showOtpSheet = false
                otpCode = ""
            }
        )
    }
}

// ... (Aquí mantén tu función PasswordRequirementItem intacta)
@Composable
fun PasswordRequirementItem(text: String, isMet: Boolean) { /* Tu código original */ }

// ... (Aquí mantén tu función LegalBottomSheet intacta)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalBottomSheet(title: String, content: String, onDismiss: () -> Unit) { /* Tu código original */ }


// --- NUEVO COMPONENTE: DISEÑO DEL OTP IDÉNTICO A TU IMAGEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpBottomSheet(
    email: String,
    otpCode: String,
    onOtpChange: (String) -> Unit,
    isVerifying: Boolean,
    errorMessage: String,
    onVerifyClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val focusManager = LocalFocusManager.current

    ModalBottomSheet(
        onDismissRequest = onCancelClick,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Verifica tu identidad",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Ingresa el código de 6 dígitos enviado a:",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Text(
                text = email,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Input estilo "caja grande" con espaciado
            OutlinedTextField(
                value = otpCode,
                onValueChange = onOtpChange,
                modifier = Modifier.fillMaxWidth().height(68.dp),
                textStyle = TextStyle(
                    fontSize = 28.sp,
                    letterSpacing = 16.sp, // Esto hace que los números se separen como en tu foto
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                ),
                singleLine = true
            )

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }

            ActionButton(
                text = "Verificar Código",
                isLoading = isVerifying,
                onClick = onVerifyClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¿No lo recibiste? Cancelar e intentar de nuevo",
                color = MaterialTheme.colorScheme.primary, // Naranja
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable { onCancelClick() }
                    .padding(8.dp)
            )
        }
    }
}