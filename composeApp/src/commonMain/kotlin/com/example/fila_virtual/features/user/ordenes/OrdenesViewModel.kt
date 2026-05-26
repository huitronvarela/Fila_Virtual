package com.example.fila_virtual.features.user.ordenes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fila_virtual.data.Pedido

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class OrdenesViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val pedidosRef = db.collection("pedidos")

    // Estado para guardar los pedidos descargados de Firebase
    private val _pedidosActivos = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidosActivos: StateFlow<List<Pedido>> = _pedidosActivos

    private val _pedidosHistorial = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidosHistorial: StateFlow<List<Pedido>> = _pedidosHistorial

    var isLoading = MutableStateFlow(true)
        private set

    init {
        escucharPedidosDelUsuario()
    }

    private fun escucharPedidosDelUsuario() {
        val userId = Firebase.auth.currentUser?.uid
        if (userId == null) {
            isLoading.value = false
            return
        }

        viewModelScope.launch {
            // Escuchamos en tiempo real los pedidos de este usuario
            pedidosRef.where { "userId" equalTo userId }
                .snapshots
                .map { snapshot -> snapshot.documents.map { it.data<Pedido>() } }
                .collect { todosLosPedidos ->
                    // Filtramos en locales:
                    // Activos = RECIBIDO, PREPARANDO, EN CAMINO o LISTO
                    val activos = todosLosPedidos.filter {
                        it.estado != "ENTREGADO" && it.estado != "CANCELADO"
                    }.sortedByDescending { it.createdAt }

                    // Historial = ENTREGADO o CANCELADO
                    val historial = todosLosPedidos.filter {
                        it.estado == "ENTREGADO" || it.estado == "CANCELADO"
                    }.sortedByDescending { it.createdAt }

                    _pedidosActivos.value = activos
                    _pedidosHistorial.value = historial
                    isLoading.value = false
                }
        }
    }
}