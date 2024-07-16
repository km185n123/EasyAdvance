package com.paparazziapps.pretamistapp.modulos.clientes.remote.providers.supa

import com.paparazziapps.pretamistapp.helper.ResultData
import com.paparazziapps.pretamistapp.modulos.clientes.data.dto.ClientDTO
import com.paparazziapps.pretamistapp.modulos.clientes.data.pojo.Client

interface ClientProvider {
    suspend fun getClient(id: String): ResultData<ClientDTO?>
    suspend fun createClient(client: Client): ResultData<String?>
}