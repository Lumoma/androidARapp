package com.example.abgabe.ui.views

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.abgabe.data.local.Cat
import com.example.abgabe.ui.states.OverviewUiState
import com.example.abgabe.viewmodels.OverviewViewModel
import java.util.UUID

object OverviewUI {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun OverviewScreen(
        viewModel: OverviewViewModel,
        uiState: OverviewUiState,
        onNavigateToQR: () -> Unit,
        onNavigateToSettings: () -> Unit,
        onNavigateToDetail: (String) -> Unit,
        onGenerateNewPictureURL: () -> Unit,
        context: Context,
    ) {
        Scaffold(
            topBar = {
                HandleTopBar(
                    onNavigateToQR = onNavigateToQR,
                    onNavigateToSettings = onNavigateToSettings,
                )
            },
            bottomBar = {
                if (uiState is OverviewUiState.Content){
                    HandleBottomBarOverview( onOpenAddNewCatScreen = { viewModel.addCatToggle.value = true })
                }
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (uiState) {
                    OverviewUiState.Loading -> {
                        LoadingScreen("Loading cats...")
                    }

                    OverviewUiState.EmptyDatabase -> {
                        DatabaseEmptyScreen()
                    }

                    is OverviewUiState.Content -> {
                        ContentScreen(
                            cats = uiState.cats,
                            onNavigateToDetail = onNavigateToDetail,
                        )
                    }

                    is OverviewUiState.AddCat -> {
                        AddNewCatScreen(
                            onGenerateNewPictureURL = onGenerateNewPictureURL,
                            randomCatPictureUrl = uiState.pictureUrl,
                            onBackToOverview = { viewModel.addCatToggle.value = false },
                            onSaveNewCat = uiState.onSaveCat,
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HandleTopBar(
        onNavigateToQR: () -> Unit,
        onNavigateToSettings: () -> Unit,
    ){
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Text(
                    "Cat Overview", maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = { onNavigateToQR() }) {
                    Icon(
                        Icons.Filled.QrCodeScanner,
                        contentDescription = "Localized description",
                    )
                }
            },
            actions = {
                IconButton(onClick = { onNavigateToSettings() }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Localized description"
                    )
                }
            },
        )
    }

    @Composable
    fun HandleBottomBarOverview(
        onOpenAddNewCatScreen: () -> Unit
    ){
        BottomAppBar(
            actions = {},
            floatingActionButton = {
                FloatingActionButton(onClick = { onOpenAddNewCatScreen() }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            },
        )
    }

    @Composable
    fun ContentScreen(
        cats: List<Cat>,
        onNavigateToDetail: (String) -> Unit,
    ) {
        ImageGrid(cats) { cat ->
            onNavigateToDetail(cat.id.toString())
        }
    }

    @Composable
    fun LoadingScreen(
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
    fun DatabaseEmptyScreen(){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text("Database is empty!")
            Text("Go to settings to generate cats.")
            Spacer(modifier = Modifier.weight(1f))
        }
    }

    @Composable
    fun AddNewCatScreen(
        onGenerateNewPictureURL: () -> Unit,
        onSaveNewCat: (Cat) -> Unit,
        onBackToOverview: () -> Unit,
        randomCatPictureUrl: String,
    ) {
        var catName by remember { mutableStateOf("") }
        var catBreed by remember { mutableStateOf("") }
        var catTemperament by remember { mutableStateOf("") }
        var catOrigin by remember { mutableStateOf("") }
        var catLifeExpectancy by remember { mutableStateOf("") }

        val keyboardController = LocalSoftwareKeyboardController.current

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                ){
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Box(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            var imageState by remember { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }

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
                                    .padding(16.dp),
                                onClick = { onGenerateNewPictureURL() }) {
                                Icon(
                                    Icons.Filled.AutoAwesome,
                                    contentDescription = "Generate Random Cat Picture"
                                )
                            }
                        }
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        ) {
                        TextField(
                            modifier = Modifier.padding(8.dp),
                            value = catName,
                            onValueChange = { catName = it },
                            label = { Text("Name") },
                            singleLine = true,
                            keyboardActions = KeyboardActions(onSend = { keyboardController?.hide() })
                        )
                        TextField(
                            modifier = Modifier.padding(8.dp),
                            value = catBreed,
                            onValueChange = { catBreed = it },
                            label = { Text("Breed") },
                            singleLine = true,
                            keyboardActions = KeyboardActions(onSend = { keyboardController?.hide() })
                        )
                        TextField(
                            modifier = Modifier.padding(8.dp),
                            value = catTemperament,
                            onValueChange = { catTemperament = it },
                            label = { Text("Temperament") },
                            singleLine = true,
                            keyboardActions = KeyboardActions(onSend = { keyboardController?.hide() })
                        )
                        TextField(
                            modifier = Modifier.padding(8.dp),
                            value = catOrigin,
                            onValueChange = { catOrigin = it },
                            label = { Text("Origin") },
                            singleLine = true,
                            keyboardActions = KeyboardActions(onSend = { keyboardController?.hide() })
                        )
                        TextField(
                            modifier = Modifier.padding(8.dp),
                            value = catLifeExpectancy,
                            onValueChange = { catLifeExpectancy = it },
                            label = { Text("Life Expectancy") },
                            singleLine = true,
                            keyboardActions = KeyboardActions(onSend = { keyboardController?.hide() })
                        )
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween

                ) {
                    Button(
                        onClick = { onBackToOverview() }
                    ) {
                        Text("Back")
                    }
                    Button(
                        onClick = {
                            val newCat = Cat(
                                id = UUID.randomUUID(),
                                name = catName,
                                breed = catBreed,
                                temperament = catTemperament,
                                origin = catOrigin,
                                lifeExpectancy = catLifeExpectancy,
                                imageUrl = randomCatPictureUrl,
                                qrCodePath = null.toString(),
                                qrCodeByteArray = null.toString().toByteArray()
                            )
                            onSaveNewCat(newCat)
                        }
                    ) {
                        Text("Save and Add to Database  ")
                        Icon(
                            Icons.Filled.Save,
                            contentDescription = "Save"
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ImageGrid(cats: List<Cat>, onClick: (Cat) -> Unit) {
        LazyVerticalGrid(columns = GridCells.Fixed(2), // Anzahl der Spalten
            contentPadding = PaddingValues(4.dp), // Padding um das Grid
            content = {
                items(cats.size) { index ->
                    ImageCard(cat = cats[index], onClick = onClick)
                }
            }
        )
    }

    @Composable
    fun ImageCard(cat: Cat, onClick: (Cat) -> Unit) {
        Card(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .clickable(onClick = { onClick(cat) }),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                AsyncImage(
                    model = cat.imageUrl,
                    contentDescription = "Image from URL",
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = cat.name,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp)
                    )
                    Text(
                        text = cat.breed,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewOverviewScreen() {
    OverviewUI.ContentScreen(
        cats = listOf(
            Cat(
                id = UUID.randomUUID(),
                name = "Minka",
                breed = "Siamese",
                temperament = "Calm",
                origin = "Thailand",
                lifeExpectancy = "15 years",
                imageUrl = null.toString(),
                qrCodePath = null.toString(),
                qrCodeByteArray = null.toString().toByteArray()
            ),
            Cat(
                id = UUID.randomUUID(),
                name = "Lulu",
                breed = "Bengal",
                temperament = "Calm",
                origin = "Thailand",
                lifeExpectancy = "15 years",
                imageUrl = null.toString(),
                qrCodePath = null.toString(),
                qrCodeByteArray = null.toString().toByteArray()
            ),
        ),
        onNavigateToDetail = {},
    )
}






@Preview
@Composable
fun PreviewAddNewCatScreen() {
    OverviewUI.AddNewCatScreen(
        onGenerateNewPictureURL = {},
        onSaveNewCat = {},
        onBackToOverview = {},
        randomCatPictureUrl = "https://cdn2.thecatapi.com/images/MTYwNjQwNQ.jpg"
    )
}