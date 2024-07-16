package com.paparazziapps.pretamistapp.modulos.clientes.remote.repository

import com.paparazziapps.pretamistapp.helper.ResultData
import com.paparazziapps.pretamistapp.modulos.clientes.data.dto.ClientDTO
import com.paparazziapps.pretamistapp.modulos.clientes.data.pojo.Client
import com.paparazziapps.pretamistapp.modulos.clientes.remote.providers.supa.ClientProvider
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClientRepositoryImpl @Inject constructor(
    private val clientProvider: ClientProvider,
) : ClientRepository {

    override suspend fun getClient(id: String): ResultData<ClientDTO?> {
        return clientProvider.getClient(id)
    }

    override suspend fun createClient(client: Client): ResultData<String?> {
        return clientProvider.createClient(client)
    }
}