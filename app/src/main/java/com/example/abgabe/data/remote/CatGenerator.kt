package com.example.abgabe.data.remote

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.room.PrimaryKey
import com.example.abgabe.data.local.Cat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class CatApiData(
    @PrimaryKey
    val id: String,
    val url: String,
    val width: Int,
    val height: Int
)

class CatGenerator {
    suspend fun getOneRandomCat(): Cat {
        val catApiString = getOneCat()
        val catApiData = Json.decodeFromString<CatApiData>(catApiString)
        return catApiData.let {
            Cat(
                id = it.id,
                name = "Cat",
                breed = "Unknown",
                temperament = "Unknown",
                origin = "Unknown",
                lifeExpectancy = 0,
                imageUrl = it.url
            )
        }
    }

    suspend fun getTenRandomCats(): List<Cat> {
        val catApiString = getTenCats()
        val catApiData = Json.decodeFromString<List<CatApiData>>(catApiString.toString())
        return catApiData.map {
            Cat(
                id = it.id,
                name = "Cat",
                breed = "Unknown",
                temperament = "Unknown",
                origin = "Unknown",
                lifeExpectancy = 0,
                imageUrl = it.url
            )
        }
    }
}