package com.paparazziapps.pretamistapp.modulos.dashboard.remote.providers

import com.paparazziapps.pretamistapp.modulos.dashboard.data.dto.BranchAgentDTO

interface BranchAgentProvider {
    suspend fun getBranchAgent(id: String): BranchAgentDTO
}