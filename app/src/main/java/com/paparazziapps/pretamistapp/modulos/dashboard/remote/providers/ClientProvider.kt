package com.paparazziapps.pretamistapp.modulos.dashboard.remote.providers

import com.paparazziapps.pretamistapp.modulos.dashboard.data.dto.ClientDTO

interface ClientProvider {
    suspend fun getClient(id: String): ClientDTO
}