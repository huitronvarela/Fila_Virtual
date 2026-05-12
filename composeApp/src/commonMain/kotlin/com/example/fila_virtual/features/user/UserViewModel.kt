package com.example.fila_virtual.features.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fila_virtual.data.TarjetaGuardada // Importación de la nueva clase
import com.example.fila_virtual.data.Usuario
import com.example.fila_virtual.repository.UserRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.functions.functions
import kotlinx.coroutines.launch

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*

class UserViewModel(private val repository: UserRepository = UserRepository()) : ViewModel() {

    private val MERCADO_PAGO_PUBLIC_KEY = "TEST-f1ae3349-69ba-4fed-b8b4-72166ffb423d"

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

    init { loadUserData() }

    fun loadUserData() {
        val uid = repository.getCurrentUserUid()
        if (uid != null) {
            viewModelScope.launch {
                isLoading = true
                errorMessage = ""
                try {
                    var data = repository.getUserData(uid)
                    if (data != null) {
                        if (data.fotoUrl.isEmpty()) {
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
                val now = dev.gitlive.firebase.firestore.Timestamp.now().seconds * 1000
                val updates = mutableMapOf<String, Any?>(
                    "nombre" to nombre,
                    "telefono" to telefono,
                    "updatedAt" to now
                )
                if (fotoUrl != null) {
                    updates["fotoUrl"] = fotoUrl
                }
                val success = repository.updateUserData(uid, updates)
                if (success) { loadUserData() }
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

    fun onNumeroTarjetaChange(nuevoNumero: String) { if (nuevoNumero.length <= 16) numeroTarjeta = nuevoNumero }
    fun onNombreTitularChange(nuevoNombre: String) { nombreTitular = nuevoNombre }
    fun onFechaExpiracionChange(nuevaFecha: String) { if (nuevaFecha.length <= 4) fechaExpiracion = nuevaFecha }
    fun onCvvChange(nuevoCvv: String) { if (nuevoCvv.length <= 4) cvv = nuevoCvv }

    fun procesarPagoSeguro() {
        if (numeroTarjeta.length < 16 || cvv.isEmpty() || fechaExpiracion.isEmpty()) {
            errorMessage = "Por favor completa todos los campos correctamente."
            return
        }
        isLoading = true
        errorMessage = ""
        viewModelScope.launch {
            try {
                val mes = fechaExpiracion.substring(0, 2)
                val anio = "20" + fechaExpiracion.substring(2, 4)
                val client = HttpClient { install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) } }
                val response: HttpResponse = client.post("https://api.mercadopago.com/v1/card_tokens?public_key=$MERCADO_PAGO_PUBLIC_KEY") {
                    contentType(ContentType.Application.Json)
                    setBody(buildJsonObject {
                        put("card_number", numeroTarjeta)
                        put("expiration_month", mes.toInt())
                        put("expiration_year", anio.toInt())
                        put("security_code", cvv)
                        put("cardholder", buildJsonObject { put("name", nombreTitular) })
                    })
                }
                if (response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK) {
                    val jsonResponse = Json.parseToJsonElement(response.bodyAsText()).jsonObject
                    val tokenId = jsonResponse["id"]?.jsonPrimitive?.content ?: ""
                    val userId = Firebase.auth.currentUser?.uid

                    if (userId != null) {
                        // 1. Extraemos y formateamos
                        val ultimos4 = numeroTarjeta.takeLast(4)
                        val expiracionFormateada = "${fechaExpiracion.substring(0, 2)}/${fechaExpiracion.substring(2, 4)}"

                        // 2. Adivinamos la marca de la tarjeta
                        val marcaTarjeta = when {
                            numeroTarjeta.startsWith("4") -> "VISA"
                            numeroTarjeta.startsWith("5") -> "MASTERCARD"
                            numeroTarjeta.startsWith("3") -> "AMEX"
                            else -> "TARJETA"
                        }

                        // 3. Creamos el objeto de datos
                        val nuevaTarjeta = TarjetaGuardada(
                            ultimos4 = ultimos4,
                            marca = marcaTarjeta,
                            nombreTitular = nombreTitular,
                            expiracion = expiracionFormateada,
                            tokenId = tokenId
                        )

                        val now = dev.gitlive.firebase.firestore.Timestamp.now().seconds * 1000
                        val currentMethods = usuario?.metodosPago?.toMutableList() ?: mutableListOf()

                        // 4. Verificamos duplicados (basado en los últimos 4)
                        if (!currentMethods.any { it.ultimos4 == ultimos4 }) {
                            currentMethods.add(nuevaTarjeta)
                        }

                        // 5. Guardamos en Firebase
                        Firebase.firestore.collection("usuarios").document(userId)
                            .update("metodosPago" to currentMethods, "card_token" to tokenId, "updatedAt" to now)

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
    // GATILLO DEL COBRO (VERSIÓN BLINDADA TEXTO PLANO)
    // =====================================================================
    fun realizarCobroConCloudFunction(monto: Double, descripcion: String) {
        isLoading = true
        errorMessage = "Conectando con el banco..."

        viewModelScope.launch {
            try {
                val currentUser = Firebase.auth.currentUser
                if (currentUser == null) {
                    errorMessage = "Error local: Tu sesión expiró."
                    isLoading = false
                    return@launch
                }
                val userId = currentUser.uid

                val functions = Firebase.functions
                val callable = functions.httpsCallable("procesarPagoAlToque")

                // 1. ENVIAMOS TEXTO PLANO
                val jsonPayload = """{"monto": $monto, "descripcion": "$descripcion", "uid": "$userId"}"""
                val result = callable.invoke(jsonPayload)

                // 2. RECIBIMOS TEXTO PLANO (Cero desempacado, cero errores)
                val responseString = result.data<String>()

                // 3. LEEMOS EL TEXTO MANUALMENTE
                if (responseString.contains("\"exito\":true") || responseString.contains("\"exito\": true")) {
                    errorMessage = "¡Cobro exitoso! El Naranjo recibió tu pedido."
                } else {
                    errorMessage = "No pasó: $responseString"
                }

            } catch (e: Exception) {
                errorMessage = "Error de conexión: ${e.message}"
                println("ERROR FIREBASE: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
}