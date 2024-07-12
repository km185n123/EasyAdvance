package com.paparazziapps.pretamistapp.modulos.network

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.paparazziapps.pretamistapp.modulos.mesage.ChatwootViewModel

class ChatwootViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatwootViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatwootViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
