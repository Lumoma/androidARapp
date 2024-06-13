package com.example.abgabe.data.util

import com.example.abgabe.data.local.Cat
import com.example.abgabe.data.remote.CatApiData
import java.util.UUID
import javax.inject.Inject

class CatConverter @Inject constructor(private val qrCodeHelper: QrCodeHelper) {

    suspend fun convertToCat(catApiData: CatApiData): Cat {

        //Generate all missing data and return it as Cat object
        val uuid = UUID.randomUUID()
        val name = getRandomCatName()
        val breed = catApiData.breeds.first()
        return Cat(
            id = uuid,
            name = name,
            breed = breed.name,
            temperament = breed.temperament,
            origin = breed.origin,
            lifeExpectancy = breed.life_span,
            imageUrl = catApiData.url,
            qrCodeByteArray = qrCodeHelper.generateQRCodeByteCodeFromUUID(uuid),
            qrCodePath = qrCodeHelper.generateQRCodeFromUUID(name, uuid)
        )
    }

    private fun getRandomCatName(): String {
        val catNames = listOf(
            "Whiskers",
            "Tiger",
            "Felix",
            "Luna",
            "Simba",
            "Mittens",
            "Oreo",
            "Bella",
            "Chloe",
            "Lucy",
            "Shadow",
            "Angel",
            "Molly",
            "Smokey",
            "Jasper",
            "Charlie",
            "Sophie",
            "Loki",
            "Zoe",
            "Cleo",
            "Pumpkin",
            "Milo",
            "Sasha",
            "Boots",
            "Peanut",
            "Misty",
            "Maggie",
            "Princess",
            "Sammy",
            "Oscar",
            "Salem",
            "Midnight",
            "Max",
            "Coco",
            "Rocky",
            "Missy",
            "Kitty",
            "Gizmo",
            "Bandit",
            "Muffin",
            "Pepper",
            "Snickers",
            "Socks",
            "Lucky",
            "Mimi",
            "Daisy",
            "Patches",
            "Oliver"
        )
        return catNames.random()
    }
}