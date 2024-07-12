/*
package com.paparazziapps.pretamistapp.modulos.clientes.providers

import com.paparazziapps.pretamistapp.modulos.clientes.pojo.Client
import com.paparazziapps.pretamistapp.modulos.network.PostgrestClient
import com.paparazziteam.yakulap.helper.applicacion.MyPreferences
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ClientSuperBaseProvider {

    private val SUPABASE_URL = "https://ohpwhyewdrrolyvhzspm.supabase.co"
    private val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9ocHdoeWV3ZHJyb2x5dmh6c3BtIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjAzODczMTUsImV4cCI6MjAzNTk2MzMxNX0.GautGl-CL-3_eGYdeXTnCMFpmPdWcxGfwxVUqz7lPTU"
    private val client = PostgrestClient(SUPABASE_URL, SUPABASE_KEY)

    var preferences = MyPreferences()

    @Serializable
    data class SupabaseResponse<T>(
        val data: List<T>?,
        val error: String?
    )


    suspend fun create(client: Client, idSucursal: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                client.sucursalId =
                    if (preferences.isSuperAdmin) idSucursal else preferences.sucursalId
                client.id = generateClientId() // Generate a unique ID for the client

                val response = this@ClientSuperBaseProvider.client
                    .from("Clientes")
                    .insert(Json.encodeToString(client))
                    .execute()

                response.status == HttpStatusCode.Created
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    // Get clients by sucursalId
    suspend fun getClients(): List<Client>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = this@ClientSuperBaseProvider.client
                    .from("Clientes")
                    .select()
                    .eq("sucursalId", preferences.sucursalId)
                    .execute()

                if (response.status == HttpStatusCode.OK) {
                    Json.decodeFromString<SupabaseResponse<Client>>(response.bodyAsText()).data
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // Get client by dni
    suspend fun getClient(dni: String): List<Client>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = if (preferences.isSuperAdmin) {
                    this@ClientSuperBaseProvider.client
                        .from("Clientes")
                        .select()
                        .eq("state", "ABIERTO")
                        .execute()
                } else {
                    this@ClientSuperBaseProvider.client
                        .from("Clientes")
                        .select()
                        .eq("dni", dni)
                        .eq("sucursalId", preferences.sucursalId)
                        .execute()
                }

                if (response.status == HttpStatusCode.OK) {
                    Json.decodeFromString<SupabaseResponse<Client>>(response.bodyAsText()).data
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // Generate a unique ID for the client
    private fun generateClientId(): String {
        return java.util.UUID.randomUUID().toString()
    }
}

*/
