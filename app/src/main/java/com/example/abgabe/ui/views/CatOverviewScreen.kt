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
import com.example.abgabe.data.remote.CatGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CatOverviewScreen(): ViewModel(){

    @Composable
    fun HomeScreen(
        onNavigateToAR: () -> Unit,
        onNavigateToDatabase: () -> Unit,
        onNavigateToSettings: () -> Unit,
        onNavigateToDetail: (String) -> Unit,
        catGenerator: CatGenerator,
        catDatabase: AppDatabase,
        modifier: Modifier = Modifier
    ) {
        val coroutineScope = rememberCoroutineScope()
        var cats by  remember {mutableStateOf(listOf<Cat>())}
        var updateDatabase by remember { mutableStateOf(false) }

        //Check if database is empty
        LaunchedEffect(key1 = Unit) {
            coroutineScope.launch(Dispatchers.IO) {
                val catsFromDatabase = catDatabase.catDao().getAll()
                if (catsFromDatabase.isEmpty()) {
                    updateDatabase = true
                }
                else {
                    cats = catsFromDatabase
                }
            }
        }

        //Create new Cats and update database
        if (updateDatabase) {
            LaunchedEffect(key1 = Unit) {
                coroutineScope.launch(Dispatchers.IO) {
                    catDatabase.catDao().deleteAll()
                    val newCats = catGenerator.getTenCatInfos()
                    newCats.forEach { catDatabase.catDao().insert(it) }
                    cats = newCats
                    updateDatabase = false
                }
            }
        }

        Column(
            modifier = modifier.padding(16.dp)
        ) {
            Text("Hallo")
            Button(onClick = { onNavigateToAR() }) {
                Text("Go to AR")
            }
            Button(onClick = { updateDatabase = true }) {
                Text("Create Database")
            }
            Button(onClick = { onNavigateToDatabase() }) {
                Text("Go to Database")
            }
            Button(onClick = { onNavigateToSettings() }) {
                Text("Settings")
            }

            ImageGrid(cats) { cat ->
                onNavigateToDetail(cat.id.toString())
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
                    .clickable(onClick = { onClick(cat) }),
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