package com.paparazziapps.pretamistapp.modulos.dashboard.data.dto

import kotlinx.serialization.SerialName

data class ClientDTO(
    @SerialName("id")
    val id: Long,
    @SerialName("created_at")
    val createdAt: String, // Ajustar según el formato de fecha
    @SerialName("name")
    val name: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("dni")
    val dni: String,
    @SerialName("phone")
    val phone: String,
    @SerialName("phone2")
    val phone2: String,
    @SerialName("age")
    val age: Int,
    @SerialName("address")
    val address: String,
    @SerialName("address2")
    val address2: String,
    @SerialName("coordinate")
    val coordinate: String,
    @SerialName("occupation")
    val occupation: String,
    @SerialName("bussiness_name")
    val businessName: String,
    @SerialName("score")
    val score: Int,
    @SerialName("branch_id")
    val branchId: Long
)
