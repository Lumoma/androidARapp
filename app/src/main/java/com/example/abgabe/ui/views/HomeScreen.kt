package com.example.abgabe.ui.views

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class Cat(
    val id: Int = 0,
    val name: String,
    val breed: String,
    val imageUrl: String,
    // ... weitere Eigenschaften
)


@Composable
fun HomeScreen(
    navController: NavController
) {
    val cats = listOf( // Beispiel-Katzenliste
        Cat(1, "Whiskers", "Siamese", "https://cdn2.thecatapi.com/images/MTg0NjE0OQ.jpg"),
        Cat(2, "Mittens", "Persian", "https://cdn2.thecatapi.com/images/abys.jpg"),
        Cat(3, "Oliver", "Maine Coon", "https://cdn2.thecatapi.com/images/MTgzOTI4Nw.jpg")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

    }
}

@Preview
@Composable
fun HomeScreenPreview() {

}