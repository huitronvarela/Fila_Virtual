package com.example.fila_virtual

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.* // <-- Importante para el estado visual
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    private val firebaseAuth = FirebaseAuth.getInstance()

    // 1. Creamos un interruptor para avisar que el login fue un éxito
    private var isLoginSuccess by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("385041919843-v9f3p7kntedtho0612ivkvcdgjsse0jh.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            App(
                onGoogleSignIn = { startGoogleSignIn() },
                onSignOut = {
                    signOut()
                    isLoginSuccess = false // Apagamos el interruptor al salir
                },
                googleLoginSuccess = isLoginSuccess // 2. Le pasamos el aviso a la App
            )
        }
    }

    private fun startGoogleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut() {
        firebaseAuth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            }  catch (e: ApiException) {
                android.util.Log.e("GOOGLE_DEBUG", "Error Code: ${e.statusCode}")
                android.util.Log.e("GOOGLE_DEBUG", "Causa: ${android.util.Log.getStackTraceString(e)}")
                Toast.makeText(this, "ERROR GOOGLE: ${e.statusCode}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user?.let {
                        saveUserToFirestore(it.uid, it.displayName, it.email, it.photoUrl?.toString())
                    }
                    Toast.makeText(this, "Sesión iniciada con Google", Toast.LENGTH_SHORT).show()

                    isLoginSuccess = true // 3. ¡Activamos el interruptor para abrir la puerta!
                } else {
                    Toast.makeText(this, "Error en Firebase: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserToFirestore(uid: String, name: String?, email: String?, photoUrl: String?) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("usuarios").document(uid)

        userRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                val userData = hashMapOf(
                    "nombre" to (name ?: "Usuario Google"),
                    "email" to (email ?: ""),
                    "telefono" to "",
                    "tipoUsuario" to "ALUMNO",
                    "billetera" to "",
                    "fechaRegistro" to "02 de marzo de 2026",
                    "fotoUrl" to photoUrl
                )
                userRef.set(userData)
            } else {
                if (photoUrl != null) {
                    userRef.update("fotoUrl", photoUrl)
                }
            }
        }
    }
}