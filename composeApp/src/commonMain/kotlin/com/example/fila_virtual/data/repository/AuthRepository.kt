package com.example.fila_virtual.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.datetime.Clock // <-- ESTE ES EL IMPORT QUE FALTABA
import kotlinx.serialization.Serializable
import kotlin.random.Random

// Estructuras de datos para armar el correo y guardar el código
@Serializable
data class MailMessage(val subject: String, val text: String, val html: String)

@Serializable
data class MailDocument(val to: String, val message: MailMessage)

@Serializable
data class OtpRecord(val code: String, val timestamp: Long)

class AuthRepository {

    // Función 1: Generar el código OTP, guardarlo en Firebase y disparar el correo
    suspend fun sendPasswordResetOtp(email: String): Boolean {
        return try {
            // 1. Generamos código de 6 dígitos aleatorio
            val otpCode = Random.nextInt(100000, 999999).toString()

            // 2. Lo guardamos en Firestore en la colección "otp_codes"
            // Ahora Clock.System funcionará correctamente
            val otpRecord = OtpRecord(code = otpCode, timestamp = Clock.System.now().toEpochMilliseconds())
            Firebase.firestore
                .collection("otp_codes")
                .document(email)
                .set(otpRecord)

            // 3. Preparamos el correo con diseño HTML profesional
            val mailData = MailDocument(
                to = email,
                message = MailMessage(
                    subject = "Tu código de recuperación - AlToque",
                    text = "Tu código de seguridad es: $otpCode",
                    html = """
                        <div style="font-family: Arial, sans-serif; text-align: center; padding: 20px;">
                            <h2 style="color: #333;">Recuperación de contraseña</h2>
                            <p style="color: #555;">Ingresa el siguiente código de 6 dígitos en la aplicación AlToque:</p>
                            <div style="background-color: #f4f4f4; padding: 15px; border-radius: 8px; display: inline-block; margin: 10px 0;">
                                <h1 style="color: #FF5A36; letter-spacing: 5px; margin: 0;">$otpCode</h1>
                            </div>
                            <p style="color: #999; font-size: 12px;"><br>Si no solicitaste este cambio, ignora este correo.</p>
                        </div>
                    """.trimIndent()
                )
            )

            // 4. Disparamos el correo metiendo el documento a la colección "mail"
            Firebase.firestore.collection("mail").add(mailData)

            true // Todo salió bien
        } catch (e: Exception) {
            println("Error enviando OTP: ${e.message}")
            false // Algo falló
        }
    }

    // Función 2: Validar que el código que escriba el usuario sea el correcto
    suspend fun verifyOtpCode(email: String, inputCode: String): Boolean {
        return try {
            val document = Firebase.firestore.collection("otp_codes").document(email).get()
            if (document.exists) {
                val record = document.data<OtpRecord>()
                record.code == inputCode // Regresa true si coinciden, false si no
            } else {
                false
            }
            // Usamos '_' en lugar de 'e' para quitar la advertencia de parámetro no usado
        } catch (_: Exception) {
            false
        }
    }
}