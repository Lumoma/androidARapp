package com.example.abgabe.data.remote

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

val client = HttpClient(CIO)

suspend fun getOneCat(): String {
    val url = "https://api.thecatapi.com/v1/images/search"
    val response = client.get(url).bodyAsText()
    return if (response.startsWith("[") && response.endsWith("]")) {
        response.substring(1, response.length - 1)
    } else {
        response
    }
}


suspend fun getTenCats(): List<CatApiData> {
    val url = "https://api.thecatapi.com/v1/images/search?limit=10"
    val response = client.get(url)

    if (response.status.isSuccess()) {
        val json = Json { ignoreUnknownKeys = true }
        val catApiStringWithBrackets = "[$response]"
        val catApiData = Json.decodeFromString<List<CatApiData>>(catApiStringWithBrackets)
        return json.decodeFromString<List<CatApiData>>(catApiData.toString())
    } else {
        throw Exception("Error fetching cats: ${response.status}")
    }
}
