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


    // Errores de Carrito / Checkout

    const val PAYMENT_REJECTED = "No pudimos procesar tu pago. Verifica tu tarjeta o intenta con otro método."

    const val TURN_GENERATION_FAILED = "Tu pago fue exitoso, pero hubo un problema al generar el turno en el sistema. Por favor, muestra este mensaje en la cafetería."

    const val CART_ITEM_UNAVAILABLE = "Uno o más productos en tu carrito ya no están disponibles."


    // Errores de Sesión y Estado

    const val SESSION_EXPIRED = "Tu sesión ha expirado. Por favor, inicia sesión nuevamente."


    // Errores Genéricos y Base de Datos

    const val DATABASE_ERROR = "No pudimos actualizar la información. Verifica tu conexión."


    // Errores de Billetera y Carrito

    const val NO_PAYMENT_METHOD_SELECTED = "Por favor, selecciona o agrega un método de pago para continuar."

    const val CART_EMPTY = "Tu carrito está vacío. Agrega productos para continuar."


    // Errores de Billetera (Wallet) y Tarjetas


    const val INSUFFICIENT_FUNDS = "Saldo insuficiente. Por favor, recarga tu billetera."

    const val INVALID_PAYMENT_AMOUNT = "Por favor, ingresa un monto válido mayor a $0."

    const val TOP_UP_FAILED = "Hubo un error al intentar recargar tu saldo. Inténtalo de nuevo."

    const val ERROR_REMOVING_CARD = "No pudimos eliminar la tarjeta. Verifica tu conexión."

    const val DUPLICATE_CARD = "Esta tarjeta ya está vinculada a tu cuenta."

    const val TRANSACTION_HISTORY_ERROR = "No pudimos cargar tu historial de movimientos."



    // Validaciones de Tarjeta de Crédito/Débito


    const val INVALID_CARD_NUMBER = "El número de tarjeta está incompleto o es inválido."

    const val INVALID_EXPIRATION_DATE = "La fecha de vencimiento no es válida. Usa el formato MMAA."

    const val CARD_EXPIRED = "Esta tarjeta ya ha expirado. Por favor, intenta con otra."

    const val INVALID_CVV = "El código de seguridad (CVV) está incompleto."

    const val ERROR_SAVING_CARD = "No pudimos guardar tu tarjeta en el sistema. Inténtalo más tarde."
}