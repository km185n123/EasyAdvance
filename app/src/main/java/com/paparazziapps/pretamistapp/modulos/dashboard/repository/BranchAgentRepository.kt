package com.paparazziapps.pretamistapp.modulos.dashboard.repository

import com.paparazziapps.pretamistapp.modulos.dashboard.data.dto.BranchAgentDTO

interface BranchAgentRepository {
    suspend fun getBranchAgent(id: String): BranchAgentDTO
}