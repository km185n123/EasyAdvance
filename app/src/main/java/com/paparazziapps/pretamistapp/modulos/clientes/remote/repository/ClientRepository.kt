package com.paparazziapps.pretamistapp.modulos.clientes.remote.repository

import com.paparazziapps.pretamistapp.helper.ResultData
import com.paparazziapps.pretamistapp.modulos.clientes.data.dto.ClientDTO
import com.paparazziapps.pretamistapp.modulos.clientes.data.pojo.Client

interface ClientRepository {
    suspend fun getClient(id: String): ResultData<ClientDTO?>
    suspend fun createClient(client: Client): ResultData<String?>
}