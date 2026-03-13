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
                    val data = repository.getUserData(uid)
                    if (data != null) {
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
