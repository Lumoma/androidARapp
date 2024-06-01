package com.example.abgabe.ui.views

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.abgabe.data.local.CatDao
import com.example.abgabe.data.remote.insertCatFromApiToDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeScreen(): ViewModel(){
    private val internalTextFlow = MutableStateFlow<String?>(null)

  @SuppressLint("NotConstructor")
@Composable
fun HomeScreen(
      onNavigateToAR: () -> Unit,
      onNavigateToDatabase: () -> Unit,
      modifier: Modifier = Modifier,
  ) {
        var showDatabase by remember { mutableStateOf(false) }

        val scrollState = rememberScrollState()
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ){
            Text("hello")
            Button(onClick = { onNavigateToAR() }) {
                Text("Go to AR")
            }
            Button(onClick = { onNavigateToDatabase() }) {
                Text("Create Database")
            }
        }
    }
}
