package com.example.fila_virtual.features.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fila_virtual.core.ErrorMessages // <-- ESTE IMPORT ARREGLA EL PRIMER ERROR
import com.example.fila_virtual.data.Pedido
import com.example.fila_virtual.data.ProductoCarrito
import com.example.fila_virtual.data.TarjetaGuardada
import com.example.fila_virtual.data.Usuario
import com.example.fila_virtual.repository.UserRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
                    val data = repository.getUserData(uid)
                    if (data != null) {
                        usuario = data
                    } else {
                        errorMessage = ErrorMessages.USER_NOT_FOUND
                    }
                } catch (e: Exception) {
                    errorMessage = ErrorMessages.DATABASE_ERROR
                    println("Error técnico en loadUserData: ${e.message}")
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
        if (numeroTarjeta.length < 16) {
            errorMessage = ErrorMessages.INVALID_CARD_NUMBER
            return
        }

        if (cvv.length < 3) {
            errorMessage = ErrorMessages.INVALID_CVV
            return
        }

        if (fechaExpiracion.length < 4) {
            errorMessage = ErrorMessages.INVALID_EXPIRATION_DATE
            return
        }

        val mes = fechaExpiracion.substring(0, 2).toIntOrNull() ?: 0
        if (mes !in 1..12) {
            errorMessage = ErrorMessages.INVALID_EXPIRATION_DATE
            return
        }

        isLoading = true
        errorMessage = ""

        viewModelScope.launch {
            try {
                val anio = "20" + fechaExpiracion.substring(2, 4)

                val client = HttpClient {
                    install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
                }

                // <-- ESTO ARREGLA EL SEGUNDO ERROR (La variable response existe de nuevo)
                val response: HttpResponse = client.post("https://api.mercadopago.com/v1/card_tokens?public_key=$MERCADO_PAGO_PUBLIC_KEY") {
                    contentType(ContentType.Application.Json)
                    setBody(buildJsonObject {
                        put("card_number", numeroTarjeta)
                        put("expiration_month", mes)
                        put("expiration_year", anio.toInt())
                        put("security_code", cvv)
                        put("cardholder", buildJsonObject {
                            put("name", nombreTitular.ifEmpty { "ALTOQUE USER" })
                        })
                    })
                }

                if (response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK) {
                    val jsonResponse = Json.parseToJsonElement(response.bodyAsText()).jsonObject
                    val tokenId = jsonResponse["id"]?.jsonPrimitive?.content ?: ""

                    val userId = Firebase.auth.currentUser?.uid
                    if (userId != null) {
                        val ultimos4 = numeroTarjeta.takeLast(4)
                        val expiracionFormateada = "${fechaExpiracion.substring(0, 2)}/${fechaExpiracion.substring(2, 4)}"
                        val now = dev.gitlive.firebase.firestore.Timestamp.now().seconds * 1000

                        val nuevaTarjeta = TarjetaGuardada(
                            ultimos4 = ultimos4,
                            marca = "VISA",
                            nombreTitular = nombreTitular,
                            expiracion = expiracionFormateada,
                            tokenId = tokenId
                        )

                        val currentMethods = usuario?.metodosPago?.toMutableList() ?: mutableListOf()
                        val yaExiste = currentMethods.any { it.ultimos4 == ultimos4 }

                        if (yaExiste) {
                            isLoading = false
                            errorMessage = ErrorMessages.DUPLICATE_CARD
                            return@launch
                        }

                        currentMethods.add(nuevaTarjeta)

                        val metodosComoMapa = currentMethods.map { t ->
                            mapOf(
                                "ultimos4" to t.ultimos4,
                                "marca" to t.marca,
                                "nombreTitular" to t.nombreTitular,
                                "expiracion" to t.expiracion,
                                "tokenId" to t.tokenId
                            )
                        }

                        Firebase.firestore.collection("usuarios").document(userId)
                            .update(
                                "metodosPago" to metodosComoMapa,
                                "card_token" to tokenId,
                                "updatedAt" to now
                            )

                        isLoading = false
                        errorMessage = "¡Tarjeta vinculada correctamente!"

                        numeroTarjeta = ""
                        nombreTitular = ""
                        fechaExpiracion = ""
                        cvv = ""
                    } else {
                        isLoading = false
                        errorMessage = ErrorMessages.SESSION_EXPIRED
                    }
                } else {
                    isLoading = false
                    errorMessage = ErrorMessages.PAYMENT_REJECTED
                }
            } catch (e: Exception) {
                isLoading = false
                errorMessage = ErrorMessages.NETWORK_ERROR
                println("Error de red en procesarPagoSeguro: ${e.message}")
            }
        }
    }

    fun realizarCobroPrueba() {
        isLoading = true
        errorMessage = "Procesando pago de \$15.00..."

        viewModelScope.launch {
            try {
                val userId = Firebase.auth.currentUser?.uid
                if (userId == null) {
                    errorMessage = ErrorMessages.SESSION_EXPIRED
                    isLoading = false
                    return@launch
                }

                val userDoc = Firebase.firestore.collection("usuarios").document(userId).get()

                val cardToken = if (userDoc.contains("card_token")) userDoc.get<String>("card_token") else ""
                val userEmail = if (userDoc.contains("email")) userDoc.get<String>("email") else "test@test.com"

                if (cardToken.isEmpty()) {
                    errorMessage = ErrorMessages.NO_PAYMENT_METHOD_SELECTED
                    isLoading = false
                    return@launch
                }

                val client = HttpClient {
                    install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
                }

                // <-- AQUÍ TAMBIÉN ESTÁ LA VARIABLE RESPONSE
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

                if (response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK) {
                    val jsonResponse = Json.parseToJsonElement(response.bodyAsText()).jsonObject
                    val statusPago = jsonResponse["status"]?.jsonPrimitive?.content ?: ""

                    if (statusPago == "approved") {
                        errorMessage = "¡Cobro exitoso! El dinero ya está en Mercado Pago."
                    } else {
                        errorMessage = ErrorMessages.PAYMENT_REJECTED
                    }
                } else {
                    errorMessage = ErrorMessages.PAYMENT_REJECTED
                    println("ERROR MERCADO PAGO: ${response.bodyAsText()}")
                }

            } catch (e: Exception) {
                errorMessage = ErrorMessages.NETWORK_ERROR
                println("Fallo de conexión al cobrar: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    // ==========================================
    // 🛒 LÓGICA DEL CARRITO DE COMPRAS
    // ==========================================

    private val _carrito = MutableStateFlow<List<ProductoCarrito>>(emptyList())
    val carrito: StateFlow<List<ProductoCarrito>> = _carrito

    fun agregarAlCarrito(idProducto: String, nombre: String, precio: Double) {
        val listaActual = _carrito.value.toMutableList()
        val itemExistente = listaActual.find { it.idProducto == idProducto }

        if (itemExistente != null) {
            val index = listaActual.indexOf(itemExistente)
            listaActual[index] = itemExistente.copy(cantidad = itemExistente.cantidad + 1)
        } else {
            listaActual.add(ProductoCarrito(idProducto, nombre, precio, 1))
        }
        _carrito.value = listaActual
    }

    fun vaciarCarrito() {
        _carrito.value = emptyList()
    }

    fun calcularTotalCarrito(): Double {
        return _carrito.value.sumOf { it.precio * it.cantidad }
    }

    // ==========================================
    // 💳 FUNCIÓN DE COBRO + CREACIÓN DE PEDIDO REAL
    // ==========================================

    fun procesarCompraDelCarrito(
        establecimientoId: String,
        establecimientoNombre: String,
        onSuccess: () -> Unit
    ) {
        if (_carrito.value.isEmpty()) {
            errorMessage = ErrorMessages.CART_EMPTY
            return
        }

        isLoading = true
        errorMessage = ""

        viewModelScope.launch {
            try {
                val userId = Firebase.auth.currentUser?.uid
                if (userId == null) {
                    errorMessage = ErrorMessages.SESSION_EXPIRED
                    isLoading = false
                    return@launch
                }

                // 1. SIMULAMOS EL TIEMPO DE PAGO
                kotlinx.coroutines.delay(1500)

                // 2. Extraemos los datos reales del carrito
                val descripcionReal = _carrito.value.joinToString(", ") { "${it.cantidad}x ${it.nombre}" }
                val montoTotalReal = calcularTotalCarrito()

                // 3. Preparamos los datos para Firebase NoSQL
                val turnoGenerado = (1..99).random()
                val now = dev.gitlive.firebase.firestore.Timestamp.now().seconds * 1000

                val pedidosRef = Firebase.firestore.collection("pedidos")
                val nuevoPedidoRef = pedidosRef.document

                val nuevoPedido = Pedido(
                    id = nuevoPedidoRef.id,
                    userId = userId,
                    establecimientoId = establecimientoId,
                    establecimientoNombre = establecimientoNombre,
                    descripcion = descripcionReal,
                    total = montoTotalReal,
                    estado = "RECIBIDO",
                    turno = turnoGenerado,
                    createdAt = now
                )

                // 4. Subimos el pedido y limpiamos todo
                nuevoPedidoRef.set(nuevoPedido)

                vaciarCarrito()

                errorMessage = ""
                isLoading = false
                onSuccess()

            } catch (e: Exception) {
                errorMessage = ErrorMessages.TURN_GENERATION_FAILED
                println("Error NoSQL al guardar el pedido: ${e.message}")
                isLoading = false
            }
        }
    }
}