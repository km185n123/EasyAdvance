package com.paparazziapps.pretamistapp.modulos.login.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paparazziapps.pretamistapp.helper.ResultData
import com.paparazziapps.pretamistapp.modulos.clientes.data.dto.ClientDTO
import com.paparazziapps.pretamistapp.modulos.dashboard.data.dto.ProfileDTO
import com.paparazziapps.pretamistapp.modulos.dashboard.data.pojo.Profile
import com.paparazziapps.pretamistapp.modulos.dashboard.remote.repository.ProfileRepository
import com.paparazziapps.pretamistapp.modulos.login.data.repository.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.gotrue.user.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _profile = MutableLiveData<ProfileDTO?>()
    val profile: LiveData<ProfileDTO?> = _profile

    private val _userSession = MutableLiveData<UserSession?>()
    val userSession: LiveData<UserSession?> = _userSession

    val combinedLiveData = MediatorLiveData<Pair<UserSession?, ProfileDTO?>>().apply {
        addSource(_userSession) { userSession ->
            this.value = Pair(userSession, _profile.value)
        }
        addSource(_profile) { profile ->
            this.value = Pair(_userSession.value, profile)
        }
    }

    fun signInAndFetchProfile(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val authResult = withContext(Dispatchers.IO) {
                    authenticationRepository.signIn(email, password)
                }

                when (authResult) {
                    is ResultData.Success -> {
                        val userSession = authResult.value
                        val profileId = userSession?.user?.id
                        val profileResult = withContext(Dispatchers.IO) {
                            profileId?.let {
                                profileRepository.getProfile(profileId)
                            }
                        }
                        when (profileResult) {
                            is ResultData.Success -> {
                                val profile = profileResult.value?.asDomainModel()
                                _userSession.value = userSession
                                _profile.value = profile

                            }
                            is ResultData.Failure -> {
                                _message.value = profileResult.throwable.message
                            }
                            is ResultData.Loading -> {
                                _isLoading.value = profileResult.isLoading
                            }

                            else -> {}
                        }
                    }
                    is ResultData.Failure -> {
                        _message.value = authResult.throwable.message
                    }
                    is ResultData.Loading -> {
                        _isLoading.value = authResult.isLoading
                    }
                }
            } catch (e: Exception) {
                _message.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }


    /*    fun onSignIn(email: String, password: String) {
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

        fun getProfile(profileId: String) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    val result = profileRepository.getProfile(profileId).asDomainModel()
                    _profile.emit(result)
                } catch (e: Exception) {
                    _message.value = e.message
                } finally {
                    _isLoading.value = false
                }
            }
        }*/

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

    private fun ProfileDTO.asDomainModel(): ProfileDTO {
        return ProfileDTO(
            id = this.id,
            createdAt = this.createdAt,
            name = this.name,
            lastName = this.lastName,
            activeUser = this.activeUser,
            idRol = this.idRol,
            location = this.location,
            locationStatus = this.locationStatus
        )
    }

    fun logout() {
        // Implement logout functionality as needed
    }
}
