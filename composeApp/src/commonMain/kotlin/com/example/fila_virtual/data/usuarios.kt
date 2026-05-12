package com.example.fila_virtual.data

import kotlinx.serialization.Serializable

/**
 * Roles globales de la plataforma.
 * CLIENTE: Usuario que realiza pedidos.
 * ADMIN: Administrador general con acceso a todo el sistema.
 * EMPLEADO: Usuario con permisos específicos en algún establecimiento.
 */
object Roles {
    const val CLIENTE = "cliente"
    const val ADMIN = "admin"
    const val EMPLEADO = "empleado"
}

@Serializable
data class Usuario(
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val fotoUrl: String = "",
    val rolGlobal: String = Roles.CLIENTE,
    val metodosPago: List<String> = emptyList(),
    val verificado: Boolean = false,
    val activo: Boolean = true,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

/**
 * 📋 Colección global: pedidos/{pedidoId}
 */
@Serializable
data class Pedido(
    val pedidoId: String = "",
    val turnoId: String = "",
    val userId: String = "",
    val establecimientoId: String = "",
    val estado: String = "pendiente", // pendiente, preparando, listo, entregado
    val total: Double = 0.0,
    val codigoQR: String = "",
    val clienteNombre: String = "", // Snapshot para evitar lecturas cruzadas
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
