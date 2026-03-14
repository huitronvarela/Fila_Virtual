package com.example.fila_virtual.repository

import com.example.fila_virtual.data.Usuario
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.firestore.firestore

class UserRepository {
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    fun getCurrentUserUid(): String? = auth.currentUser?.uid
    
    // Función nueva para obtener los datos de la sesión activa
    fun getFirebaseUser(): FirebaseUser? = auth.currentUser

    suspend fun getUserData(uid: String): Usuario? {
        return try {
            val document = db.collection("usuarios").document(uid).get()
            if (document.exists) document.data<Usuario>() else null
        } catch (e: Exception) {
            println("🔥 Repo Error: ${e.message}")
            null
        }
    }

    suspend fun signOut() {
        auth.signOut()
    }
}
