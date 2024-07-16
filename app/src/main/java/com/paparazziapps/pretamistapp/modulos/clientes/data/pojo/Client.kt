package com.paparazziapps.pretamistapp.modulos.clientes.data.pojo

data class Client(
    val id: Long? = null,
    var createdAt: String? = null,
    var name: String? = null,
    var lastName: String? = null,
    var dni: String? = null,
    var phone: String? = null,
    var phone2: String? = null,
    var age: Int?? = null,
    var address: String? = null,
    var address2: String? = null,
    var coordinate: String? = null,
    var occupation: String? = null,
    var businessName: String? = null,
    var score: Int? = null,
    var branchId: Long? = null
)
