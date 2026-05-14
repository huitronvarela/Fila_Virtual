package com.example.fila_virtual.repository

import com.example.fila_virtual.data.Empleado
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

class EmpleadoRepository {
    private val firestore = Firebase.firestore

    suspend fun registrarEmpleado(empleado: Empleado): Result<Unit> {
        return try {
            val empleadosRef = firestore.collection("empleados")

            // 👇 ¡Sin paréntesis para generar un ID aleatorio automáticamente!
            val nuevoDoc = empleadosRef.document

            val empleadoConId = empleado.copy(id = nuevoDoc.id)
            nuevoDoc.set(empleadoConId)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}