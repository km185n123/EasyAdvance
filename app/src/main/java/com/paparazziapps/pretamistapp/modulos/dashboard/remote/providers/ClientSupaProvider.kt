package com.paparazziapps.pretamistapp.modulos.dashboard.remote.providers

import com.paparazziapps.pretamistapp.modulos.dashboard.data.dto.ClientDTO
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClientSupaProvider @Inject constructor(
    private val postgrest: Postgrest
) : ClientProvider {

    override suspend fun getClient(id: String): ClientDTO {
        return withContext(Dispatchers.IO) {
            postgrest.from("client").select {
                filter {
                    eq("id", id)
                }
            }.decodeSingle()
        }
    }
}
