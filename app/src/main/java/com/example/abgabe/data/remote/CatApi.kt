package com.example.abgabe.data.remote

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.client.request.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

val client = HttpClient(CIO)
val apiKey = "live_RhoeIS1CZSQxrEYGaUiRbmNCGUsUvBrqJsml10ApSBodOteF8DtYzIyE0kthZ6jM"

suspend fun getOneCat(): String {
    val url = "https://api.thecatapi.com/v1/images/search?limit=1&has_breeds=1&api_key=$apiKey"
    return client.get(url).bodyAsText()
}

suspend fun getTenCats(): String {
    val url = "https://api.thecatapi.com/v1/images/search?limit=10&has_breeds=1&api_key=$apiKey"
    return client.get(url).bodyAsText()
}