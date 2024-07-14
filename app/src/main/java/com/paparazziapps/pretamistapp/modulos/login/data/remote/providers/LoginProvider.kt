package com.paparazziapps.pretamistapp.modulos.login.data.remote.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginProvider {

    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun getIsLogin(): Boolean = mAuth.currentUser != null

    fun getEmail(): String? = mAuth.currentUser?.email

    fun loginEmail(email: String, pass: String): Task<AuthResult> =
        mAuth.signInWithEmailAndPassword(email, pass)

    fun signout() {
        mAuth.signOut()
    }
}
