package com.example.abgabe.ui.views

import androidx.compose.foundation.layout.Column
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsScreen: ViewModel() {

    @Composable
    fun ClearDatabase(db: AppDatabase) {
        val coroutineScope = rememberCoroutineScope()
        var updateDatabase by remember { mutableStateOf(false) }

        if (updateDatabase) {
            LaunchedEffect(key1 = Unit) {
                coroutineScope.launch(Dispatchers.IO) {
                    withContext(Dispatchers.IO) {
                        db.clearAllTables()
                        updateDatabase = false
                    }
                }
            }
        }

        Column {
            Text(text = "Clear Database")
            Button(onClick = { updateDatabase = true }) {
                Text("Clear")
            }
        }
    }
}