package com.paparazziapps.pretamistapp.modulos.mesage

import RetrofitClient
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paparazziapps.pretamistapp.modulos.network.ChatwootService
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ChatwootViewModel(private val context: Context) : ViewModel() {
    private val retrofit = RetrofitClient.client
    private val chatwootService = retrofit.create(ChatwootService::class.java)

    private val _messageSent = MutableLiveData<Boolean>()
    val messageSent: LiveData<Boolean> = _messageSent

    fun sendMessage(phoneNumber: String, message: String) {
        viewModelScope.launch {
            try {
                val request = SendMessageRequest(
                    conversation = Conversation(meta = Meta(sender = Sender(phoneNumber))),
                    content = message
                )
                val response = chatwootService.sendMessage(request)



               /* if (response.isEmpty()) {
                    _messageSent.value = true
                } else {
                    _messageSent.value = false
                   // Log.e("ChatwootViewModel", "Failed to send message: ${response.message}")
                }*/
            } catch (e: HttpException) {
                _messageSent.value = false
                Log.e("ChatwootViewModel", "Error sending message", e)
                // Manejar errores HTTP (404, 500, etc.)
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("SendMessage", "Error HTTP ${e.code()}: $errorBody")
               // Response.error(e.response()?.code(), e.response()?.errorBody())
            } catch (e: IOException) {
                // Manejar errores de red (conexión perdida, timeout, etc.)
                Log.e("SendMessage", "Error de red: ${e.message}")
              //  Response.error(null, null)
            } catch (e: Exception) {
                // Manejar otros errores
                Log.e("SendMessage", "Error inesperado: ${e.message}")
               // Response.error(null, null)
            }

        }
    }

    fun sendMessageWithAttachment(phoneNumber: String, message: String, attachmentUrl: String) {
        viewModelScope.launch {
            try {
                val request = SendMessageRequest(
                    conversation = Conversation(meta = Meta(sender = Sender(phoneNumber))),
                    content = message,
                    attachments = listOf(
                        Attachment(
                            data_url = attachmentUrl,
                            file_name = "file.pdf",
                            file_type = "application/pdf"
                        )
                    ),
                    metadata = Metadata(
                        actions = listOf(
                            Action(type = "reply", text = "Sí", payload = "confirmar_pago"),
                            Action(type = "reply", text = "No", payload = "cancelar_pago")
                        )
                    )
                )

                val response = chatwootService.sendMessage(request)
                if (response.isSuccessful) {
                    // Handle success
                } else {
                    // Handle failure
                }
            } catch (e: Exception) {
                // Handle exception
                e.printStackTrace()
            }
        }
    }
}
