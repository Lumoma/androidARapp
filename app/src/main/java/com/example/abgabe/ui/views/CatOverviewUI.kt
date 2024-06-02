package com.example.abgabe.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.abgabe.ui.states.CatOverviewScreenUiState

object CatOverviewUI {
    @Composable
    fun CatOverviewContent(
        state: CatOverviewScreenUiState,
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (state) {
                CatOverviewScreenUiState.GeneratingCats -> {
                    Text("Generating cats...")
                }
                CatOverviewScreenUiState.AccessingDatabase -> {
                    Text("Accessing database...")
                }
                CatOverviewScreenUiState.LoadingPictures -> {
                    Text("Loading pictures...")
                }
                is CatOverviewScreenUiState.Content -> {
                    Text(state.text)
                    Button(onClick = state.onLoadClicked) {
                        Text("Load")
                    }
                }
            }
        }
    }

    @Composable
    fun GenerateCatsState(
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(modifier = modifier)
            Text("Generating cats...")
        }
    }

    @Composable
    fun AccessDatabaseState(
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(modifier = modifier)
            Text("Accessing database...")
        }
    }

    @Composable
    fun LoadPicturesState(
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(modifier = modifier)
            Text("Loading pictures...")
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun CatOverviewContentPreview(modifier: Modifier = Modifier) {
    CatOverviewUI.GenerateCatsState(modifier = modifier)
}

@Preview(showSystemUi = true)
@Composable
fun GenerateCatsStatePreview(modifier: Modifier = Modifier) {
    CatOverviewUI.AccessDatabaseState(modifier = modifier)
}

@Preview(showSystemUi = true)
@Composable
fun AccessDatabaseStatePreview(modifier: Modifier = Modifier) {
    CatOverviewUI.LoadPicturesState(modifier = modifier)
}

