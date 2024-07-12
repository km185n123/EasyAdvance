package com.paparazziapps.pretamistapp.modulos.mesage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paparazziapps.pretamistapp.modulos.network.RetrofitClient
import kotlinx.coroutines.launch

class WhatsappViewModel : ViewModel() {
    private val _messageSent = MutableLiveData<Boolean>()
    val messageSent: LiveData<Boolean> = _messageSent

    fun sendMessage(phoneNumber: String, message: String) {
        viewModelScope.launch {
            try {
                val request = SendMessageRequest(
                    conversation = Conversation(meta = Meta(sender = Sender(phoneNumber))),
                    content = message
                )
                val response = RetrofitClient.chatwootService.sendMessage(request)
                if (response.success) {
                    _messageSent.value = true
                } else {
                    _messageSent.value = false
                    Log.e("ChatwootViewModel", "Failed to send message: ${response.message}")
                }
            } catch (e: Exception) {
                _messageSent.value = false
                Log.e("ChatwootViewModel", "Error sending message", e)
            }
        }
    }
}