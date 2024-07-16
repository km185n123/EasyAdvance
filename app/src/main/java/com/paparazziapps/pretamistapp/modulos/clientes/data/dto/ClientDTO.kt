package com.paparazziapps.pretamistapp.modulos.clientes.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientDTO(
    @SerialName("id")
    val id: Long? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("last_name")
    val lastName: String? = null,
    @SerialName("dni")
    val dni: String? = null,
    @SerialName("phone")
    val phone: String? = null,
    @SerialName("phone2")
    val phone2: String? = null,
    @SerialName("age")
    val age: Int? = null,
    @SerialName("address")
    val address: String? = null,
    @SerialName("address2")
    val address2: String? = null,
    @SerialName("coordinate")
    val coordinate: String? = null,
    @SerialName("occupation")
    val occupation: String? = null,
    @SerialName("bussiness_name")
    val businessName: String? = null,
    @SerialName("score")
    val score: Int? = null,
    @SerialName("branch_id")
    val branchId: Long? = null,
)
