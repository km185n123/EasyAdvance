package com.paparazziapps.pretamistapp.modulos.login.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paparazziapps.pretamistapp.helper.ResultData
import com.paparazziapps.pretamistapp.modulos.login.data.repository.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.gotrue.user.UserSession
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val _userSession = MutableLiveData<UserSession?>()
    val userSession: LiveData<UserSession?> get() = _userSession

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading


    fun onSignIn(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                when(val result = authenticationRepository.signIn(email, password)){
                    is ResultData.Success -> {
                        _userSession.value =  result.value
                    }
                    is ResultData.Failure -> {
                        _message.value = result.throwable.message
                    }
                    is ResultData.Loading -> {
                        _isLoading.value = result.isLoading
                    }
                }
            } catch (e: Exception) {
                _message.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onGoogleSignIn() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                when(val result = authenticationRepository.signInWithGoogle()){
                    is ResultData.Success -> {
                        _userSession.value =  userSession.value
                    }
                    is ResultData.Failure -> {
                        _message.value = result.throwable.message
                    }
                    is ResultData.Loading -> {
                        _isLoading.value = result.isLoading
                    }
                }
            } catch (e: Exception) {
                _message.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        // Implement logout functionality as needed
    }
}
