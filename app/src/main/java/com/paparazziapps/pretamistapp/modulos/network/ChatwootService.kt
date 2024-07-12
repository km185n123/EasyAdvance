package com.paparazziapps.pretamistapp.modulos.mesage

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path


interface ChatwootService {
    @Multipart
    @POST("/conversations/{conversationId}/messages")
    fun createMessage(
        @Header("api_access_token") token: String?,
        @Path("conversationId") conversationId: String?,
        @Part("content") content: RequestBody?,
        @Part("message_type") messageType: RequestBody?,
        @Part("private") isPrivate: RequestBody?,
        @Part attachment: Part?
    ): Call<ResponseBody?>?
}