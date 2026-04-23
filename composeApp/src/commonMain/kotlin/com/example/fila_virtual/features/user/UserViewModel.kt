package com.example.fila_virtual.features.user

// Importaciones de Ktor para HTTP
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
        if (numeroTarjeta.length < 16 || cvv.isEmpty() || fechaExpiracion.isEmpty()) {
            errorMessage = "Por favor completa todos los campos correctamente."
            return
        }

        isLoading = true
        errorMessage = ""

        // En Multiplatform (GitLive), Firebase usa Corrutinas
        viewModelScope.launch {
            try {
                // 1. Obtenemos al usuario activo
                val userId = Firebase.auth.currentUser?.uid
                if (userId == null) {
                    isLoading = false
                    errorMessage = "Error: No hay una sesión activa."
                    return@launch
                }

                // 2. Solo tomamos los últimos 4 dígitos por seguridad
                val ultimos4Digitos = numeroTarjeta.takeLast(4)
                val tarjetaSegura = "**** **** **** $ultimos4Digitos"

                // 3. Lo subimos a Firestore
                Firebase.firestore.collection("usuarios").document(userId)
                    .update("billetera" to tarjetaSegura)
                // ¡NUEVA LÍNEA! Le decimos a la app que recargue tu perfil para ver la tarjeta
                loadUserData()

                // 4. Si llega a esta línea, todo salió bien
                isLoading = false


                errorMessage = "¡Tarjeta vinculada correctamente!"

                // Limpiamos los campos visuales
                numeroTarjeta = ""
                nombreTitular = ""
                fechaExpiracion = ""
                cvv = ""

            } catch (e: Exception) {
                // Si algo falla en la conexión, cae aquí automáticamente
                isLoading = false
                errorMessage = "Error al guardar la tarjeta: ${e.message}"
            }
        }
    }
}