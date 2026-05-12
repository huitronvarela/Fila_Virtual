package com.example.fila_virtual.data

import kotlinx.serialization.Serializable

@Serializable
data class Producto(
    val id: String = "",
    val establecimientoId: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val categoria: String = "",
    val imagenUrl: String = "",
    val disponible: Boolean = true,
    val popularidad: Double = 0.0,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

object CategoriasProducto {
    const val COMIDA = "Comida"
    const val BEBIDAS = "Bebidas"
    const val SNACKS = "Snacks"
    const val POSTRES = "Postres"
}
