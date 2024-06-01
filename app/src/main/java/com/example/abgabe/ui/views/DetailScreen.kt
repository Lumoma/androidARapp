package com.example.abgabe.ui.views

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.abgabe.data.local.AppDatabase
import com.example.abgabe.data.local.Cat
import com.example.abgabe.data.remote.CatGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailScreen: ViewModel() {
    @Composable
    fun DisplayCatJson(catDatabase: AppDatabase, catGenerator: CatGenerator) {
        var cat by remember { mutableStateOf<Cat?>(null) }
        val coroutineScope = rememberCoroutineScope()
        var updateDatabase by remember { mutableStateOf(false) }

        Button(onClick = { updateDatabase = true }) {
            Text("Get Random Cat")
        }

        if (updateDatabase) {
            LaunchedEffect(key1 = Unit) {
                coroutineScope.launch(Dispatchers.IO) {
                    val newCat = catGenerator.getOneRandomCat()
                    newCat?.let { catDatabase.catDao().insert(it) }
                    cat = newCat
                }
            }
        }

        cat?.let { cat ->
            Text("Cat: ${cat.name}")
        }
    }
}