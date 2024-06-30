package com.paparazziapps.pretamistapp.modulos.clientes.pojo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Client (
    @SerialName("id")
    var id:String?=null,
    @SerialName("nombres")
    var nombres:String? = null,
    @SerialName("apellidos")
    var apellidos: String? = null,
    @SerialName("dni")
    var dni:String? = null,
    @SerialName("celular1")
    var celular1:String? = null,
    @SerialName("celular2")
    var celular2:String? = null,
    @SerialName("edad")
    var edad:String? = null,
    @SerialName("direccion")
    var direccion:String? = null,
    @SerialName("direccion2")
    var direccion2:String? = null,
    @SerialName("coordenada")
    var coordenada:String? = null,

    //Sucursal
    @SerialName("sucursalId")
    var sucursalId:Int?=null,
    @SerialName("type")
    var type:Int?=null,
    @SerialName("title")
    var title:String?=null
){
    override fun toString(): String {
        return nombres.toString()
    }
}