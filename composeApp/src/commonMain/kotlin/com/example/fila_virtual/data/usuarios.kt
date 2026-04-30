package com.example.fila_virtual.data

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val nombre: String,
    val telefono: String,
    val email: String,
    val tipoUsuario: String,
    val billetera: String,
    val fechaRegistro: String,
    val fotoUrl: String? = null
)

// 2. Sub-colección: establecimientos/{id}/empleados/{uid}
@Serializable
data class Empleado(
    val uid: String = "",
    val rol: String = "", // "cajero", "cocina", "entrega", "gerente"
    val activo: Boolean = true,
    val joinedAt: Long = 0L
)
// 3. Colección global: pedidos/{pedidoId}
@Serializable
data class Pedido(
    val pedidoId: String = "",
    val turnoId: String = "",
    val userId: String = "",
    val establecimientoId: String = "",
    val estado: String = "pendiente", // pendiente, preparando, listo, entregado
    val total: Double = 0.0,
    val codigoQR: String = "",
    val clienteNombre: String = "", // Snapshot para no hacer lecturas cruzadas
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)