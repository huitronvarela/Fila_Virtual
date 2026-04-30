package com.example.fila_virtual.features.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fila_virtual.data.Usuario
import com.example.fila_virtual.repository.UserRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.launch

// Importaciones de Ktor para conectarnos a Mercado Pago
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*

class UserViewModel(private val repository: UserRepository = UserRepository()) : ViewModel() {

    // Llaves de prueba de Mercado Pago
    private val MERCADO_PAGO_PUBLIC_KEY = "TEST-f1ae3349-69ba-4fed-b8b4-72166ffb423d"
    private val MERCADO_PAGO_ACCESS_TOKEN = "TEST-5274885548194765-041905-2ab93399cf879f1f6a2de1078fe249fd-2922185240"

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

    fun updateProfile(nombre: String, telefono: String, fotoUrl: String?, onResult: (Boolean) -> Unit) {
        val uid = repository.getCurrentUserUid()
        if (uid != null) {
            viewModelScope.launch {
                val updates = mapOf(
                    "nombre" to nombre,
                    "telefono" to telefono,
                    "fotoUrl" to fotoUrl
                )
                val success = repository.updateUserData(uid, updates)
                if (success) {
                    loadUserData() // Recargar datos locales tras la actualización
                }
                onResult(success)
            }
        } else {
            onResult(false)
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
        if (numeroTarjeta.length < 16 || cvv.isEmpty() || fechaExpiracion.isEmpty()) {
            errorMessage = "Por favor completa todos los campos correctamente."
            return
        }

        isLoading = true
        errorMessage = ""

        viewModelScope.launch {
            try {
                // 1. Dividimos la fecha
                val mes = fechaExpiracion.substring(0, 2)
                val anio = "20" + fechaExpiracion.substring(2, 4)

                // 2. Preparamos Ktor
                val client = HttpClient {
                    install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
                }

                // 3. Generar el Token
                val response: HttpResponse = client.post("https://api.mercadopago.com/v1/card_tokens?public_key=$MERCADO_PAGO_PUBLIC_KEY") {
                    contentType(ContentType.Application.Json)
                    setBody(buildJsonObject {
                        put("card_number", numeroTarjeta)
                        put("expiration_month", mes.toInt())
                        put("expiration_year", anio.toInt())
                        put("security_code", cvv)
                        put("cardholder", buildJsonObject {
                            put("name", nombreTitular)
                        })
                    })
                }

                if (response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK) {
                    val jsonResponse = Json.parseToJsonElement(response.bodyAsText()).jsonObject
                    val tokenId = jsonResponse["id"]?.jsonPrimitive?.content ?: ""

                    val userId = Firebase.auth.currentUser?.uid
                    if (userId != null) {
                        val ultimos4 = numeroTarjeta.takeLast(4)
                        val mascara = "**** **** **** $ultimos4"

                        Firebase.firestore.collection("usuarios").document(userId)
                            .update(
                                "billetera" to mascara,
                                "card_token" to tokenId
                            )

                        loadUserData()

                        isLoading = false
                        errorMessage = "¡Tarjeta vinculada con éxito (Sandbox)!"

                        numeroTarjeta = ""
                        nombreTitular = ""
                        fechaExpiracion = ""
                        cvv = ""
                    } else {
                        isLoading = false
                        errorMessage = "Error: No hay una sesión activa."
                    }
                } else {
                    isLoading = false
                    errorMessage = "Mercado Pago rechazó la tarjeta. Revisa los datos."
                }
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "Error de conexión: ${e.message}"
            }
        }
    }

    // =====================================================================
    // NUEVA FUNCIÓN: Ejecutar el cobro real de prueba a Mercado Pago
    // =====================================================================
    fun realizarCobroPrueba() {
        isLoading = true
        errorMessage = "Procesando pago de \$15.00..."

        viewModelScope.launch {
            try {
                // 1. Verificamos la sesión y extraemos los datos del usuario
                val userId = Firebase.auth.currentUser?.uid
                if (userId == null) {
                    errorMessage = "Error: No hay una sesión activa."
                    isLoading = false
                    return@launch
                }

                val userDoc = Firebase.firestore.collection("usuarios").document(userId).get()

                // Usamos validación segura en caso de que el campo aún no exista
                val cardToken = if (userDoc.contains("card_token")) userDoc.get<String>("card_token") else ""
                val userEmail = if (userDoc.contains("email")) userDoc.get<String>("email") else "test@test.com"

                if (cardToken.isEmpty()) {
                    errorMessage = "No tienes ninguna tarjeta vinculada."
                    isLoading = false
                    return@launch
                }

                // 2. Preparamos el cliente HTTP
                val client = HttpClient {
                    install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
                }

                // 3. Ejecutamos el cargo por $15 pesos
                val response: HttpResponse = client.post("https://api.mercadopago.com/v1/payments") {
                    header(HttpHeaders.Authorization, "Bearer $MERCADO_PAGO_ACCESS_TOKEN")
                    contentType(ContentType.Application.Json)
                    setBody(buildJsonObject {
                        put("transaction_amount", 15.00)
                        put("token", cardToken)
                        put("description", "Orden de prueba en la cafetería El Naranjo")
                        put("installments", 1)
                        put("payment_method_id", "visa")
                        put("payer", buildJsonObject {
                            put("email", userEmail)
                        })
                    })
                }

                // 4. Analizamos la respuesta
                if (response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK) {
                    val jsonResponse = Json.parseToJsonElement(response.bodyAsText()).jsonObject
                    val statusPago = jsonResponse["status"]?.jsonPrimitive?.content ?: ""

                    if (statusPago == "approved") {
                        errorMessage = "¡Cobro exitoso! El dinero ya está en Mercado Pago."
                    } else {
                        errorMessage = "Cobro procesado pero en estado: $statusPago"
                    }
                } else {
                    val errorBody = response.bodyAsText()
                    errorMessage = "Error en el cobro: ${response.status}"
                    println("ERROR MERCADO PAGO: $errorBody") // Imprime en Logcat el motivo del rechazo
                }

            } catch (e: Exception) {
                errorMessage = "Fallo de conexión al cobrar: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}