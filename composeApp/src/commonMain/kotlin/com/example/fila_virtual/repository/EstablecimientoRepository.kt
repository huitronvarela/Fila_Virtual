package com.example.fila_virtual.repository

import com.example.fila_virtual.data.Establecimiento
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EstablecimientoRepository {
    private val db = Firebase.firestore
    private val establecimientosRef = db.collection("establecimientos")

    suspend fun guardarEstablecimiento(establecimiento: Establecimiento): Result<Unit> {
        return try {
            // Generamos la referencia: si no tiene ID, Firebase crea uno nuevo
            val docRef = if (establecimiento.id.isEmpty()) {
                establecimientosRef.document
            } else {
                establecimientosRef.document(establecimiento.id)
            }

            val finalEstablecimiento = establecimiento.copy(id = docRef.id)
            docRef.set(finalEstablecimiento)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarEstado(id: String, activo: Boolean): Result<Unit> {
        return try {
            establecimientosRef.document(id).update("activo" to activo)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- ESTAS SON LAS FUNCIONES QUE FALTABAN ---

    fun getEstablecimientos(): Flow<List<Establecimiento>> {
        return establecimientosRef.snapshots.map { snapshot ->
            snapshot.documents.map { it.data<Establecimiento>() }
        }
    }

    fun getEstablecimientosByOwner(ownerUid: String): Flow<List<Establecimiento>> {
        // Importante: Asegúrate de tener importado dev.gitlive.firebase.firestore.where arriba
        return establecimientosRef.where { "ownerUid" equalTo ownerUid }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data<Establecimiento>() }
            }
    }
}