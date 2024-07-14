package com.paparazziapps.pretamistapp.modulos.dashboard.repository

import com.paparazziapps.pretamistapp.modulos.dashboard.data.dto.ProfileDTO
import com.paparazziapps.pretamistapp.modulos.dashboard.remote.providers.ProfileProvider
import com.paparazziapps.pretamistapp.modulos.dashboard.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileProvider: ProfileProvider,
) : ProfileRepository {

    override suspend fun getProfile(id: String): ProfileDTO {
        return profileProvider.getProfile(id)
    }
}