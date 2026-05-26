package com.example.fila_virtual.data

import kotlinx.serialization.Serializable

@Serializable
data class Pedido(
    val id: String = "",
    val userId: String = "",
    val establecimientoId: String = "",
    val establecimientoNombre: String = "",
    val descripcion: String = "", // <--- NUEVO CAMPO AÑADIDO
    val total: Double = 0.0,
    val estado: String = "RECIBIDO",
    val turno: Int = 0,
    val createdAt: Long = 0L
)