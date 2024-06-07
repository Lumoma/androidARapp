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
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.abgabe.data.remote.getRandomCatPicture
import com.example.abgabe.ui.states.SettingsUiState
import com.example.abgabe.ui.views.SettingsUI.ContentScreen
import com.example.abgabe.ui.views.SettingsUI.SettingsScreen
import com.example.abgabe.viewmodels.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SettingsUI {

    @Composable
    fun SettingsScreen(
        viewModel: SettingsViewModel,
        uiState: SettingsUiState,
        onNavigateToOverview: () -> Unit,
        context: Context
    ) {
        val coroutineScope = rememberCoroutineScope()

        when (uiState) {
            is SettingsUiState.Loading -> {
                LoadingState("Loading")
            }
            is SettingsUiState.EmptyDatabase -> {
                Question("Database is empty! Do you want to generate a new Database?",
                    onYesClick = {
                        coroutineScope.launch {
                            viewModel.generateCat(10, context)
                        }
                    },
                    onNoClick = {
                        onNavigateToOverview()
                    }
                )
            }
            is SettingsUiState.Content -> {
                val currentCatsAmount = (uiState as SettingsUiState.Content).currentCatsAmount
                var randomCatPictureUrl by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(key1 = currentCatsAmount) {
                    randomCatPictureUrl = getRandomCatPicture()
                }

                ContentScreen(
                    currentCatsAmount = currentCatsAmount,
                    onNavigateToOverview = onNavigateToOverview,
                    onGenerateCatsClicked = { amount ->
                        coroutineScope.launch {
                            viewModel.generateCat(amount, context)
                        }
                    },
                    onDumpDatabaseClicked = {
                        coroutineScope.launch {
                            viewModel.dumpDatabase()
                        }
                    },
                    onGenerateNewDatabaseClicked = {
                        coroutineScope.launch {
                            viewModel.generateCat(10, context)
                        }
                    },
                    onGenerateRandomCatPictureClick = {
                        coroutineScope.launch {
                            randomCatPictureUrl = getRandomCatPicture()
                        }
                    },
                    randomCatPictureUrl = randomCatPictureUrl
                )
            }

            SettingsUiState.DumpingDatabase -> viewModel.dumpDatabase()
            SettingsUiState.Error -> Alert("Your Wish has to be more than the current amount of Cats!")
            is SettingsUiState.UpdatingDatabase -> LoadingState("Updating Database")
        }
    }


    @Composable
    fun ContentScreen(
        currentCatsAmount: Int,
        onNavigateToOverview: () -> Unit,
        onGenerateCatsClicked: (Int) -> Unit,
        onDumpDatabaseClicked: () -> Unit,
        onGenerateNewDatabaseClicked: () -> Unit,
        onGenerateRandomCatPictureClick: () -> Unit,
        randomCatPictureUrl: String?
    ) {
        Scaffold(
            topBar = {
                HandleTopBar(onNavigateToOverview)
            },
            bottomBar = {
                HandleBottomBar(
                    onDumpDatabaseClicked = onDumpDatabaseClicked,
                    onGenerateNewDatabaseClicked = onGenerateNewDatabaseClicked
                )
            },
        ) { innerPadding ->
            LazyColumn (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 16.dp)
                    .padding(start = 16.dp)
                    .padding(top = 16.dp)
                    .padding(bottom = 16.dp)
                    .padding(innerPadding)
            ) {
                item {
                    CurrentCatAmount(currentCatAmount = currentCatsAmount)
                }
                item {
                    HorizontalDivider(thickness = 2.dp)
                }
                item {
                    CatGenerator(onGenerateCatsClick = { amount -> (onGenerateCatsClicked)(amount) })
                }
                item {
                    HorizontalDivider(thickness = 2.dp)
                }
                item {
                    RandomCatPictureGenerator( onGenerateRandomCatPictureClick = onGenerateRandomCatPictureClick, randomCatPictureUrl = randomCatPictureUrl)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HandleTopBar(
        onNavigateToOverview: () -> Unit,
    ) {
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
    }

    @Composable
    fun HandleBottomBar(
        onDumpDatabaseClicked: () -> Unit,
        onGenerateNewDatabaseClicked: () -> Unit,
    ) {
        BottomAppBar {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Absolute.SpaceAround
            ) {
                ExtendedFloatingActionButton(
                    onClick = { onDumpDatabaseClicked() },
                    icon = { Icon(Icons.Filled.Delete, "Delete whole Database") },
                    text = { Text(text = "Dump Database") },
                    modifier = Modifier.padding(start = 16.dp)
                )

                ExtendedFloatingActionButton(
                    onClick = { onGenerateNewDatabaseClicked() },
                    icon = { Icon(Icons.Filled.Dataset, "Generate Random Database") },
                    text = { Text(text = "Fill Database") },
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }
    }


    @Composable
    fun CurrentCatAmount(
        currentCatAmount: Int
    ) {
        Text(text = "Amount of Cats: ",
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(text = "$currentCatAmount",
            fontSize = 40.sp,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .padding(start = 16.dp)
        )
    }

    @Composable
    fun CatGenerator(
        onGenerateCatsClick: (toGenerateAmount: Int) -> Unit,
    ) {
        var wishedCatAmount by remember { mutableStateOf("") }
        val keyboardController = LocalSoftwareKeyboardController.current

        Text(text = "Generate new Cats: ",
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
        Column(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(start = 16.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(value = wishedCatAmount,
                onValueChange = {  wishedCatAmount = it },
                label = { Text("Wished amount of cats") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(onSend = { keyboardController?.hide() })
            )

            Button(onClick = { onGenerateCatsClick(wishedCatAmount.toInt()) }) {
                    Text("Generate")

            }
        }
    }

    @Composable
    fun RandomCatPictureGenerator(
        onGenerateRandomCatPictureClick: () -> Unit,
        randomCatPictureUrl: String? = null,
    ){
        Text(text = "Random Cat Picture Generator: ",
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
        Button(
            modifier = Modifier.padding(16.dp),
            onClick = { onGenerateRandomCatPictureClick() }) {
            Icon(Icons.Filled.AutoAwesome, contentDescription = "Generate Random Cat Picture")
        }
        Image(
            painter = rememberImagePainter(data = randomCatPictureUrl),
            contentDescription = "Cat Image",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
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
    fun Question(
        question: String,
        onYesClick: () -> Unit,
        onNoClick: () -> Unit
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
                }) {
                    Text("Yes")
                }
            })
        }
    }

    @Composable
    fun Alert(
        text: String
    ) {
        var showAlert by remember { mutableStateOf(true) }

        if (showAlert) {

            AlertDialog(onDismissRequest = {
                showAlert = false
            }, title = {
                Text(text = "Info")
            }, text = {
                Text(text)
            }, confirmButton = {
                Button(onClick = {
                    showAlert = false
                }) {
                    Text("Close")
                }
            })
        }
    }
}

@Preview
@Composable
fun PreviewSettingsUI () {
    ContentScreen(
        currentCatsAmount = 10,
        onNavigateToOverview = {},
        onGenerateCatsClicked = { },
        onDumpDatabaseClicked = { },
        onGenerateNewDatabaseClicked = { },
        onGenerateRandomCatPictureClick = { },
        randomCatPictureUrl = "randomCatPictureUrl"
    )
}