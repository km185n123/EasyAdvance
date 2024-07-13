package com.paparazziapps.pretamistapp.factory

import LoanViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.paparazziapps.pretamistapp.modulos.login.providers.LoginProvider
import com.paparazziapps.pretamistapp.modulos.login.viewmodels.ViewModelLogin
import com.paparazziapps.pretamistapp.modulos.registro.providers.PrestamoProvider

class LoginViewModelFactory(private val loginProvider: LoginProvider) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelLogin::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ViewModelLogin(loginProvider) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
