package com.example.fila_virtual.features.admin.empleados

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fila_virtual.data.Empleado
import com.example.fila_virtual.features.admin.FormState
import com.example.fila_virtual.repository.EmpleadoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EmpleadoViewModel : ViewModel() {

    // Ya sabe buscarlo en la carpeta repository correcta
    private val repository = EmpleadoRepository()

    private val _uiState = MutableStateFlow<FormState>(FormState.Idle)
    val uiState: StateFlow<FormState> = _uiState

    fun guardarEmpleado(
        establecimientoId: String,
        nombre: String,
        correo: String,
        telefono: String,
        rol: String,
        onSuccess: () -> Unit
    ) {
        if (nombre.isBlank() || correo.isBlank() || telefono.isBlank()) {
            _uiState.value = FormState.Error("Por favor llena todos los campos")
            return
        }

        viewModelScope.launch {
            _uiState.value = FormState.Loading

            val nuevoEmpleado = Empleado(
                establecimientoId = establecimientoId,
                nombre = nombre,
                correo = correo,
                telefono = telefono,
                rol = rol,
                activo = true
            )

            val result = repository.registrarEmpleado(nuevoEmpleado)

            if (result.isSuccess) {
                _uiState.value = FormState.Success
                onSuccess()
            } else {
                _uiState.value = FormState.Error(result.exceptionOrNull()?.message ?: "Error al registrar")
            }
        }
    }

    fun resetState() {
        _uiState.value = FormState.Idle
    }
}