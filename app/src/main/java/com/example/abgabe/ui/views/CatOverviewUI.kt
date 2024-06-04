package com.example.abgabe.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.abgabe.data.local.Cat
import com.example.abgabe.ui.states.CatOverviewUiState

object CatOverviewUI {

    @Composable
    fun Content(
        uiState: CatOverviewUiState,
        onNavigateToAR: () -> Unit,
        onNavigateToDatabase: () -> Unit,
        onNavigateToSettings: () -> Unit,
        onNavigateToDetail: (String) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        Column(
            modifier = modifier.padding(16.dp)
        ) {
            Text("Cat Overview")
            Button(onClick = onNavigateToAR) {
                Text("Go to AR")
            }
            Button(onClick = onNavigateToDatabase) {
                Text("Go to RandomCatGenerator")
            }
            Button(onClick = onNavigateToSettings) {
                Text("Settings")
            }

            when (uiState) {
                CatOverviewUiState.Loading -> {
                    CircularProgressIndicator()
                    Text("Loading cats...")
                }
                CatOverviewUiState.EmptyDatabase -> {
                    Text("Database is empty, go to settings to generate cats")
                }
                is CatOverviewUiState.Success -> {
                    ImageGrid(uiState.cats) { cat ->
                        onNavigateToDetail(cat.id.toString())
                    }
                }
            }
        }
    }

    @Composable
    fun ImageGrid(cats: List<Cat>, onClick: (Cat) -> Unit) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // Anzahl der Spalten
            contentPadding = PaddingValues(4.dp), // Padding um das Grid
            content = {
                items(cats.size) { index ->
                    ImageCard(cat = cats[index], onClick = onClick)
                }
            }
        )
    }

    @Composable
    fun ImageCard(cat: Cat, onClick: (Cat) -> Unit) {
        Card(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .clickable(onClick = { onClick(cat)}),
            shape = RoundedCornerShape(8.dp)
        ) {
            AsyncImage(
                model = cat.imageUrl,
                contentDescription = "Image from URL",
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }
    }
}


