package com.example.abgabe.viewmodels

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import coil.compose.AsyncImage
import com.example.abgabe.data.local.AppDatabase
import com.example.abgabe.data.local.Cat
import com.example.abgabe.data.remote.CatGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val database: AppDatabase,
) : ViewModel() {
    @Composable
    fun DetailScreen(id: String?) {
        val coroutineScope = rememberCoroutineScope()
        var cat by remember { mutableStateOf<Cat?>(null) }

        LaunchedEffect(key1 = Unit) {
            coroutineScope.launch(Dispatchers.IO) {
                val uuidString: String? = id
                if (uuidString != null) {
                    val uuid: UUID? = UUID.fromString(uuidString)
                    uuid?.let { cat = database.catDao().getById(it) }
                }
            }
        }
        cat?.let {
            CatData(cat = it)
        }
    }

    @Composable
    fun CatData(cat: Cat) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    AsyncImage(
                        model = cat.imageUrl,
                        contentDescription = "Image from URL: ${cat.imageUrl}",
                        modifier = Modifier
                            .height(300.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            item { Text("Name: ${cat.name}") }
            item { Text("Breed: ${cat.breed}") }
            item { Text("Temperament: ${cat.temperament}") }
            item { Text("Origin: ${cat.origin}") }
            item { Text("Life Expectancy: ${cat.lifeExpectancy}") }
            item { DisplayQRCode(qrCodeImage = cat.qrCodeImage) }
        }
    }

    @Composable
    fun DisplayQRCode(qrCodeImage: ByteArray) {
        val bitmap = BitmapFactory.decodeByteArray(qrCodeImage, 0, qrCodeImage.size)
        val imageBitmap = bitmap.asImageBitmap()

        Image(
            bitmap = imageBitmap,
            contentDescription = "QR Code",
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
    }
}
