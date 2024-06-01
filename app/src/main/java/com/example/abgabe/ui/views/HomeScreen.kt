package com.example.abgabe.ui.views

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel

class HomeScreen(): ViewModel(){

@Composable
fun HomeScreen(
      onNavigateToAR: () -> Unit,
      onNavigateToDatabase: () -> Unit,
      modifier: Modifier = Modifier,
  ) {
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
