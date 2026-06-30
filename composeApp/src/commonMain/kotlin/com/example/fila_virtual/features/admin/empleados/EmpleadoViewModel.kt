package com.example.fila_virtual.features.admin.empleados

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fila_virtual.data.Empleado
import com.example.fila_virtual.data.Usuario
import com.example.fila_virtual.features.admin.FormState
import com.example.fila_virtual.repository.EmpleadoRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class EmpleadoDetalle(
    val uid: String = "",
    val nombre: String = "",
    val correo: String = "",
    val rol: String = "",
    val activo: Boolean = true,
    val joinedAt: Long = 0L
)
class EmpleadoViewModel : ViewModel() {

    private val repository = EmpleadoRepository()
    private val db = Firebase.firestore

    private val _uiState = MutableStateFlow<FormState>(FormState.Idle)
    val uiState: StateFlow<FormState> = _uiState

    private val _empleados = MutableStateFlow<List<EmpleadoDetalle>>(emptyList())
    val empleados: StateFlow<List<EmpleadoDetalle>> = _empleados

    fun cargarEmpleados(establecimientoId: String) {
        viewModelScope.launch {
            _uiState.value = FormState.Loading
            val result = repository.obtenerEmpleados(establecimientoId)

            if (result.isSuccess) {
                val listaEmpleados = result.getOrDefault(emptyList())
                val empleadosDetalle = listaEmpleados.map { empleado ->
                    async {
                        try {
                            val userDoc = db.collection("users").document(empleado.uid).get()
                            val usuario = userDoc.data<Usuario>()

                            EmpleadoDetalle(
                                uid = empleado.uid,
                                nombre = usuario.nombre.ifEmpty { "Usuario sin nombre" },
                                correo = usuario.email,
                                rol = empleado.rol,
                                activo = empleado.activo,
                                joinedAt = empleado.joinedAt
                            )
                        } catch (e: Exception) {
                            EmpleadoDetalle(uid = empleado.uid, nombre = "Usuario Desconocido", rol = empleado.rol, activo = empleado.activo)
                        }
                    }
                }.awaitAll()

                _empleados.value = empleadosDetalle
                _uiState.value = FormState.Idle
            } else {
                _uiState.value = FormState.Error(
                    result.exceptionOrNull()?.message ?: "Error al cargar empleados"
                )
            }
        }
    }

    fun guardarEmpleadoPorCorreo(
        correoBusqueda: String,
        rol: String,
        establecimientoId: String,
        onSuccess: () -> Unit
    ) {
        if (correoBusqueda.isBlank()) {
            _uiState.value = FormState.Error("Por favor ingresa un correo válido")
            return
        }

        viewModelScope.launch {
            _uiState.value = FormState.Loading

            try {
                val userQuery = db.collection("users")
                    .where("email", equalTo = correoBusqueda.trim())
                    .get()

                if (userQuery.documents.isEmpty()) {
                    _uiState.value = FormState.Error("No existe ningún usuario registrado con este correo en la app.")
                    return@launch
                }

                val usuarioEncontrado = userQuery.documents.first().data<Usuario>()

                val nuevoEmpleado = Empleado(
                    uid = usuarioEncontrado.uid,
                    rol = rol,
                    activo = true
                )

                val result = repository.registrarEmpleado(establecimientoId, nuevoEmpleado)

                if (result.isSuccess) {
                    _uiState.value = FormState.Success
                    cargarEmpleados(establecimientoId)
                    onSuccess()
                } else {
                    _uiState.value = FormState.Error(
                        result.exceptionOrNull()?.message ?: "Error al vincular empleado"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = FormState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun resetState() {
        _uiState.value = FormState.Idle
    }
}