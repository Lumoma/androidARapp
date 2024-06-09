package com.example.abgabe.ui.views

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.abgabe.ui.states.SettingsUiState
import com.example.abgabe.ui.views.SettingsUI.ContentScreen
import com.example.abgabe.viewmodels.SettingsViewModel
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
                LoadingState("Loading...")
            }
            is SettingsUiState.EmptyDatabase -> {
                FillDatabaseQuestion(
                    onYesClick = {
                        coroutineScope.launch {
                            viewModel.generateNewDatabase(context)
                        }
                    },
                    onNoClick = {
                        viewModel.showContent()
                    }
                )
            }
            is SettingsUiState.Content -> {
                val currentCatsAmount = uiState.currentCatsAmount
                val randomCatPictureUrl  = uiState.currentRandomCatPic

                ContentScreen(
                    currentCatsAmount = currentCatsAmount,
                    onNavigateToOverview = onNavigateToOverview,
                    onGenerateCatsClicked = { amount ->
                        coroutineScope.launch {
                            viewModel.generateAmountOfCats(amount, context)
                        }
                    },
                    onDumpDatabaseClicked = {
                        coroutineScope.launch {
                            viewModel.dumpDatabase()
                        }
                    },
                    onGenerateNewDatabaseClicked = {
                        coroutineScope.launch {
                            viewModel.generateNewDatabase(context)
                        }
                    },
                    onGenerateRandomCatPictureClick = {
                        coroutineScope.launch {
                            viewModel.showContent()
                        }
                    },
                    randomCatPictureUrl = randomCatPictureUrl
                )
            }
            is SettingsUiState.WrongWish -> WishAlert { viewModel.showContent() }
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
                    .padding(16.dp)
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
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                var imageState by remember {
                    mutableStateOf<AsyncImagePainter.State>(
                        AsyncImagePainter.State.Empty
                    )
                }

                AsyncImage(
                    model = randomCatPictureUrl,
                    contentDescription = "Cat Image",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop,
                    onState = { state -> imageState = state }
                )

                if (imageState is AsyncImagePainter.State.Loading) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    onClick = { onGenerateRandomCatPictureClick() }) {
                    Icon(
                        Icons.Filled.AutoAwesome,
                        contentDescription = "Generate Random Cat Picture"
                    )
                }
            }
        }
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
    fun FillDatabaseQuestion(
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
                    onYesClick()
                    showDialog = false
                }) {
                    Text("Yes")
                }
            }, dismissButton = {
                Button(onClick = {
                    onNoClick()
                    showDialog = false
                }) {
                    Text("No")
                }
            })
        }
    }

    @Composable
    fun WishAlert(
        onNavigateToContent: () -> Unit
    ) {
        var showAlert by remember { mutableStateOf(true) }

        if (showAlert) {

            AlertDialog(onDismissRequest = {
                showAlert = false
            }, title = {
                Text(text = "Info")
            }, text = {
                Text("Your Wish has to be more than the current amount of Cats!")
            }, confirmButton = {
                Button(onClick = {
                    onNavigateToContent()
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