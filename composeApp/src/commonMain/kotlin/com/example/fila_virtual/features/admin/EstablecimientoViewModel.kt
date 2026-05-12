package com.example.fila_virtual.features.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fila_virtual.data.Establecimiento
import com.example.fila_virtual.repository.EstablecimientoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EstablecimientoViewModel : ViewModel() {
    private val repository = EstablecimientoRepository()

    private val _uiState = MutableStateFlow<FormState>(FormState.Idle)
    val uiState: StateFlow<FormState> = _uiState

    // Flow de establecimientos desde el repositorio
    val establecimientos: StateFlow<List<Establecimiento>> = repository.getEstablecimientos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun guardarEstablecimiento(establecimiento: Establecimiento, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = FormState.Loading
            val result = repository.guardarEstablecimiento(establecimiento)
            if (result.isSuccess) {
                _uiState.value = FormState.Success
                onSuccess()
            } else {
                _uiState.value = FormState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun actualizarEstado(id: String, activo: Boolean) {
        viewModelScope.launch {
            repository.actualizarEstado(id, activo)
        }
    }

    fun resetState() {
        _uiState.value = FormState.Idle
    }
}

sealed class FormState {
    object Idle : FormState()
    object Loading : FormState()
    object Success : FormState()
    data class Error(val message: String) : FormState()
}
