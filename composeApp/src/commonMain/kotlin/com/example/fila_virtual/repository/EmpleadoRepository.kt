package com.example.fila_virtual.repository

import com.example.fila_virtual.data.Empleado
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.where // Importación necesaria para la extensión query

class EmpleadoRepository {
    private val firestore = Firebase.firestore

    suspend fun registrarEmpleado(empleado: Empleado): Result<Unit> {
        return try {
            val empleadosRef = firestore.collection("empleados")
            val docRef = if (empleado.id.isEmpty()) empleadosRef.document else empleadosRef.document(empleado.id)
            val empleadoConId = empleado.copy(id = docRef.id)
            docRef.set(empleadoConId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerEmpleados(establecimientoId: String): Result<List<Empleado>> {
        return try {
            val snapshot = firestore.collection("empleados")
                .where("establecimientoId", equalTo = establecimientoId)
                .get()
            val empleados = snapshot.documents.mapNotNull {
                it.data<Empleado>()
            }
            Result.success(empleados)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
