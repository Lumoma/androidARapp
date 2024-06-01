package com.example.abgabe.ui.views

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
import com.example.abgabe.data.remote.insertCatFromApiToDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailScreen: ViewModel() {
    @Composable
    fun DisplayCatJson(catDatabase: AppDatabase) {

        var catJson by remember { mutableStateOf<String?>(null) }
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(key1 = Unit) {
            coroutineScope.launch(Dispatchers.IO) {
                insertCatFromApiToDb(catDatabase)
            }
        }

        catJson?.let { json ->
            Text("Cat JSON: $json")
        }
    }
}