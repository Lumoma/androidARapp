package com.example.abgabe.viewmodels

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import coil.compose.rememberImagePainter
import com.example.abgabe.data.remote.CatGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RandomCatImageViewModel @Inject constructor(
    private val catGenerator: CatGenerator
) : ViewModel() {
    @Composable
    fun DisplayCatJson(
        modifier: Modifier = Modifier)
    {
        var  randomCatPictureUrl by remember { mutableStateOf<String?>(null) }
        val coroutineScope = rememberCoroutineScope()
        var updateDatabase by remember { mutableStateOf(false) }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        )
        {
            Button(onClick = { updateDatabase = true }) {
                Text("Get Random Cat")
            }

            if (updateDatabase) {
                LaunchedEffect(key1 = Unit) {
                    coroutineScope.launch(Dispatchers.IO) {
                        randomCatPictureUrl = catGenerator.getRandomCatPictureUrl()
                        updateDatabase = false
                    }
                }
            }

            randomCatPictureUrl?.let {
                Image(
                    painter = rememberImagePainter(data = randomCatPictureUrl),
                    contentDescription = "Cat Image",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}