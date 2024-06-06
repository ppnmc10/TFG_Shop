package com.crmv.tfg_shop.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LoginScreenViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val _loading = MutableLiveData(false)

    // Función para hacer login
    fun signInWithEmailAndPassword(email: String, password: String, home: () -> Unit) = viewModelScope.launch {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("LoginScreenViewModel", "signInWithEmailAndPassword: logueado")
                        home()
                    } else {
                        Log.d("LoginScreenViewModel", "signInWithEmailAndPassword: ${task.result.toString()}")
                    }
                }
        } catch (ex: Exception) {
            Log.d("LoginScreenViewModel", "signInWithEmailAndPassword: ${ex.message}")
        }
    }

    // Función para crear una cuenta
    fun createUserWithEmailAndPassword(email: String, password: String, name: String, onResult: (Boolean, String?) -> Unit) {
        if (_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val userId = user?.uid
                        if (userId != null) {
                            val userData = hashMapOf(
                                "name" to name,
                                "email" to email
                            )
                            db.collection("users").document(userId).set(userData)
                                .addOnSuccessListener {
                                    onResult(true, null)
                                }
                                .addOnFailureListener { e ->
                                    onResult(false, e.message)
                                }
                        } else {
                            onResult(false, "User ID is null")
                        }
                    } else {
                        val exception = task.exception
                        val errorMessage = exception?.message ?: "Ha ocurrido un error"
                        onResult(false, errorMessage)
                    }
                    _loading.value = false
                }
        }
    }

    fun resetPassword(email: String, onComplete: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    // Función para añadir producto
    fun addProduct(name: String, description: String, onResult: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        user?.let {
            val productData = hashMapOf(
                "name" to name,
                "description" to description,
                "userId" to user.uid
            )
            db.collection("products").add(productData)
                .addOnSuccessListener {
                    onResult(true, null)
                }
                .addOnFailureListener { e ->
                    onResult(false, e.message)
                }
        } ?: run {
            onResult(false, "No user is currently signed in.")
        }
    }
}
