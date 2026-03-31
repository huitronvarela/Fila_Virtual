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
import com.example.fila_virtual.components.ActionButton
import com.example.fila_virtual.components.InputField
import com.example.fila_virtual.components.NavigationLink
import com.example.fila_virtual.components.PasswordInputField
import com.example.fila_virtual.components.TermsCheckbox
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

// Importaciones de Firebase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

// Importaciones de recursos
import fila_virtual.composeapp.generated.resources.Res
import fila_virtual.composeapp.generated.resources.*

// Importaciones de tu arquitectura
import com.example.fila_virtual.core.*
import com.example.fila_virtual.data.Usuario
import com.example.fila_virtual.navigation.Screens
import com.example.fila_virtual.repository.AuthRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onNavigate: (Screens) -> Unit) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val authRepository = remember { AuthRepository() }

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
    
    var isSuccess by remember { mutableStateOf(false) }

    // Estados para Bottom Sheets
    var showPrivacyPolicy by remember { mutableStateOf(false) }
    var showTermsAndConditions by remember { mutableStateOf(false) }
    var showOtpSheet by remember { mutableStateOf(false) }

    // Configuración para que abran COMPLETAMENTE y fluido
    val legalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val otpSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var otpCode by remember { mutableStateOf("") }
    var isVerifyingOtp by remember { mutableStateOf(false) }
    var otpErrorMessage by remember { mutableStateOf("") }

    // LÓGICA DE ACTIVACIÓN DEL BOTÓN (Estricta)
    val isFormValid = nombre.isNotBlank() && 
                     telefono.length == 10 && 
                     isValidEmail(email) && 
                     isStrongPassword(password) && 
                     password == confirmPassword && 
                     termsAccepted

    Surface(
        modifier = Modifier.fillMaxSize(),
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
                onValueChange = { if (isValidName(it)) nombre = it }, 
                placeholder = stringResource(Res.string.placeholder_name),
                leadingIcon = Icons.Filled.Person,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                isError = nombre.isNotEmpty() && nombre.length < 3
            )
            Spacer(modifier = Modifier.height(16.dp))

            InputField(
                label = stringResource(Res.string.label_phone), 
                value = telefono, 
                onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) telefono = it }, 
                placeholder = stringResource(Res.string.placeholder_phone),
                leadingIcon = Icons.Filled.Phone,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                visualTransformation = PhoneVisualTransformation(),
                isError = telefono.isNotEmpty() && !isValidPhone(telefono)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            InputField(
                label = stringResource(Res.string.label_email), 
                value = email, 
                onValueChange = { email = it }, 
                placeholder = stringResource(Res.string.placeholder_email),
                leadingIcon = Icons.Filled.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                isError = email.isNotEmpty() && !isValidEmail(email)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            val requirements = checkPasswordRequirements(password)
            PasswordInputField(
                label = stringResource(Res.string.label_password), 
                value = password, 
                onValueChange = { password = it }, 
                passwordVisible = passwordVisible, 
                onVisibilityChange = { passwordVisible = it }, 
                placeholder = stringResource(Res.string.placeholder_password),
                leadingIcon = Icons.Filled.Lock,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                isError = password.isNotEmpty() && !requirements.isAllMet
            )
            
            if (password.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
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
                onValueChange = { confirmPassword = it }, 
                passwordVisible = confirmPasswordVisible, 
                onVisibilityChange = { confirmPasswordVisible = it }, 
                placeholder = stringResource(Res.string.placeholder_confirm_password),
                leadingIcon = Icons.Filled.Lock,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                isError = confirmPassword.isNotEmpty() && confirmPassword != password
            )
            
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

            // BOTÓN REGISTRAR: SOLO SE ACTIVA SI SE CUMPLEN TODAS LAS CONDICIONES
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        if (authRepository.sendRegistrationOtp(email.trim())) {
                            showOtpSheet = true
                        } else {
                            errorMessage = "Error al enviar código"
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = isFormValid && !isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color.LightGray
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(text = stringResource(Res.string.btn_register), color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            NavigationLink(stringResource(Res.string.already_have_account), stringResource(Res.string.btn_login)) { onNavigate(Screens.Login) }
            Spacer(modifier = Modifier.height(48.dp))
        }
        
        AnimatedVisibility(visible = isSuccess, enter = slideInVertically(initialOffsetY = { it }) + fadeIn()) {
            Box(modifier = Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painterResource(Res.drawable.logot), null, modifier = Modifier.size(120.dp))
                    Text(stringResource(Res.string.app_name), color = MaterialTheme.colorScheme.primary, fontSize = 40.sp, fontWeight = FontWeight.Bold)
                    Text("¡Registro exitoso!", color = Color.Gray, fontSize = 18.sp)
                }
            }
        }
    }

    // --- BOTTOM SHEETS LEGALES ---
    if (showPrivacyPolicy) {
        LegalBottomSheet(
            title = "Aviso de Privacidad",
            content = LegalConstants.AVISO_DE_PRIVACIDAD,
            sheetState = legalSheetState,
            onDismiss = { showPrivacyPolicy = false }
        )
    }

    if (showTermsAndConditions) {
        LegalBottomSheet(
            title = "Términos y Condiciones",
            content = LegalConstants.TERMINOS_Y_CONDICIONES,
            sheetState = legalSheetState,
            onDismiss = { showTermsAndConditions = false }
        )
    }

    // --- BOTTOM SHEET OTP ---
    if (showOtpSheet) {
        OtpBottomSheet(
            email = email,
            otpCode = otpCode,
            sheetState = otpSheetState,
            onOtpChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) otpCode = it },
            isVerifying = isVerifyingOtp,
            errorMessage = otpErrorMessage,
            onVerifyClick = {
                scope.launch {
                    isVerifyingOtp = true
                    if (authRepository.verifyOtpCode(email.trim(), otpCode)) {
                        try {
                            val authResult = Firebase.auth.createUserWithEmailAndPassword(email.trim(), password.trim())
                            authResult.user?.uid?.let { uid ->
                                val nuevoUsuario = Usuario(nombre.trim(), telefono.trim(), email.trim(), "ALUMNO", "", "11 de marzo de 2026")
                                Firebase.firestore.collection("usuarios").document(uid).set(nuevoUsuario)
                            }
                            showOtpSheet = false
                            isSuccess = true
                            delay(1500)
                            onNavigate(Screens.Home)
                        } catch (e: Exception) {
                            showOtpSheet = false
                            errorMessage = mapFirebaseError(e.message)
                        }
                    } else {
                        otpErrorMessage = "Código incorrecto"
                    }
                    isVerifyingOtp = false
                }
            },
            onCancelClick = { showOtpSheet = false }
        )
    }
}

