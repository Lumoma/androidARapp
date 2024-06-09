package com.example.abgabe.data.remote

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.provider.MediaStore
import com.example.abgabe.data.local.Cat
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.util.UUID

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
    return client.get(url).body<List<CatPicData>>().map { it.url }.first()
}

suspend fun getCatsApi(amount: Int, context: Context): List<Cat> {
    val url = "https://api.thecatapi.com/v1/images/search?limit=$amount&has_breeds=1&api_key=$apiKey"
    return client.get(url).body<List<CatApiData>>().map { convertToCat(it, context) }
}

private suspend fun convertToCat(catApiData: CatApiData, context: Context): Cat {
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
        qrCodeByteArray = generateQRCodeByteCodeFromUUID(uuid),
        qrCodePath = generateQRCodeFromUUID(name, uuid, context)
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

 suspend fun generateQRCodeFromUUID(name: String, uuid: UUID, context: Context): String = withContext(Dispatchers.IO) {
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

    // Add cat name and UUID on the QR code
    val canvas = Canvas(bmp)
    val paintName = Paint().apply {
        color = Color.RED
        textSize = 14f
    }
    val paintUUID = Paint().apply {
        color = Color.BLUE
        textSize = 8f
    }

    canvas.drawText(name, 10f, 20f, paintName)
     canvas.drawText(uuid.toString(), 20f, 190f, paintUUID)

    //Start Coroutine to save the image to the storage
    var imagePath = ""
    CoroutineScope(Dispatchers.IO).launch {
        imagePath = saveImageToStorage(bmp, context, uuid.toString())
    }

    return@withContext imagePath
}

fun saveImageToStorage(bitmap: Bitmap, context: Context, catId: String): String {
    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "QR_$catId.png")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/CatQRs")
    }

    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    uri?.let {
        val outputStream = resolver.openOutputStream(it)
        if (outputStream != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
        outputStream?.close()
    }

    return uri?.path ?: ""
}

fun generateQRCodeByteCodeFromUUID(uuid: UUID): ByteArray {
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