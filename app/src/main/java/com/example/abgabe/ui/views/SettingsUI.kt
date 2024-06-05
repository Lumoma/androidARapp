package com.example.abgabe.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.abgabe.ui.states.SettingsUiState
import com.example.abgabe.viewmodels.SettingsViewModel

object SettingsUI {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HandleDatabaseContent(
        viewModel: SettingsViewModel,
        uiState: SettingsUiState,
        onNavigateToOverview: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text(
                            "Settings", maxLines = 1, overflow = TextOverflow.Clip
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { onNavigateToOverview() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBackIosNew,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                )
            },
            bottomBar = {
                BottomAppBar {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Absolute.SpaceAround
                    ) {
                        ExtendedFloatingActionButton(
                            onClick = { viewModel.onDumpDatabaseClicked() },
                            icon = { Icon(Icons.Filled.Delete, "Delete whole Database") },
                            text = { Text(text = "Dump Database") },
                            modifier = Modifier.padding(start = 16.dp) // Abstand von der linken Wand
                        )

                        ExtendedFloatingActionButton(
                            onClick = { viewModel.onGenerateCatsClicked(10) },
                            icon = { Icon(Icons.Filled.Dataset, "Generate Random Database") },
                            text = { Text(text = "Fill Database") },
                            modifier = Modifier.padding(end = 16.dp) // Abstand von der rechten Wand
                        )
                    }
                }
            },
        ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = modifier.verticalScroll(rememberScrollState())) {
                when (uiState) {
                    is SettingsUiState.Loading -> LoadingState(text = "Loading database...")

                    is SettingsUiState.DumpingDatabase -> LoadingState(text = "Dumping database...")

                    is SettingsUiState.Content -> ContentState(uiState.amount,
                        viewModel::onDumpDatabaseClicked
                    ) { amount -> (viewModel::onGenerateCatsClicked)(amount) }

                     is SettingsUiState.DatabaseEmpty -> {
                        ShowDialog { amount -> (viewModel::onGenerateCatsClicked)(amount) }
                        ContentState(0,
                            viewModel::onDumpDatabaseClicked
                        ) { amount -> (viewModel::onGenerateCatsClicked)(amount) }
                    }
                }
            }
            }
        }
    }

    @Composable
    fun ContentState(
        currentCatAmount: Int,
        onDumpDatabase: () -> Unit,
        onGenerateClick: (int: Int) -> Unit,
    ) {
        var catAmount by remember { mutableStateOf("") }
        val keyboardController = LocalSoftwareKeyboardController.current

        Text(text = "Cats in Database: $currentCatAmount")

        if ((catAmount.toIntOrNull() ?: 0) > 0) {
            val amount = catAmount.toIntOrNull() ?: 1
            Button(onClick = { onGenerateClick(amount) }) {
                Text(if (amount == 1) "Generate one more cat" else "Generate $amount more new cats")
            }
        } else {
            Button(onClick = { onGenerateClick(10) }) {
                Text("Generate 10 Cats")
            }
        }

        TextField(value = catAmount,
            onValueChange = { catAmount = it },
            label = { Text("Amount of new cats") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(onSend = { keyboardController?.hide() })
        )
    }

    @Composable
    fun LoadingState(
        text: String, modifier: Modifier = Modifier
    ) {

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
            AlertDialog(onDismissRequest = {
                showDialog = false
            }, title = {
                Text(text = "Database is empty!")
            }, text = {
                Text("Do you want to generate a new Database?")
            }, confirmButton = {
                Button(onClick = {
                    showDialog = false
                }) {
                    Text("No")
                }
            }, dismissButton = {
                Button(onClick = {
                    showDialog = false
                    onGenerateClick(10)
                }) {
                    Text("Yes, Generate new Database with 10 Cats")
                }
            })
        }
    }
}



