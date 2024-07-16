package com.paparazziapps.pretamistapp.modulos.dashboard.remote.repository

import com.paparazziapps.pretamistapp.modulos.dashboard.data.dto.BranchAgentDTO
import com.paparazziapps.pretamistapp.modulos.dashboard.remote.providers.BranchAgentProvider
import javax.inject.Inject


class BranchAgentRepositoryImpl @Inject constructor(
    private val branchAgentProvider: BranchAgentProvider,
) : BranchAgentRepository {

    override suspend fun getBranchAgent(id: String): BranchAgentDTO {
        return branchAgentProvider.getBranchAgent(id)
    }
}