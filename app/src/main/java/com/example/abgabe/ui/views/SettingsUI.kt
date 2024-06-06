package com.example.abgabe.ui.views

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.abgabe.data.remote.getRandomCatPicture
import com.example.abgabe.ui.states.SettingsUiState
import com.example.abgabe.viewmodels.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SettingsUI {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HandleDatabaseContent(
        context: Context,
        viewModel: SettingsViewModel,
        uiState: SettingsUiState,
        onNavigateToOverview: () -> Unit,
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
                            modifier = Modifier.padding(start = 16.dp)
                        )

                        ExtendedFloatingActionButton(
                            onClick = { viewModel.onGenerateCatsClicked(10, context = context) },
                            icon = { Icon(Icons.Filled.Dataset, "Generate Random Database") },
                            text = { Text(text = "Fill Database") },
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                }
            },
        ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 16.dp)
                .padding(start = 16.dp)
                .padding(top = 16.dp)
                .padding(bottom = 16.dp)
                .padding(innerPadding)
        ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    when (uiState) {
                        is SettingsUiState.Loading -> LoadingState(text = "Loading database...", )

                        is SettingsUiState.DumpingDatabase -> LoadingState(text = "Dumping database...")

                        is SettingsUiState.Content -> ContentState(uiState.amount,
                            viewModel::onDumpDatabaseClicked
                        ) { amount -> (viewModel::onGenerateCatsClicked)(amount, context) }

                         is SettingsUiState.DatabaseEmpty -> {
                            ShowDialog { amount -> (viewModel::onGenerateCatsClicked)(amount, context) }
                            ContentState(0,
                                viewModel::onDumpDatabaseClicked
                            ) { amount -> (viewModel::onGenerateCatsClicked)(amount, context) }
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

        Text(text = "Amount of Cats: ",
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(text = "$currentCatAmount",
            fontSize = 40.sp,
            modifier = Modifier.padding(bottom = 16.dp)
                .padding(start = 16.dp)
        )
        HorizontalDivider(thickness = 2.dp)
        Text(text = "Generate new Cats: ",
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
        Column(
            modifier = Modifier.padding(top = 16.dp)
                .padding(start = 16.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(value = catAmount,
                onValueChange = { catAmount = it },
                label = { Text("Amount of new cats") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(onSend = { keyboardController?.hide() })
            )

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
        }
        HorizontalDivider(thickness = 2.dp)
        Text(text = "Random Cat Picture Generator: ",
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
        DisplayRandomCatPicture()
    }

    @Composable
    fun LoadingState(
        text: String
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))
                CircularProgressIndicator()
                Text(text = text)
                Spacer(modifier = Modifier.weight(1f))
            }
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
                    Text("Yes")
                }
            })
        }
    }

    @Composable
    fun DisplayRandomCatPicture(
        modifier: Modifier = Modifier)
    {
        var  randomCatPictureUrl by remember { mutableStateOf<String?>(null) }
        val coroutineScope = rememberCoroutineScope()
        var updateDatabase by remember { mutableStateOf(false) }

        Column(
            modifier = modifier
                .fillMaxSize()

        )
        {
            Button(
                modifier = modifier.padding(16.dp),
                onClick = { updateDatabase = true }) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = "Generate Random Cat Picture")
            }

            if (updateDatabase) {
                LaunchedEffect(key1 = Unit) {
                    coroutineScope.launch(Dispatchers.IO) {
                        randomCatPictureUrl = getRandomCatPicture()
                        updateDatabase = false
                    }
                }
            }

            randomCatPictureUrl?.let {
                Image(
                    painter = rememberImagePainter(data = randomCatPictureUrl),
                    contentDescription = "Cat Image",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}




@Preview
@Composable
fun PreviewSettingsUI () {
    SettingsUI.ContentState(10, {}, {})
}