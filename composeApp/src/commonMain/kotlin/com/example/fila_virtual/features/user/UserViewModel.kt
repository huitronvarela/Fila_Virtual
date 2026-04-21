package com.example.fila_virtual.features.user

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fila_virtual.data.Usuario
import com.example.fila_virtual.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository = UserRepository()) : ViewModel() {

    // Cambiamos el acceso a repository a public para que EditProfileScreen pueda usarlo
    val userRepository: UserRepository get() = repository

    var usuario by mutableStateOf<Usuario?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf("")
        private set

    init {
        loadUserData()
    }

    // En UserViewModel.kt
    fun loadUserData() {
        val uid = repository.getCurrentUserUid()
        if (uid != null) {
            viewModelScope.launch {
                isLoading = true
                try {
                    var data = repository.getUserData(uid)

                    if (data != null) {
                        // 1. Forzar obtención de foto si Firestore no la tiene
                        if (data.fotoUrl.isNullOrEmpty()) {
                            val firebaseUser = repository.getFirebaseUser()

                            // Intentamos obtener la foto principal
                            var googlePhotoUrl = firebaseUser?.photoURL

                            // Si está vacía, buscamos dentro de los datos del proveedor (Google)
                            if (googlePhotoUrl.isNullOrEmpty()) {
                                googlePhotoUrl = firebaseUser?.providerData?.firstOrNull { !it.photoURL.isNullOrEmpty() }?.photoURL
                            }

                            if (!googlePhotoUrl.isNullOrEmpty()) {
                                println("DEBUG: Foto encontrada en Google -> $googlePhotoUrl")
                                // Guardamos la URL en el estado local
                                data = data.copy(fotoUrl = googlePhotoUrl)
                            } else {
                                println("DEBUG: La foto de Google llegó NULA en todos lados")
                            }
                        }

                        // 2. Verificación de teléfono
                        val telefonoFinal = if (data.telefono.isNullOrEmpty()) "Sin registrar" else data.telefono
                        data = data.copy(telefono = telefonoFinal)

                        usuario = data
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
}
