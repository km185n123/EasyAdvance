package com.paparazziapps.pretamistapp.modulos.registro.pojo

import com.paparazziapps.pretamistapp.modulos.clientes.pojo.Payment
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Credit (
    @SerialName("id")
    var id:String?=null,
    @SerialName("start_time")
    var startTime:String? = null,
    @SerialName("end_time")
    var endTime:String? = null,
    @SerialName("last_payment_date")
    var lastPaymentDate:String? = null,
    @SerialName("credit_interest_rate")
    var creditInterestRate:String? = null,
    @SerialName("daily_amount_Pay")
    var dailyAmountPay:String? = null,
    @SerialName("amount_total_pay")
    var amountTotalPay:String? = null,
    @SerialName("credit_term")
    var creditTerm:String? = null,
    var creditStatus:String? = null,
    @SerialName("days_paid")
    var daysPaid:String? = null,
    @SerialName("credit_interest")
    var creditInterest:String? = null,
    @SerialName("capital")
    var capital:Long? = null,
    @SerialName("days_remaining_to_pay")
    var daysRemainingToPay:String? = null,
    @SerialName("dni")
    var dni:String? = null,
    @SerialName("responsible_id")
    var responsibleId:String? = null,
    @SerialName("payment_history")
    var paymentHistory: List<Payment>? = emptyList(),

    //Sucursal
    @SerialName("sucursalId")
    var sucursalId:Int?=null,
    @SerialName("type")
    var type:Int?=null,
    @SerialName("title")
    var title:String?=null
    /* @SerialName("id")
     var id:String?=null,
     @SerialName("nombres")
     var nombres:String? = null,
     @SerialName("apellidos")
     var apellidos: String? = null,
     @SerialName("dni")
     var dni:String? = null,
     @SerialName("celular")
     var celular:String? = null,
     @SerialName("fecha")
     var fecha:String? = null,
     @SerialName("unixtime")
     var unixtime:Long?= null,
     @SerialName("unixtimeRegistered")
     var unixtimeRegistered: Long? = null,
     @SerialName("capital")
     var capital:Int? = null,
     @SerialName("interes")
     var interes:Int? = null,
     @SerialName("plazo_vto")
     var plazo_vto:Int?=null,
     @SerialName("coordenada")
     var coordenada:String? = null,
     @SerialName("actual")
     var actual:Boolean? = null,

     //Calcular dias retrasados y
     @SerialName("dias_restantes_por_pagar")
     var dias_restantes_por_pagar:Int?=null,
     @SerialName("fechaUltimoPago")
     var fechaUltimoPago: String? = null,
     @SerialName("diasPagados")
     var diasPagados: Int? = null,
     @SerialName("montoTotalAPagar")
     var montoTotalAPagar:Double?=null,
     @SerialName("montoDiarioAPagar")
     var montoDiarioAPagar:Double?=null,
     @SerialName("state")
     var state:String?= null, //CERRADO,ABIERTO

     //Sucursal
     @SerialName("sucursalId")
     var sucursalId:Int?=null,
     @SerialName("type")
     var type:Int?=null,
     @SerialName("title")
     var title:String?=null*/
)

enum class TypePrestamo(val value: Int) {
    TITLE(0),
    CARD(1)
}