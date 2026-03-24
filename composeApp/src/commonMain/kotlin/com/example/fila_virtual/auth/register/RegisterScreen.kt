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

@OptIn(ExperimentalMaterial3Api::class)
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

    // --- ESTADOS PARA LOS BOTTOM SHEETS (Tarjetas que suben) ---
    var showPrivacyPolicy by remember { mutableStateOf(false) }
    var showTermsAndConditions by remember { mutableStateOf(false) }

    // DIBUJAMOS EL FORMULARIO DE REGISTRO NORMAL
    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding() // CORRECCIÓN: Evita el choque con la barra de Android
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
                onTermsClick = { showTermsAndConditions = true }, // Abre la tarjeta de Términos
                onPrivacyClick = { showPrivacyPolicy = true }     // Abre la tarjeta de Privacidad
            )

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
                                fechaRegistro = "14 de marzo de 2026"
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

            Spacer(modifier = Modifier.height(48.dp)) // Espacio final para que el scroll libere el botón
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

    // --- LÓGICA DE LOS BOTTOM SHEETS ---

    if (showPrivacyPolicy) {
        LegalBottomSheet(
            title = "Aviso de Privacidad",
            content = """
                En estricto cumplimiento a lo establecido por la Ley Federal de Protección de Datos
                Personales en Posesión de los Particulares, su Reglamento y los Lineamientos del
                Aviso de Privacidad vigentes en los Estados Unidos Mexicanos (en adelante, la
                "Legislación Vigente"), AlToque S.A. de C.V., con domicilio legal ubicado en
                Carretera Manzanillo-Cihuatlán Km. 20, C.P. 28860, Manzanillo, Colima (en
                adelante, el "Responsable"), es el responsable del uso, tratamiento, confidencialidad
                y protección de sus datos personales recabados a través de la aplicación móvil
                denominada "AlToque", disponible para dispositivos con sistemas operativos iOS
                y Android, así como de cualquier entorno digital, portal web o servicio asociado a la
                misma (en adelante y de manera conjunta, la "Plataforma").
                
                1. Datos Personales Recabados
                Para llevar a cabo las finalidades descritas en el presente aviso, utilizaremos los siguientes datos personales:
                • Nombre completo.
                • Correo electrónico.
                • Número de teléfono celular.
                • Imagen y datos contenidos en su Identificación Oficial vigente (INE).
                • Datos patrimoniales y/o financieros (información de tarjetas de crédito o débito, procesados de manera segura exclusivamente a través de proveedores autorizados).
                
                2. Finalidades del Tratamiento de Datos
                Los datos personales recabados serán utilizados para las siguientes finalidades primarias, las cuales son necesarias para el servicio que solicita:
                • Registro, creación y administración de su cuenta de usuario en la Plataforma.
                • Procesamiento de pagos a través de la pasarela electrónica.
                • Gestión, seguimiento y notificación de sus pedidos de alimentos y su posición en AlToque.
                • Validación de identidad y prevención de fraudes, suplantación de identidad, pedidos falsos o mal uso de la Plataforma.
                
                3. Transferencia de Datos a Terceros
                El Responsable se compromete a no comercializar ni vender su información personal. Sus datos únicamente podrán ser compartidos con los siguientes terceros, bajo estrictas medidas de seguridad, para garantizar el funcionamiento de la Plataforma:
                • Proveedores de Infraestructura Tecnológica (ej. Google Firebase).
                • Proveedores de Procesamiento de Pagos (ej. PayPal).
                • Establecimientos Afiliados.
                
                4. Ejercicio de Derechos ARCO
                Usted tiene derecho a conocer qué datos personales tenemos de usted, para qué los utilizamos y las condiciones del uso que les damos (Acceso). Asimismo, es su derecho solicitar la corrección de su información personal en caso de que esté desactualizada, sea inexacta o incompleta (Rectificación); que la eliminemos de nuestros registros o bases de datos (Cancelación); así como oponerse al uso de sus datos personales para fines específicos (Oposición). Para el ejercicio de cualquiera de los derechos ARCO, deberá enviar una solicitud respectiva al correo electrónico oficial: ggutierrez0@ucol.mx.
            """.trimIndent(),
            onDismiss = { showPrivacyPolicy = false }
        )
    }

    if (showTermsAndConditions) {
        LegalBottomSheet(
            title = "Términos y Condiciones",
            content = """
                1. Aceptación de los Términos y Restricción de Edad
                El acceso y uso de la Plataforma "AlToque" atribuye la condición de Usuario e implica la aceptación plena y sin reservas de todas y cada una de las disposiciones incluidas en este documento. Debido a la necesidad legal de validar la identidad mediante identificación oficial (INE) y la realización de transacciones electrónicas, el uso de esta Plataforma está estrictamente limitado a personas mayores de 18 años.
                
                2. Mecánica de Pedidos y Pagos
                La Plataforma opera bajo un modelo de servicio automatizado y pago anticipado. Todo pedido realizado a través de "Fila Virtual" deberá ser liquidado en su totalidad al momento de solicitarlo, mediante las pasarelas de pago integradas en la aplicación (PayPal). No se aceptarán pagos en efectivo en la sucursal para los pedidos gestionados a través de la Plataforma.
                
                3. Políticas de Cancelación, Tolerancia y Reembolsos
                Debido a la naturaleza perecedera de los bienes comercializados (alimentos y bebidas preparados bajo demanda), no procederá ningún tipo de reembolso monetario una vez que el pago ha sido procesado exitosamente y el pedido ha entrado en la cola de producción del establecimiento.
                • Tolerancia por Retraso: Si el Usuario no se presenta en la sucursal al momento de ser notificado que su turno o pedido está listo, el producto quedará a resguardo del establecimiento hasta su horario de cierre del día en curso.
                • Inasistencia Total: En caso de que el Usuario no recoja su pedido antes del cierre de operaciones del día, el producto se considerará entregado/mermado y no habrá devolución del importe pagado.
                
                4. Tiempos de Espera y Límites de Responsabilidad
                "AlToque" opera exclusivamente como un intermediario tecnológico de gestión de turnos y pagos entre el Usuario y el Establecimiento de consumo.
                • Los tiempos de espera mostrados en la Plataforma son estimaciones.
                • Cualquier compensación derivada de demoras excepcionales en la preparación de los alimentos será responsabilidad exclusiva del establecimiento.
                
                5. Conducta del Usuario y Sanciones Administrativas
                El Usuario se obliga a utilizar la Plataforma de manera lícita, ética y de buena fe. Queda estrictamente prohibido apartar lugares sin intención de compra, realizar pedidos falsos, entorpecer el dinamismo de la fila virtual, o realizar cualquier acto que altere el funcionamiento del software.
                
                6. Jurisdicción y Legislación Aplicable
                Para la interpretación, cumplimiento y ejecución de los presentes Términos y Condiciones, así como para la resolución de cualquier controversia que pudiera suscitarse, las partes se someten expresamente a las leyes federales de los Estados Unidos Mexicanos y a la jurisdicción de los tribunales competentes en el Estado de Colima.
            """.trimIndent(),
            onDismiss = { showTermsAndConditions = false }
        )
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

// --- COMPONENTE BOTTOM SHEET REUTILIZABLE ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalBottomSheet(
    title: String,
    content: String,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope() // <--- Agregamos esto para la animación

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding() // CORRECCIÓN: Evita el choque del botón cerrar con el sistema
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )

            // Caja de texto con scroll
            Box(
                modifier = Modifier
                    .weight(1f, fill = false)
            ) {
                Text(
                    text = content,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Justify, // CORRECCIÓN: Justificado para que se vea como documento legal pro
                    modifier = Modifier.verticalScroll(rememberScrollState())
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón con animación de bajada
            ActionButton(
                text = "Cerrar",
                isLoading = false,
                onClick = {
                    // Primero animamos hacia abajo y luego cerramos
                    scope.launch {
                        sheetState.hide()
                        onDismiss()
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}