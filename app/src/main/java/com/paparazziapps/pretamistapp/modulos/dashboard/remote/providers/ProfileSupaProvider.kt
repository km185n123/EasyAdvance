package com.paparazziapps.pretamistapp.modulos.dashboard.remote.providers

import com.paparazziapps.pretamistapp.helper.ResultData
import com.paparazziapps.pretamistapp.modulos.dashboard.data.dto.ProfileDTO
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileSupaProvider @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage
) : ProfileProvider {

    override suspend fun getProfile(id: String): ResultData<ProfileDTO?> {
        return try {
            withContext(Dispatchers.IO) {
                val data = postgrest.from("branch").select(Columns.raw("id, team_name, profiles(id, name)"));


                val postgrestResult = postgrest.from("profiles").select {
                    filter {
                        eq("id", id)

                    }
                }.decodeSingle<ProfileDTO>()
                ResultData.Success(postgrestResult)
            }

        } catch (e: Exception) {
            ResultData.Failure(e)
        }
    }
}
