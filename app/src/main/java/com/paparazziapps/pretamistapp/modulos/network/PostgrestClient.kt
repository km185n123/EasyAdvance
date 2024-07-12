/*
package com.paparazziapps.pretamistapp.modulos.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class PostgrestClient(private val baseUrl: String, private val apiKey: String) {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            })
        }
    }

    suspend fun from(tableName: String): PostgrestQueryBuilder {
        return PostgrestQueryBuilder(client, baseUrl, apiKey, tableName)
    }
}

class PostgrestQueryBuilder(
    private val client: HttpClient,
    private val baseUrl: String,
    private val apiKey: String,
    private val tableName: String
) {

    private var query = StringBuilder("$baseUrl/rest/v1/$tableName")

    fun select(): PostgrestQueryBuilder {
        query.append("?select=*")
        return this
    }

    fun insert(data: String): PostgrestQueryBuilder {
        query.append("?")
        return this
    }

    fun eq(column: String, value: Any): PostgrestQueryBuilder {
        query.append("&$column=eq.$value")
        return this
    }

    suspend fun execute(): HttpResponse {
        val response: HttpResponse = client.request(query.toString()) {
            method = if (query.contains("insert")) HttpMethod.Post else HttpMethod.Get
            headers {
                append("apikey", apiKey)
                append("Authorization", "Bearer $apiKey")
                append("Content-Type", ContentType.Application.Json.toString())
            }
            if (query.contains("insert")) {
                setBody(query.toString())
            }
        }
        return response
    }
}
*/
