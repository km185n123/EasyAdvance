package com.paparazziapps.pretamistapp.modulos.dashboard.data.pojo

import kotlinx.serialization.SerialName


data class Client(
    val id: Long,
    val createdAt: String,
    val name: String,
    val lastName: String,
    val dni: String,
    val phone: String,
    val phone2: String,
    val age: Int,
    val address: String,
    val address2: String,
    val coordinate: String,
    val occupation: String,
    val businessName: String,
    val score: Int,
    val branchId: Long
)
