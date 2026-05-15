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
    
    fun getFirebaseUser(): FirebaseUser? = auth.currentUser

    suspend fun getUserData(uid: String): Usuario? {
        return try {
            val document = db.collection("usuarios").document(uid).get()
            if (document.exists) {
                val user = document.data<Usuario>()
                user.copy(uid = uid)
            } else {
                null
            }
        } catch (e: Exception) {
            println("🔥 Repo Error: ${e.message}")
            null
        }
    }

    suspend fun updateUserData(uid: String, updates: Map<String, Any?>): Boolean {
        return try {
            db.collection("usuarios").document(uid).update(updates)
            true
        } catch (e: Exception) {
            println("🔥 Update Error: ${e.message}")
            false
        }
    }

    suspend fun signOut() {
        auth.signOut()
    }
}
