package com.example.fila_virtual.core.data

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val nombre: String,
    val telefono: String,
    val email: String,
    val tipoUsuario: String,
    val billetera: String,
    val fechaRegistro: String
)