package com.paparazziapps.pretamistapp.modulos.network

import com.paparazziapps.pretamistapp.modulos.mesage.SendMessageRequest
import com.paparazziapps.pretamistapp.modulos.mesage.SendMessageResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatwootService {
    @Headers(
        "Content-Type: application/json",
        "custom-header: headerValue"
    )
    @POST("chatwoot")
    suspend fun sendMessage(@Body request: SendMessageRequest): Response<SendMessageResponse>

}
