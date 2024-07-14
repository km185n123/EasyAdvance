package com.paparazziapps.pretamistapp.modulos.dashboard.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paparazziapps.pretamistapp.modulos.dashboard.data.dto.BranchAgentDTO
import com.paparazziapps.pretamistapp.modulos.dashboard.repository.BranchAgentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BranchAgentViewModel @Inject constructor(
    private val branchAgentRepository: BranchAgentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _branchAgent = MutableStateFlow<BranchAgentDTO?>(null)
    val branchAgent: StateFlow<BranchAgentDTO?> = _branchAgent

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    init {
        val branchAgentId = savedStateHandle.get<String>("branchAgentId")
        branchAgentId?.let {
            getBranchAgent(branchAgentId = it)
        }
    }

    private fun getBranchAgent(branchAgentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = branchAgentRepository.getBranchAgent(branchAgentId)
                _branchAgent.emit(result)
            } catch (e: Exception) {
                _message.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
