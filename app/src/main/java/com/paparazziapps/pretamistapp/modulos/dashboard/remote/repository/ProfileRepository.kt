package com.paparazziapps.pretamistapp.modulos.dashboard.remote.repository

import com.paparazziapps.pretamistapp.helper.ResultData
import com.paparazziapps.pretamistapp.modulos.dashboard.data.dto.ProfileDTO

interface ProfileRepository {
    suspend fun getProfile(id: String): ResultData<ProfileDTO?>
}