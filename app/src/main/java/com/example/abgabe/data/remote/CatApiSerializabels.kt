package com.example.abgabe.data.remote

import kotlinx.serialization.Serializable

//https://api.thecatapi.com/v1/images/search?limit=1&has_breeds=1&api_key=live_RhoeIS1CZSQxrEYGaUiRbmNCGUsUvBrqJsml10ApSBodOteF8DtYzIyE0kthZ6jM

@Serializable
data class CatPicData(
    val url: String
)

@Serializable
data class CatApiData(
    val width: Int,
    val height: Int,
    val breeds: List<Breed>,
    val url: String
)

@Serializable
data class Breed(
    val weight: Weight,
    val id: String,
    val name: String,
    val temperament: String,
    val origin: String,
    val description: String,
    val life_span: String,
)

@Serializable
data class Weight(
    val imperial: String,
    val metric: String
)
