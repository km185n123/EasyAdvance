package com.paparazziapps.pretamistapp.modulos.dashboard.repository

import com.paparazziapps.pretamistapp.modulos.dashboard.data.dto.ProfileDTO

interface ProfileRepository {
    suspend fun getProfile(id: String): ProfileDTO
}