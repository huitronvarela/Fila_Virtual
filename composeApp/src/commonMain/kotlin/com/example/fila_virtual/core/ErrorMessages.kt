package com.example.fila_virtual.core

object ErrorMessages {
    // Validaciones Generales
    const val EMPTY_FIELDS = "Por favor llena todos los campos."
    const val INVALID_EMAIL = "Ingresa un correo electrónico válido."

    // Auth & Login
    const val INVALID_CREDENTIALS = "El correo o la contraseña son incorrectos."
    const val USER_NOT_FOUND = "No existe ninguna cuenta con este correo."
    const val EMAIL_ALREADY_IN_USE = "Este correo electrónico ya está registrado."
    const val TOO_MANY_REQUESTS = "Demasiados intentos fallidos. Inténtalo más tarde."

    // Recuperación de contraseña (OTP)
    const val OTP_SEND_ERROR = "Hubo un problema al enviar el código. Inténtalo de nuevo."
    const val OTP_EMPTY = "Por favor ingresa el código de 6 dígitos."
    const val OTP_INVALID = "El código es incorrecto o ha expirado."

    // Contraseñas
    const val PASSWORD_TOO_SHORT = "La contraseña debe tener al menos 8 caracteres."
    const val PASSWORDS_DO_NOT_MATCH = "Las contraseñas no coinciden."
    const val PASSWORD_WEAK = "La contraseña debe incluir números y caracteres especiales."

    // Errores de Red
    const val NETWORK_ERROR = "Error de conexión. Revisa tu internet."
    const val UNKNOWN_ERROR = "Ocurrió un error inesperado. Inténtalo más tarde."
}