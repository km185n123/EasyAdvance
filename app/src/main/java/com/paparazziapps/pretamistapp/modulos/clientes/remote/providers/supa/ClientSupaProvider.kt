package com.paparazziapps.pretamistapp.modulos.clientes.remote.providers.supa

import com.paparazziapps.pretamistapp.helper.ResultData
import com.paparazziapps.pretamistapp.modulos.clientes.data.dto.ClientDTO
import com.paparazziapps.pretamistapp.modulos.clientes.data.pojo.Client
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.util.Identity.decode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class ClientSupaProvider @Inject constructor(
    private val postgrest: Postgrest
) : ClientProvider {

    override suspend fun getClient(id: String): ResultData<ClientDTO?> {
         return try {
             withContext(Dispatchers.IO) {
               val postgrestResult = postgrest.from("client").select {
                    filter {
                        eq("id", id)
                    }
                }.decodeSingle<ClientDTO>()
                 ResultData.Success(postgrestResult)
            }

        } catch (e: Exception) {
            ResultData.Failure(e)
        }
    }

    override suspend fun createClient(client: Client): ResultData<String?> {
        return try {
            withContext(Dispatchers.IO) {
                val clientDTO = ClientDTO(
                    createdAt = client.createdAt,
                    name = client.name,
                    lastName = client.lastName,
                    dni = client.dni,
                    phone = client.phone,
                    phone2 = client.phone2,
                    age = client.age,
                    address = client.address,
                    address2 = client.address2,
                    coordinate = client.coordinate,
                    occupation = client.occupation,
                    businessName = client.businessName,
                    score = client.score,
                    branchId = client.branchId
                )
                postgrest.from("client").insert(clientDTO)
                ResultData.Success("ok")
            }
        } catch (e: Exception) {
            ResultData.Failure(e)
        }
    }
}
