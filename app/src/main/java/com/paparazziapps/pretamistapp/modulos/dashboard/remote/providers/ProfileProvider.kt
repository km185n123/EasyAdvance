package com.paparazziapps.pretamistapp.modulos.dashboard.remote.providers

import com.paparazziapps.pretamistapp.modulos.dashboard.data.dto.ProfileDTO

interface ProfileProvider {
    suspend fun getProfile(id: String): ProfileDTO
}