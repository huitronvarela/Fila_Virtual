package com.example.fila_virtual.repository

import com.example.fila_virtual.data.Establecimiento
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class EstablecimientoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val establecimientosRef = db.collection("establecimientos")

    suspend fun guardarEstablecimiento(establecimiento: Establecimiento): Result<Unit> {
        return try {
            val id = establecimiento.id.ifEmpty { establecimientosRef.document().id }
            val finalEstablecimiento = establecimiento.copy(id = id)

            establecimientosRef.document(id).set(finalEstablecimiento).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarEstado(id: String, activo: Boolean): Result<Unit> {
        return try {
            establecimientosRef.document(id).update("activo", activo).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getEstablecimientos(): Flow<List<Establecimiento>> = callbackFlow {
        val subscription = establecimientosRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val establecimientos = snapshot.toObjects(Establecimiento::class.java)
                trySend(establecimientos)
            }
        }
        awaitClose { subscription.remove() }
    }
}
