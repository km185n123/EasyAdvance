package com.paparazziapps.pretamistapp.modulos.dashboard.data.pojo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Branch(
    val id: Long,
    val createdAt: String,
    val name: String,
    val totalBox: String,
    val totalInFlow: String
)
