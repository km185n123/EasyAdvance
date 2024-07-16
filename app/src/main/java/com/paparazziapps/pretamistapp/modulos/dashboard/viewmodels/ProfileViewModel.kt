package com.paparazziapps.pretamistapp.modulos.dashboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paparazziapps.pretamistapp.modulos.dashboard.data.dto.ProfileDTO
import com.paparazziapps.pretamistapp.modulos.dashboard.remote.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _profile = MutableStateFlow<ProfileDTO?>(null)
    val profile: Flow<ProfileDTO?> = _profile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: Flow<Boolean> get() = _isLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    init {
        val profileId = savedStateHandle.get<String>("profileId")
        profileId?.let {
           // getProfile(profileId = it)
        }
    }

    /*fun getProfile(profileId: String) {
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
}
