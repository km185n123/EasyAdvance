package com.paparazziapps.pretamistapp.modulos.dashboard.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDTO(
    @SerialName("id") val id: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("name") val name: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("active_user") val activeUser: Boolean,
    @SerialName("id_rol") val idRol: Long,
    @SerialName("location") val location: String,
    @SerialName("location_status") val locationStatus: Boolean
)
