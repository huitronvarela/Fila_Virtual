package com.example.fila_virtual.auth.register

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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

// Importaciones de tu Logo
import fila_virtual.composeapp.generated.resources.Res
import fila_virtual.composeapp.generated.resources.*

// Importaciones de tu nueva arquitectura
import com.example.fila_virtual.core.components.*
import com.example.fila_virtual.core.data.Usuario
import com.example.fila_virtual.core.navigation.Screens
import com.example.fila_virtual.core.*


@Composable
fun RegisterScreen(onNavigate: (Screens) -> Unit) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

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

    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        color = Color.White
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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

            TermsCheckbox(termsAccepted = termsAccepted, onCheckedChange = { termsAccepted = it })

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp))
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }

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

                scope.launch {
                    isLoading = true
                    errorMessage = ""
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
                                fechaRegistro = "02 de marzo de 2026"
                            )
                            Firebase.firestore.collection("usuarios").document(uid).set(nuevoUsuario)
                        }
                        
                        isLoading = false
                        isSuccess = true
                        delay(1500)
                        onNavigate(Screens.Home)

                    } catch (e: Exception) {
                        errorMessage = mapFirebaseError(e.message)
                        isLoading = false
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
}

@Composable
fun PasswordRequirementItem(text: String, isMet: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = if (isMet) Icons.Filled.CheckCircle else Icons.Filled.Circle,
            contentDescription = null,
            tint = if (isMet) Color(0xFF2E7D32) else Color.LightGray,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (isMet) Color(0xFF2E7D32) else Color.Gray
        )
    }
}
