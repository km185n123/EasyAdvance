package com.paparazziapps.pretamistapp.modulos.dashboard.repository

import com.paparazziapps.pretamistapp.modulos.dashboard.data.dto.ClientDTO

interface ClientRepository {
    suspend fun getClient(id: String): ClientDTO
}