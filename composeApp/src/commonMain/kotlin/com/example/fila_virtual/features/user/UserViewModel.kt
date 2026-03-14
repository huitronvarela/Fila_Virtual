package com.example.fila_virtual.features.user

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fila_virtual.data.Usuario
import com.example.fila_virtual.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository = UserRepository()) : ViewModel() {

    var usuario by mutableStateOf<Usuario?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf("")
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
                    // 1. Obtenemos los datos del usuario desde Firestore
                    var data = repository.getUserData(uid)

                    if (data != null) {
                        // 2. Si Firestore NO tiene foto, sacamos la foto de la sesión de Google
                        if (data.fotoUrl.isNullOrEmpty()) {
                            val firebaseUser = repository.getFirebaseUser()
                            val googlePhotoUrl = firebaseUser?.photoURL

                            // Si Google sí tiene foto, se la inyectamos a nuestros datos usando .copy()
                            if (googlePhotoUrl != null) {
                                data = data.copy(fotoUrl = googlePhotoUrl)
                            }
                        }

                        // 3. Asignamos el usuario (ahora con foto) al estado de la vista
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
}