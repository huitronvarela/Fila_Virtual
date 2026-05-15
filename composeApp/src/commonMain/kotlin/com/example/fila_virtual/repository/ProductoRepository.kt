package com.example.fila_virtual.repository

import com.example.fila_virtual.data.Producto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProductoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val productosRef = db.collection("productos")

    suspend fun guardarProducto(producto: Producto): Result<Unit> {
        return try {
            val id = producto.id.ifEmpty { productosRef.document().id }
            val finalProducto = producto.copy(
                id = id,
                createdAt = if (producto.createdAt == 0L) System.currentTimeMillis() else producto.createdAt,
                updatedAt = System.currentTimeMillis()
            )

            productosRef.document(id).set(finalProducto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getProductos(establecimientoId: String): Flow<List<Producto>> = callbackFlow {
        val subscription = productosRef
            .whereEqualTo("establecimientoId", establecimientoId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val productos = snapshot.toObjects(Producto::class.java)
                    trySend(productos)
                }
            }
        awaitClose { subscription.remove() }
    }

    fun getProductosByOwner(ownerUid: String): Flow<List<Producto>> = callbackFlow {
        val subscription = productosRef
            .whereEqualTo("ownerUid", ownerUid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val productos = snapshot.toObjects(Producto::class.java)
                    trySend(productos)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun actualizarDisponibilidad(productoId: String, disponible: Boolean): Result<Unit> {
        return try {
            productosRef.document(productoId).update("disponible", disponible).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
