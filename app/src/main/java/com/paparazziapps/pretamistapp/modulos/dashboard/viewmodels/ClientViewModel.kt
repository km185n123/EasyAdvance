package com.paparazziapps.pretamistapp.modulos.dashboard.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paparazziapps.pretamistapp.modulos.dashboard.data.dto.ClientDTO
import com.paparazziapps.pretamistapp.modulos.dashboard.repository.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _client = MutableStateFlow<ClientDTO?>(null)
    val client: StateFlow<ClientDTO?> = _client

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    init {
        val clientId = savedStateHandle.get<String>("clientId")
        clientId?.let {
            getClient(clientId = it)
        }
    }

    private fun getClient(clientId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = clientRepository.getClient(clientId)
                _client.emit(result)
            } catch (e: Exception) {
                _message.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
