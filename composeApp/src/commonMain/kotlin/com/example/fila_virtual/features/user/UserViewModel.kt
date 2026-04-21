package com.example.fila_virtual.features.user

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fila_virtual.data.Usuario
import com.example.fila_virtual.repository.UserRepository
import kotlinx.coroutines.launch

// Importaciones de Ktor para HTTP
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*

class UserViewModel(private val repository: UserRepository = UserRepository()) : ViewModel() {

    var usuario by mutableStateOf<Usuario?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf("")
        private set

    var numeroTarjeta by mutableStateOf("")
        private set

    var nombreTitular by mutableStateOf("")
        private set

    var fechaExpiracion by mutableStateOf("")
        private set

    var cvv by mutableStateOf("")
        private set

    init {
        loadUserData()
    }

    fun loadUserData() {
        val uid = repository.getCurrentUserUid()
        if (uid != null) {
            viewModelScope.launch {
                isLoading = true
                errorMessage = ""
                try {
                    var data = repository.getUserData(uid)
                    if (data != null) {
                        if (data.fotoUrl.isNullOrEmpty()) {
                            val firebaseUser = repository.getFirebaseUser()
                            val googlePhotoUrl = firebaseUser?.photoURL
                            if (googlePhotoUrl != null) {
                                data = data.copy(fotoUrl = googlePhotoUrl)
                            }
                        }
                        usuario = data
                    } else {
                        errorMessage = "No se encontraron datos del usuario"
                    }
                } catch (e: Exception) {
                    errorMessage = "Error: ${e.message}"
                } finally {
                    isLoading = false
                }
            }
        }
    }

    fun signOut(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.signOut()
            onSuccess()
        }
    }

    fun onNumeroTarjetaChange(nuevoNumero: String) {
        if (nuevoNumero.length <= 16) numeroTarjeta = nuevoNumero
    }

    fun onNombreTitularChange(nuevoNombre: String) {
        nombreTitular = nuevoNombre
    }

    fun onFechaExpiracionChange(nuevaFecha: String) {
        if (nuevaFecha.length <= 4) fechaExpiracion = nuevaFecha
    }

    fun onCvvChange(nuevoCvv: String) {
        if (nuevoCvv.length <= 4) cvv = nuevoCvv
    }

    fun procesarPagoSeguro() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""

            val client = HttpClient {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }
            }

            try {
                if (fechaExpiracion.length != 4) throw Exception("La fecha debe tener 4 dígitos (MMAA)")
                val mes = fechaExpiracion.substring(0, 2).toInt()
                val anio = fechaExpiracion.substring(2, 4).toInt() + 2000

                // Tu Public Key exacta de la captura
                val publicKey = "TEST-f1ae3349-69ba-4fed-b8b4-72166ffb423d"

                val mpResponse = client.post("https://api.mercadopago.com/v1/card_tokens?public_key=$publicKey") {
                    contentType(ContentType.Application.Json)
                    setBody(buildJsonObject {
                        put("card_number", numeroTarjeta)
                        put("expiration_month", mes)
                        put("expiration_year", anio)
                        put("security_code", cvv)
                        put("cardholder", buildJsonObject {
                            put("name", nombreTitular)
                        })
                    })
                }

                if (!mpResponse.status.isSuccess()) {
                    throw Exception("Mercado Pago rechazó los datos de la tarjeta. Verifica el número.")
                }

                val responseText = mpResponse.bodyAsText()
                val jsonResponse = Json { ignoreUnknownKeys = true }.parseToJsonElement(responseText).jsonObject
                val tokenGenerado = jsonResponse["id"]?.jsonPrimitive?.content
                    ?: throw Exception("Error al generar el Token de seguridad.")

                val functionUrl = "https://us-central1-altoque-c1c87.cloudfunctions.net/procesarPagoDirecto"

                val firebaseResponse = client.post(functionUrl) {
                    contentType(ContentType.Application.Json)
                    setBody(buildJsonObject {
                        put("data", buildJsonObject {
                            put("tokenTarjeta", tokenGenerado)
                            put("montoTotal", 50)
                            put("metodoPagoId", "debvisa")
                            // Obligamos a que use un correo distinto al de tu cuenta de Mercado Pago
                            put("emailComprador", "gerardo.comprador.altoque.999@ucol.mx")
                        })
                    })
                }

                if (firebaseResponse.status.isSuccess()) {
                    errorMessage = "¡Tarjeta vinculada correctamente!"
                    numeroTarjeta = ""
                    nombreTitular = ""
                    fechaExpiracion = ""
                    cvv = ""
                } else {
                    val errorDetalle = firebaseResponse.bodyAsText()
                    throw Exception("Código ${firebaseResponse.status}: $errorDetalle")
                }

            } catch (e: Exception) {
                errorMessage = "Hubo un problema: ${e.message}"
            } finally {
                client.close()
                isLoading = false
            }
        }
    }
}