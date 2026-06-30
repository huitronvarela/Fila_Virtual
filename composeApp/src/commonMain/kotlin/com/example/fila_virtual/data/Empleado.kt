package com.example.fila_virtual.data

import kotlinx.serialization.Serializable

/**
 * 🧑‍🍳 SUBCOLECCIÓN: Empleados
 * Ruta en Firestore: establecimientos/{establecimientoId}/empleados/{uid}
 * * Nota: Para mostrar los datos del empleado en la UI (nombre, foto),
 * debes cruzar este 'uid' con la colección global 'users'.
 */
@Serializable
data class Empleado(
    val uid: String = "",
    val rol: String = "empleado",
    val activo: Boolean = true,
    val joinedAt: Long = 0L,
    val updatedAt: Long = 0L
)