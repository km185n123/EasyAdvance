package com.paparazziapps.pretamistapp.modulos.location

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

class MyViewModelStoreOwner : ViewModelStoreOwner {
    private val viewModelStore = ViewModelStore()

    override fun getViewModelStore(): ViewModelStore {
        return viewModelStore
    }
}