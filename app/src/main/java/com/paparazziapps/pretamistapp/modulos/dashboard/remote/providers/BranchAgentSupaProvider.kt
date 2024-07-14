package com.paparazziapps.pretamistapp.modulos.dashboard.remote.providers

import com.paparazziapps.pretamistapp.modulos.dashboard.data.dto.BranchAgentDTO
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
class BranchAgentSupaProvider @Inject constructor(
    private val postgrest: Postgrest
) : BranchAgentProvider {

    override suspend fun getBranchAgent(id: String): BranchAgentDTO {
        return withContext(Dispatchers.IO) {
            postgrest.from("branch_agent").select {
                filter {
                    eq("id", id)
                }
            }.decodeSingle()
        }
    }
}