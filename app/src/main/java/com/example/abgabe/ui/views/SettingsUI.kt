package com.example.abgabe.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.abgabe.ui.states.SettingsUiState
import com.example.abgabe.viewmodels.SettingsViewModel

object SettingsUI {

    @Composable
    fun HandleDatabaseContent(
        viewModel: SettingsViewModel,
        uiState: SettingsUiState,
        onNavigateToOverview: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Column {
            Box(modifier = modifier, contentAlignment = Alignment.Center) {

                //Headline
                Text(
                    text = "Settings",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 50.dp),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(modifier = modifier.verticalScroll(rememberScrollState())) {
                when (uiState) {
                    SettingsUiState.Loading -> LoadingState(text = "Loading database...")
                    SettingsUiState.DumpingDatabase -> LoadingState(text = "Dumping database...")
                    is SettingsUiState.Content -> ContentState(
                        uiState.amount,
                        onNavigateToOverview,
                        viewModel::onDumpDatabaseClicked,
                        { amount -> (viewModel::onGenerateCatsClicked)(amount) }
                    )
                    SettingsUiState.DatabaseEmpty -> {
                        // Hier kannst du entweder den Dialog anzeigen oder eine andere UI für eine leere Datenbank
                        ShowDialog({ amount -> (viewModel::onGenerateCatsClicked)(amount) })
                        ContentState(
                            0,
                            onNavigateToOverview,
                            viewModel::onDumpDatabaseClicked,
                            { amount -> (viewModel::onGenerateCatsClicked)(amount) }
                        )
                    }
                }
            }
        }
    }

    @Composable
fun ContentState(
    currentCatAmount: Int,
    onNavigateToSettings: () -> Unit,
    onDumpDatabase: () -> Unit,
    onGenerateClick: (int: Int) -> Unit,
) {
    var catAmount by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Button(onClick = { onNavigateToSettings() }) {
        Text("Back to Overview")
    }
    Button(onClick = { onDumpDatabase() }) {
        Text("Dump Database")
    }

    Text(text = "Cats in Database: $currentCatAmount")

    if ((catAmount.toIntOrNull() ?: 0) > 0) {
        Button(onClick = { onGenerateClick(catAmount.toIntOrNull() ?: 1) }) {
            Text("Generate ${catAmount.toIntOrNull() ?: 1} new cats")
        }
    }
    else {
        Button(onClick = { onGenerateClick(10) }) {
            Text("Generate 10 Cats")
        }
    }

    TextField(
        value = catAmount,
        onValueChange = { catAmount = it },
        label = { Text("Amount of new cats") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        keyboardActions = KeyboardActions(onSend = { keyboardController?.hide() })
    )
}

    @Composable
    fun LoadingState(
        text: String,
        modifier: Modifier = Modifier) {

        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = modifier.align(Alignment.Center))
            Text(text = text, modifier = modifier.align(Alignment.BottomCenter))
        }
    }

    @Composable
    fun ShowDialog(
        onGenerateClick: (int: Int) -> Unit,
    ) {
        var showDialog by remember { mutableStateOf(true) }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                },
                title = {
                    Text(text = "Database is empty!")
                },
                text = {
                    Text("Do you want to generate a new Database?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog = false
                        }
                    ) {
                        Text("No")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            onGenerateClick(10)
                        }
                    ) {
                        Text("Yes, Generate new Database with 10 Cats")
                    }
                }
            )
        }
    }
}



