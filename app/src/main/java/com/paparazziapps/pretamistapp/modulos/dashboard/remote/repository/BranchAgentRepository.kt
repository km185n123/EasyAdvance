package com.paparazziapps.pretamistapp.modulos.dashboard.remote.repository

import com.paparazziapps.pretamistapp.modulos.dashboard.data.dto.BranchAgentDTO

interface BranchAgentRepository {
    suspend fun getBranchAgent(id: String): BranchAgentDTO
}