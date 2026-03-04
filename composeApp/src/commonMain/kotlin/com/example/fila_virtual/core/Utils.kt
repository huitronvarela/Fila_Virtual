package com.example.fila_virtual.core

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
            "La contraseña es muy débil (debe tener al menos 6 caracteres)."
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
