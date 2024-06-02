package com.example.abgabe.data.remote

import androidx.room.PrimaryKey
import com.example.abgabe.data.local.Cat
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

//https://api.thecatapi.com/v1/images/search?limit=10&has_breeds=1&api_key=live_RhoeIS1CZSQxrEYGaUiRbmNCGUsUvBrqJsml10ApSBodOteF8DtYzIyE0kthZ6jM

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
                name = getRandomCatName(),
                breed = getRandomCatBreeds(),
                temperament = getRandomCatTemperament(),
                origin = getRandomCatOrigin(),
                lifeExpectancy = getRandomeCatLifeExpectancy(),
                imageUrl = it.url
            )
        }
    }

    suspend fun getTenRandomCats(): List<Cat> {
        val catApiList= getTenCats()
        return catApiList.map {
            Cat(
                id = it.id,
                name = getRandomCatName(),
                breed = getRandomCatBreeds(),
                temperament = getRandomCatTemperament(),
                origin = getRandomCatOrigin(),
                lifeExpectancy = getRandomeCatLifeExpectancy(),
                imageUrl = it.url
            )
        }
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

