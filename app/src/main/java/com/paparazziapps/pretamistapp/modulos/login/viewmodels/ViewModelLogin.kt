package com.paparazziapps.pretamistapp.modulos.login.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.paparazziapps.pretamistapp.modulos.login.providers.LoginProvider

class ViewModelLogin(private val loginProvider: LoginProvider) : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val _isLoginEmail = MutableLiveData<Boolean>()
    val isLoginEmail: LiveData<Boolean> get() = _isLoginEmail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun loginWithEmail(email: String?, pass: String?) {
        _isLoading.value = true
        try {
            loginProvider.loginEmail(email ?: "", pass ?: "").addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _message.value = "Bienvenido"
                    _isLoginEmail.value = true
                    _isLoading.value = false
                } else {
                    _message.value = "Usuario y/o contraseña incorrectos"
                    _isLoginEmail.value = false
                    _isLoading.value = false
                }
            }
        } catch (e: Exception) {
            _message.value = e.message
        }
    }

    fun logout() {
        loginProvider.signout()
    }
}
