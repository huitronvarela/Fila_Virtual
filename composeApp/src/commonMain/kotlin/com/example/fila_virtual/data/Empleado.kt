package com.example.fila_virtual.data

import kotlinx.serialization.Serializable

@Serializable
data class Empleado(
    val id: String = "",
    val establecimientoId: String = "",
    val nombre: String = "",
    val correo: String = "",
    val telefono: String = "",
    val rol: String = "",
    val activo: Boolean = true
)