package com.example.fila_virtual.data

import kotlinx.serialization.Serializable

/**
 * Roles globales de la plataforma.
 * CLIENTE: Usuario que realiza pedidos.
 * ADMIN: Administrador general con acceso a todo el sistema.
 * EMPLEADO: Usuario con permisos específicos en establecimientos.
 * CLIENTE: Usuario que realiza pedidos.
 * ADMIN: Administrador general con acceso a todo el sistema.
 * EMPLEADO: Usuario con permisos específicos en establecimientos.
 */
object Roles {
    const val CLIENTE = "cliente"
    const val ADMIN = "admin"
    const val EMPLEADO = "empleado"
}

/**
 * Roles específicos para el personal de los establecimientos.
 */
object RolEmpleado {
    const val CAJERO = "cajero"
    const val COCINA = "cocina"
    const val ENTREGA = "entrega"
    const val GERENTE = "gerente"
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

// 2. Sub-colección: establecimientos/{id}/empleados/{uid}
// 2. Sub-colección: establecimientos/{id}/empleados/{uid}
@Serializable
data class Empleado(
    val uid: String = "",
    val rol: String = RolEmpleado.CAJERO,
    val activo: Boolean = true,
    val joinedAt: Long = 0L
)

// 3. Colección global: pedidos/{pedidoId}
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
