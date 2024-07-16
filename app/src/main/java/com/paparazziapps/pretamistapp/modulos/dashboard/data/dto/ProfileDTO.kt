package com.paparazziapps.pretamistapp.modulos.dashboard.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDTO(
    @SerialName("id") val id: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("last_name") val lastName: String,
    @SerialName("active_user") val activeUser: Boolean,
    @SerialName("id_rol") val idRol: Long? = null,
    @SerialName("location") val location: String? = null,
    @SerialName("location_status") val locationStatus: Boolean? = null,
)
