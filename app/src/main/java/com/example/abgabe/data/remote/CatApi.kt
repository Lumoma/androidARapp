package com.example.abgabe.data.remote

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

val client = HttpClient(CIO)

val apiKey = "live_RhoeIS1CZSQxrEYGaUiRbmNCGUsUvBrqJsml10ApSBodOteF8DtYzIyE0kthZ6jM"
//val alternativeApiKey = "live_bqJ3cWHZ7TjaUm2rHHhHdHBhCk857LUHRHzThCcj0PhW65tFz5lS3toZwY61V7R6"

suspend fun getOneCat(): String {
    val url = "https://api.thecatapi.com/v1/images/search?limit=1&has_breeds=1&api_key=$apiKey"
    return client.get(url).bodyAsText()
}

suspend fun getCats(amount: Int): String {
    val url = "https://api.thecatapi.com/v1/images/search?limit=$amount&has_breeds=1&api_key=$apiKey"
    return client.get(url).bodyAsText()
}