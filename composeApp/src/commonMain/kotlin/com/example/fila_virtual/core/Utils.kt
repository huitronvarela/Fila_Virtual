package com.example.fila_virtual.core

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Mapea los mensajes de error técnicos a mensajes amigables para el usuario.
 */
fun mapFirebaseError(message: String?): String {
    if (message == null) return "Ocurrió un error inesperado."
    
    return when {
        message.contains("invalid-email") || message.contains("INVALID_EMAIL") -> 
            "El correo electrónico no tiene un formato válido."
        message.contains("user-not-found") || message.contains("USER_NOT_FOUND") -> 
            "No existe ninguna cuenta con este correo."
        message.contains("wrong-password") || message.contains("INVALID_PASSWORD") -> 
            "La contraseña es incorrecta."
        message.contains("email-already-in-use") || message.contains("EMAIL_EXISTS") -> 
            "Este correo electrónico ya está registrado."
        message.contains("weak-password") || message.contains("WEAK_PASSWORD") -> 
            "La contraseña es muy débil."
        message.contains("network-request-failed") -> 
            "Error de conexión. Revisa tu internet."
        message.contains("too-many-requests") -> 
            "Demasiados intentos fallidos. Inténtalo más tarde."
        else -> "Error: $message"
    }
}

/**
 * Valida si un correo tiene formato correcto.
 */
fun isValidEmail(email: String): Boolean {
    return email.contains("@") && email.contains(".")
}

/**
 * Valida si un nombre contiene solo letras y espacios.
 */
fun isValidName(name: String): Boolean {
    return name.all { it.isLetter() || it.isWhitespace() }
}

/**
 * Requisitos de contraseña para la UI.
 */
data class PasswordRequirements(
    val hasMinLength: Boolean = false,
    val hasUpperCase: Boolean = false,
    val hasDigit: Boolean = false,
    val hasSpecialChar: Boolean = false
) {
    val isAllMet: Boolean get() = hasMinLength && hasUpperCase && hasDigit && hasSpecialChar
}

fun checkPasswordRequirements(password: String): PasswordRequirements {
    return PasswordRequirements(
        hasMinLength = password.length >= 9,
        hasUpperCase = password.any { it.isUpperCase() },
        hasDigit = password.any { it.isDigit() },
        hasSpecialChar = password.any { !it.isLetterOrDigit() }
    )
}

/**
 * Valida la fortaleza de la contraseña.
 */
fun isStrongPassword(password: String): Boolean {
    return checkPasswordRequirements(password).isAllMet
}

/**
 * Valida si el teléfono tiene exactamente 10 dígitos.
 */
fun isValidPhone(phone: String): Boolean {
    return phone.length == 10
}

/**
 * Transformación visual para teléfono: 000 000 0000
 */
class PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val out = StringBuilder()
        for (i in text.text.indices) {
            out.append(text.text[i])
            if (i == 2 || i == 5) out.append(" ")
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset + 1
                if (offset <= 10) return offset + 2
                return out.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 3) return offset
                if (offset <= 7) return offset - 1
                if (offset <= 12) return offset - 2
                return text.text.length
            }
        }

        return TransformedText(AnnotatedString(out.toString()), offsetMapping)
    }
}
