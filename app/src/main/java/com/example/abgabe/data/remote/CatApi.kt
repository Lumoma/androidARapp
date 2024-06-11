package com.example.abgabe.data.remote

import android.content.Context
import com.example.abgabe.data.local.Cat
import com.example.abgabe.data.util.CatConverter
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Inject

class CatApi @Inject constructor(private val catConverter: CatConverter) {

    val client = HttpClient(CIO){
        install(ContentNegotiation){
            json(Json { // this: JsonBuilder
                encodeDefaults = true
                ignoreUnknownKeys = true
            })
        }
    }

    val apiKey = "live_RhoeIS1CZSQxrEYGaUiRbmNCGUsUvBrqJsml10ApSBodOteF8DtYzIyE0kthZ6jM"
    val randomPicApiKey = "live_bqJ3cWHZ7TjaUm2rHHhHdHBhCk857LUHRHzThCcj0PhW65tFz5lS3toZwY61V7R6"

    suspend fun getRandomCatPictureUrlFromApi(): String {
        val url = "https://api.thecatapi.com/v1/images/search?limit=1&api_key=$randomPicApiKey"
        return client.get(url).body<List<CatPicData>>().map { it.url }.first() //Serialization is done by Ktor
    }

    suspend fun getCatsApi(amount: Int, context: Context): List<Cat> {
        val url = "https://api.thecatapi.com/v1/images/search?limit=$amount&has_breeds=1&api_key=$apiKey"
        return client.get(url).body<List<CatApiData>>().map { catConverter.convertToCat(it) } //Serialization is done by Ktor
    }
}



