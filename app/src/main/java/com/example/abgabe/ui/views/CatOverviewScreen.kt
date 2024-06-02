package com.example.abgabe.ui.views

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
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
        catGenerator: CatGenerator,
        catDatabase: AppDatabase,
        modifier: Modifier = Modifier
    ) {
        var cats by  remember {mutableStateOf(listOf<Cat>())}
        val coroutineScope = rememberCoroutineScope()
        var updateDatabase by remember { mutableStateOf(false) }
        var existentDatabase by remember { mutableStateOf(false) }

        if (updateDatabase) {
            LaunchedEffect(key1 = Unit) {
                coroutineScope.launch(Dispatchers.IO) {
                    // Überprüfen, ob die Datenbank bereits Bilder enthält
                    val existingCats = catDatabase.catDao().getAll()
                    if (existingCats.isEmpty()) {
                        // Wenn die Datenbank leer ist, fügen Sie neue Bilder hinzu
                        val newCats = catGenerator.getTenRandomCats()
                        // Fügen Sie die Bilder in die Datenbank ein
                        catDatabase.catDao().insertTen(newCats)
                        cats = newCats
                    }
                    updateDatabase = false
                }
            }
        }


        LazyColumn(
            modifier = modifier.padding(16.dp)
        ) {
            item {
                Text("Hallo")
            }
            item {
                Button(onClick = { onNavigateToAR() }) {
                    Text("Go to AR")
                }
            }
            item {
                Button(onClick = { updateDatabase = true }) {
                    Text("Create Database")
                }
            }
            item {
                Button(onClick = { onNavigateToDatabase() }) {
                    Text("Go to Database")
                }
            }
            item {
                Button(onClick = { onNavigateToSettings() }) {
                    Text("Settings")
                }
            }

            items(cats) { cat ->
                //Text(cat.name)
                Text(cat.imageUrl)

                /*

                Image(
                    painter = rememberImagePainter(data =  cat.imageUrl),
                    contentDescription = "Cat Image",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
                 */
            }
        }
    }
}


