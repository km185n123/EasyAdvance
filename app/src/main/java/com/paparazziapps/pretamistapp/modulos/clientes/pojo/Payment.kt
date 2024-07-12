package com.paparazziapps.pretamistapp.modulos.clientes.pojo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Payment(
    @SerialName("payment_id")
    var paymentId: String? = null,
    @SerialName("payment_date")
    var paymentDate: String? = null,
    @SerialName("amount_paid")
    var amountPaid: String? = null,
    @SerialName("payment_status")
    var paymentStatus: String? = null,
    @SerialName("responsible_id")
    var responsibleId: String? = null,
    @SerialName("payment_type")
    var payment_type: String? = null,
    @SerialName("description")
    var description: String? = null,
    @SerialName("novelty_of_non_payment")
    var noveltyOfNonPayment: String? = null
)
