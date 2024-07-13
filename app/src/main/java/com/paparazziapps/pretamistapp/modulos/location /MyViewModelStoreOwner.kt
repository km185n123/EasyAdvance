package com.paparazziapps.pretamistapp.modulos.location

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

class MyViewModelStoreOwner : ViewModelStoreOwner {
    override val viewModelStore = ViewModelStore()
    
}