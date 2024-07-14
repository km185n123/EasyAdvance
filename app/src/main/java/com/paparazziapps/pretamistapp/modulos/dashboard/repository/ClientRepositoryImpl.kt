package com.paparazziapps.pretamistapp.modulos.dashboard.repository

import com.paparazziapps.pretamistapp.modulos.dashboard.data.dto.ClientDTO
import com.paparazziapps.pretamistapp.modulos.dashboard.remote.providers.ClientProvider
import javax.inject.Inject

class ClientRepositoryImpl @Inject constructor(
    private val clientProvider: ClientProvider,
) : ClientRepository {

    override suspend fun getClient(id: String): ClientDTO {
        return clientProvider.getClient(id)
    }
}