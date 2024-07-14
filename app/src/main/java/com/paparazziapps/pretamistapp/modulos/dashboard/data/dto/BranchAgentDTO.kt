package com.paparazziapps.pretamistapp.modulos.dashboard.data.dto

import kotlinx.serialization.SerialName


data class BranchAgentDTO(
    @SerialName("id")
    val id: Long,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("id_branch")
    val idBranch: Long,
    @SerialName("id_agent")
    val idAgent: String
)