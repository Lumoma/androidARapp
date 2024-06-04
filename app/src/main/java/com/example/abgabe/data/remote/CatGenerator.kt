package com.example.abgabe.data.remote

import android.graphics.Bitmap
import android.graphics.Color
import com.example.abgabe.data.local.Cat
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.util.UUID

//https://api.thecatapi.com/v1/images/search?limit=1&has_breeds=1&api_key=live_RhoeIS1CZSQxrEYGaUiRbmNCGUsUvBrqJsml10ApSBodOteF8DtYzIyE0kthZ6jM

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

class CatGenerator {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getCatInfo(): List<Cat> {
        val catApiString = getOneCat()
        val catApiDataList = json.decodeFromString<List<CatApiData>>(catApiString)
        return catApiDataList.map { convertToCat(it) }
    }

    suspend fun getTenCatInfos(): List<Cat> {
        val catApiString = getTenCats()
        val catApiDataList = json.decodeFromString<List<CatApiData>>(catApiString)
        return catApiDataList.map { convertToCat(it) }
    }

    suspend fun getCatInfos(amount: Int): List<Cat> {
        val catApiString = getCats(amount)
        val catApiDataList = json.decodeFromString<List<CatApiData>>(catApiString)
        return catApiDataList.map { convertToCat(it) }
    }

    private fun convertToCat(catApiData: CatApiData): Cat {
        val uuid = UUID.randomUUID()
        val breed = catApiData.breeds.firstOrNull()
        return Cat(
            id = uuid,
            name = getRandomCatName(),
            breed = breed?.name?: getRandomCatBreeds(),
            temperament = breed?.temperament?: getRandomCatTemperament(),
            origin = breed?.origin?: getRandomCatOrigin(),
            lifeExpectancy = breed?.life_span?: getRandomeCatLifeExpectancy().toString(),
            imageUrl = catApiData.url,
            qrCodeImage = generateQRCodeFromUUID(uuid)
        )
    }

    private fun generateQRCodeFromUUID(uuid: UUID): ByteArray {
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(uuid.toString(), BarcodeFormat.QR_CODE, 200, 200)

        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }

        val stream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 90, stream)
        return stream.toByteArray()
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

    private fun getRandomCatBreeds(): String {
        val breeds = listOf(
            "Abyssinian",
            "American Bobtail",
            "American Curl",
            "American Shorthair",
            "American Wirehair",
            "Balinese",
            "Bengal",
            "Birman",
            "Bombay",
            "British Shorthair",
            "Burmese",
            "Burmilla",
            "Chartreux",
            "Colorpoint Shorthair",
            "Cornish Rex",
            "Cymric",
            "Devon Rex",
            "Egyptian Mau",
            "European Burmese",
            "Exotic",
            "Havana Brown",
            "Himalayan",
            "Japanese Bobtail",
            "Javanese",
            "Korat",
            "LaPerm",
            "Main")
        return breeds.random()
    }

    private fun getRandomCatTemperament(): String {
        val temperaments = listOf(
            "Active",
            "Agile",
            "Agreeable",
            "Alert",
            "Amiable",
            "Amicable",
            "Amusing",
            "Brave",
            "Bright",
            "Calm",
            "Charming",
            "Cheerful",
            "Clever",
            "Confident",
            "Cooperative",
            "Courageous",
            "Courteous",
            "Curious",
            "Determined",
            "Diligent",
            "Diplomatic",
            "Discreet",
            "Dynamic",
            "Easygoing",
            "Energetic",
            "Enthusiastic",
            "Exuberant",
            "Fair",
            "Faithful",
            "Fearless",
            "Forceful",
            "Forgiving",
            "Frank",
            "Friendly",
            "Funny",
            "Generous",
            "Gentle",
            "Good",
            "Good-natured",
            "Gracious",
            "Hardworking",
            "Helpful",
            "Honest",
            "Honorable",
            "Humorous",
            "Idealistic",
            "Imaginative",
            "Impartial",
            "Independent",
            "Intelligent",
            "Intuitive",
            "Inventive",
            "Kind",
            "Loving",
            "Loyal",
            "Modest",
            "Neat",
            "Nice",
            "Optimistic",
            "Passionate",
            "Patient",
            "Persistent",
            "Pioneering",
            "Philosophical",
            "Placid",
            "Plucky",
            "Polite",
            "Powerful",
            "Practical",
            "Pro-active",
            "Quick-witted",
            "Quiet",
            "Rational",
            "Reliable",
            "Reserved",
            "Resourceful",
            "Romantic",
            "Sensible",
            "Sensitive",
            "Shy",
            "Sincere",
            "Sociable",
            "Straightforward",
            "Sympathetic",
            "Thoughtful",
            "Tidy",
            "Tough",
            "Unassuming",
            "Understanding",
            "Versatile",
            "Warmhearted",
            "Willing",
            "Witty"
        )
        return temperaments.random()
    }

    private fun getRandomCatOrigin(): String {
        val origins = listOf(
            "Germany",
            "France",
            "Italy",
            "Spain",
            "Portugal",
            "Austria",
            "Switzerland",
            "Belgium",
            "Netherlands"
        )
        return origins.random()
    }

    private fun getRandomeCatLifeExpectancy(): Int {
        return (10..25).random()
    }
}

