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
    val response = client.get(url).bodyAsText()
    return Json.decodeFromString(ListSerializer(CatApiData.serializer()), response)
}