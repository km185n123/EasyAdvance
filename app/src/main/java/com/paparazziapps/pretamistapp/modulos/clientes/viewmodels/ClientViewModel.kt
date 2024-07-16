package com.paparazziapps.pretamistapp.modulos.clientes.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paparazziapps.pretamistapp.helper.ResultData
import com.paparazziapps.pretamistapp.modulos.clientes.data.dto.ClientDTO
import com.paparazziapps.pretamistapp.modulos.clientes.data.pojo.Client
import com.paparazziapps.pretamistapp.modulos.clientes.remote.repository.ClientRepository
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

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val _success = MutableLiveData<String>()
    val success: LiveData<String> get() = _success

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
                when(val result = clientRepository.getClient(clientId)){
                    is ResultData.Success -> {
                        _client.emit(result.value)
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

    fun onCreateClient(
       client: Client
    ) {
        validateClientFields(client)
        viewModelScope.launch {
            _isLoading.value = true
            try {
                when(val result = clientRepository.createClient(client = client)){
                    is ResultData.Success -> {
                        _success.value = result.value
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

    fun validateClientFields(client: Client?): Boolean {
        return if (
            client?.name.isNullOrEmpty() || client?.lastName.isNullOrEmpty() || client?.dni.isNullOrEmpty() || client?.phone.isNullOrEmpty() ||
            client?.phone2.isNullOrEmpty() || client?.address.isNullOrEmpty() || client?.address2.isNullOrEmpty() || client?.coordinate.isNullOrEmpty() ||
            client?.occupation.isNullOrEmpty() || client?.businessName.isNullOrEmpty()
        ) {
            _message.value = "Por favor, complete todos los campos correctamente"
            false
        } else {
            true
        }
    }
}
