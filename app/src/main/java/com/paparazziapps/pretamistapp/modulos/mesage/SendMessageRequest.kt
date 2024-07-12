package com.paparazziapps.pretamistapp.modulos.mesage

import com.google.gson.annotations.SerializedName



data class SendMessageRequest(
    val event: String = "message_created",
    val private: Boolean = false,
    val message_type: String = "outgoing",
    val conversation: Conversation? = null,
    val content: String? = null,
    val attachments: List<Attachment>? =  null,
    val metadata: Metadata? = null
)

data class Sender(
    @SerializedName("phone_number")
    val phoneNumber: String
)

data class Attachment(
    val data_url: String,
    val file_name: String,
    val file_type: String
)

data class Conversation(
    val channel: String = "Channel::Api",
    val meta: Meta = Meta()
)

data class Meta(
    val sender: Sender = Sender("")
)

data class Metadata(
    val actions: List<Action>
)

data class Action(
    val type: String,
    val text: String,
    val payload: String
)


data class SendMessageResponse(
    val success: Boolean,
    val message: String
)
