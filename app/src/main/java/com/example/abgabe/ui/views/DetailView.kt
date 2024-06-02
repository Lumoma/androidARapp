package com.example.abgabe.ui.views

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import coil.compose.AsyncImage
import com.example.abgabe.data.local.AppDatabase
import com.example.abgabe.data.local.Cat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class DetailScreen: ViewModel(){

    @SuppressLint("NotConstructor")
    @Composable
    fun DetailScreen(id: String?, db: AppDatabase) {
        val coroutineScope = rememberCoroutineScope()
        var cat by remember { mutableStateOf<Cat?>(null) }

        LaunchedEffect(key1 = Unit) {
            coroutineScope.launch(Dispatchers.IO) {
                val uuidString: String? = id
                if (uuidString != null) {
                    val uuid: UUID? = UUID.fromString(uuidString)
                    uuid?.let { cat = db.catDao().getById(it) }
                }
            }
        }
        cat?.let {
            CatData(cat = it)
        }
    }

    @Composable
    fun CatData(cat: Cat) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("ID: ${cat.id}")
            Text("Name: ${cat.name}")
            Text("Breed: ${cat.breed}")
            Text("Temperament: ${cat.temperament}")
            Text("Origin: ${cat.origin}")
            Text("Life Expectancy: ${cat.lifeExpectancy}")
            Text("Picture URL: ${cat.imageUrl}")
            Text("QR Code ID: ${cat.qrCodeID}")
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                AsyncImage(
                    model = cat.imageUrl,
                    contentDescription = "Image from URL",
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
