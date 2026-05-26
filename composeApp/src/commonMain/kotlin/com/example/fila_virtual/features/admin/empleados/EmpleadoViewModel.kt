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

    private val repository = EmpleadoRepository()

    private val _uiState = MutableStateFlow<FormState>(FormState.Idle)
    val uiState: StateFlow<FormState> = _uiState

    private val _empleados = MutableStateFlow<List<Empleado>>(emptyList())
    val empleados: StateFlow<List<Empleado>> = _empleados

    fun cargarEmpleados(establecimientoId: String) {
        viewModelScope.launch {
            _uiState.value = FormState.Loading
            val result = repository.obtenerEmpleados(establecimientoId)
            if (result.isSuccess) {
                _empleados.value = result.getOrDefault(emptyList())
                _uiState.value = FormState.Idle
            } else {
                _uiState.value = FormState.Error(
                    result.exceptionOrNull()?.message ?: "Error al cargar empleados"
                )
            }
        }
    }

    fun guardarEmpleado(
        id: String = "",
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
                id = id,
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
                // Recargar la lista tras registrar exitosamente
                cargarEmpleados(establecimientoId)
                onSuccess()
            } else {
                _uiState.value = FormState.Error(
                    result.exceptionOrNull()?.message ?: "Error al registrar"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = FormState.Idle
    }
}