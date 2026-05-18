package com.example.fila_virtual.data

import kotlinx.serialization.Serializable

/**
 * 🏢 ENTIDAD PRINCIPAL: Establecimiento
 * Ruta en Firestore: establecimientos/{establecimientoId}
 */
@Serializable
data class Establecimiento(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val logoUrl: String = "",
    val ubicacion: Ubicacion = Ubicacion(),
    val horario: Map<String, HorarioDia> = emptyMap(),
    val activo: Boolean = true,
    val ownerUid: String = "",
    val gerenteUid: String? = null,
    val categorias: List<String> = emptyList(),
    val ratingAvg: Double = 0.0,
    val ratingCount: Int = 0,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

@Serializable
data class Ubicacion(
    val direccion: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val referencia: String = ""
)

@Serializable
data class HorarioDia(
    val apertura: String = "09:00",
    val cierre: String = "18:00",
    val cerrado: Boolean = false
)



/**
 * 📋 SUBCOLECCIÓN: Eventos de Auditoría (Bitácora)
 */
@Serializable
data class AuditEvent(
    val eventId: String = "",
    val type: String = "",
    val actorUid: String = "",
    val actorRolSnapshot: String = "",
    val targetType: String = "",
    val targetId: String = "",
    val payload: Map<String, String> = emptyMap(),
    val createdAt: Long = 0L
)

/**
 * 📊 SUBCOLECCIÓN: Métricas Diarias de Empleados
 */
@Serializable
data class EmpleadoMetricasDiarias(
    val docId: String = "",
    val uid: String = "",
    val dayKey: String = "",
    val counts: Map<String, Int> = emptyMap(),
    val updatedAt: Long = 0L
)
