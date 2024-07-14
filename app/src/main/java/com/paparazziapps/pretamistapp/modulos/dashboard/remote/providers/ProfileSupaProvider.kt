package com.paparazziapps.pretamistapp.modulos.dashboard.remote.providers

import com.paparazziapps.pretamistapp.modulos.dashboard.data.dto.ProfileDTO
import com.paparazziapps.pretamistapp.modulos.dashboard.remote.providers.ProfileProvider
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileSupaProvider @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage
) : ProfileProvider {

    override suspend fun getProfile(id: String): ProfileDTO {
        return withContext(Dispatchers.IO) {
            postgrest.from("branch").select {
                filter {
                    eq("id", id)
                }
            }.decodeSingle<ProfileDTO>()
        }
    }
}
