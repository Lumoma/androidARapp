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
import androidx.compose.ui.Modifier
import com.example.abgabe.data.local.AppDatabase
import com.example.abgabe.ui.states.SettingsUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object SettingsUI {

    @Composable
    fun HandleDatabaseContent(
        uiState: SettingsUIState,
        onNavigateToOverview: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Column {
            Text(text = "Settings")
            Text(text = "Database")
            Button(onClick = { TODO() }) {
                Text("Dump Database")
            }
            Button(onClick = { TODO() }) {
                Text("Fill Database")
            }
        }
    }
}