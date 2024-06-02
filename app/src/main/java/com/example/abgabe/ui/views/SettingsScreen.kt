package com.example.abgabe.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.example.abgabe.data.local.AppDatabase

class SettingsScreen: ViewModel() {

    @Composable
    fun ClearDatabase(db: AppDatabase) {
        Column {
            Text(text = "Clear Database")
            Button(onClick = { db.catDao().deleteAll() }) {
                Text("Clear")
            }
        }
    }
}