package com.paparazziapps.pretamistapp.modulos.dashboard.remote.repository

import com.paparazziapps.pretamistapp.helper.ResultData
import com.paparazziapps.pretamistapp.modulos.dashboard.data.dto.ProfileDTO
import com.paparazziapps.pretamistapp.modulos.dashboard.remote.providers.ProfileProvider
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileProvider: ProfileProvider,
) : ProfileRepository {

    override suspend fun getProfile(id: String): ResultData<ProfileDTO?> {
        return profileProvider.getProfile(id)
    }
}