@Composable
fun PasswordRequirementItem(text: String, isMet: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Icon(
            imageVector = if (isMet) Icons.Default.CheckCircle else Icons.Default.Circle,
            contentDescription = null,
            tint = if (isMet) Color(0xFF4CAF50) else Color.LightGray,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 12.sp, color = if (isMet) Color.Black else Color.Gray)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalBottomSheet(title: String, content: String, sheetState: SheetState, onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize() // OCUPA TODA LA PANTALLA
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = title, 
                fontSize = 24.sp, 
                fontWeight = FontWeight.Bold, 
                color = Color.Black,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            HorizontalDivider(color = Color(0xFFF1F3F5))
            
            Box(modifier = Modifier.weight(1f).padding(vertical = 16.dp).verticalScroll(rememberScrollState())) {
                Text(
                    text = content, 
                    fontSize = 15.sp, 
                    color = Color(0xFF444444), 
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Justify // TEXTO JUSTIFICADO
                )
            }
            
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("He leído y acepto", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpBottomSheet(
    email: String,
    otpCode: String,
    sheetState: SheetState,
    onOtpChange: (String) -> Unit,
    isVerifying: Boolean,
    errorMessage: String,
    onVerifyClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onCancelClick,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Verifica tu identidad", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Código enviado a $email", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 12.dp))
            OutlinedTextField(
                value = otpCode,
                onValueChange = onOtpChange,
                modifier = Modifier.fillMaxWidth().height(68.dp),
                textStyle = TextStyle(fontSize = 28.sp, letterSpacing = 16.sp, textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
            if (errorMessage.isNotEmpty()) Text(errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
            Spacer(modifier = Modifier.height(24.dp))
            ActionButton("Verificar Código", isVerifying, onVerifyClick)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Cancelar", color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { onCancelClick() }.padding(8.dp))
        }
    }
}
