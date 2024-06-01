package com.example.abgabe.data.remote

//CatApi.kt: Ein Interface, das die Endpunkte der API definiert. Es verwendet Retrofit-Annotationen, um die HTTP-Methoden (GET, POST usw.) und die Endpunkt-Pfade anzugeben.

import com.example.abgabe.data.local.AppDatabase
import com.example.abgabe.data.local.CatApiData
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

val client = HttpClient(CIO)

suspend fun getCat(): String {
    val url = "https://api.thecatapi.com/v1/images/search"
    return client.get(url).bodyAsText()

}

suspend fun insertCatFromApiToDb(roomDatabase: AppDatabase) {
    // 1. Rufen Sie die API auf und erhalten Sie die Antwort.
    val catJson = getCat()

    // 2. Konvertieren Sie die Antwort in ein `Cat`-Objekt.
    // Angenommen, die API-Antwort hat das gleiche Format wie die `Cat`-Klasse.
    val catApiResponse = Json.decodeFromString<CatApiData>(catJson)

    // 3. Fügen Sie das `Cat`-Objekt in die Datenbank ein.
    val catDao = roomDatabase.catDao()
    catDao.insert(catApiResponse)
}


