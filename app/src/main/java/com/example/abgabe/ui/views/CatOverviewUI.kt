package com.example.abgabe.ui.views

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.abgabe.data.local.Cat
import com.example.abgabe.data.remote.generateQRCodeByteCodeFromUUID
import com.example.abgabe.ui.states.CatOverviewUiState
import com.example.abgabe.viewmodels.CatOverviewViewModel
import java.util.UUID

object CatOverviewUI {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Content(
        uiState: CatOverviewUiState,
        onNavigateToQR: () -> Unit,
        onNavigateToSettings: () -> Unit,
        onNavigateToDetail: (String) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        val showCreateCatDialog = remember { mutableStateOf(false) }

        AddCatDialog(
            showDialog = showCreateCatDialog.value,
            onDialogClose = {},
        )

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

            topBar = {
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
                    scrollBehavior = scrollBehavior,
                )
            },
            bottomBar = {
                BottomAppBar(
                    actions = {},
                    floatingActionButton = {
                        FloatingActionButton(onClick = { showCreateCatDialog.value = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                        }
                    },
                )
            },
        ) { innerPadding ->
            Box(
                modifier = modifier
                    .padding(innerPadding)
            ) {
                when (uiState) {
                    CatOverviewUiState.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            CircularProgressIndicator()
                            Text(text = "Loading Cats...")
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                    CatOverviewUiState.EmptyDatabase -> {
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

                    is CatOverviewUiState.Success -> {
                        ImageGrid(uiState.cats) { cat ->
                            onNavigateToDetail(cat.id.toString())
                        }
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
            })
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
            Column {
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

    @Composable
    fun AddCatDialog(
        showDialog: Boolean,
        onDialogClose: () -> Unit,
        //context: Context
    ) {
        var catName by remember { mutableStateOf("") }
        var catBreed by remember { mutableStateOf("") }
        var catTemperament by remember { mutableStateOf("") }
        var catOrigin by remember { mutableStateOf("") }
        var catLifeExpectancy by remember { mutableStateOf("") }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { onDialogClose() },
                title = { Text("Add Cat") },
                text = {
                    Column {
                        TextField(
                            value = catName,
                            onValueChange = { catName = it },
                            label = { Text("Name") }
                        )
                        TextField(
                            value = catBreed,
                            onValueChange = { catBreed = it },
                            label = { Text("Breed") }
                        )
                        TextField(
                            value = catTemperament,
                            onValueChange = { catTemperament = it },
                            label = { Text("Temperament") }
                        )
                        TextField(
                            value = catOrigin,
                            onValueChange = { catOrigin = it },
                            label = { Text("Origin") }
                        )
                        TextField(
                            value = catLifeExpectancy,
                            onValueChange = { catLifeExpectancy = it },
                            label = { Text("Life Expectancy") }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val uuid = UUID.randomUUID()
                        val newCat = Cat(
                            id = uuid,
                            name = catName,
                            breed = catBreed,
                            temperament = catTemperament,
                            origin = catOrigin,
                            lifeExpectancy = catLifeExpectancy,
                            imageUrl = "https://cdn2.thecatapi.com/images/MTYwNjQwMw.jpg", // TODO: get random cat image
                            qrCodeByteArray = generateQRCodeByteCodeFromUUID(uuid),
                            qrCodePath = "qrCodePath" // TODO: generateQRCodeFromUUID(catName, uuid, context)
                        )

                        //viewModel.addCat(newCat)
                        onDialogClose()
                    }) {
                        Text("Add Cat")
                    }
                },
                dismissButton = {
                    Button(onClick = { onDialogClose() }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun CatOverviewUIPreview() {
    CatOverviewUI.Content(
        uiState = CatOverviewUiState.Success(
            cats = listOf(
                Cat(
                    id = UUID.randomUUID(),
                    name = "Cat 1",
                    breed = "Breed 1",
                    temperament = "Temperament 1",
                    origin = "Origin 1",
                    lifeExpectancy = "Life Expectancy 1",
                    imageUrl = "https://cdn2.thecatapi.com/images/MTYwNjQwMw.jpg",
                    qrCodePath = "qrCodePath",
                    qrCodeByteArray = byteArrayOf(0, 1, 2, 3)
                ),
                Cat(
                    id = UUID.randomUUID(),
                    name = "Cat 2",
                    breed = "Breed 2",
                    temperament = "Temperament 2",
                    origin = "Origin 2",
                    lifeExpectancy = "Life Expectancy 2",
                    imageUrl = "https://cdn2.thecatapi.com/images/MTYwNjQwMw.jpg",
                    qrCodePath = "qrCodePath",
                    qrCodeByteArray = byteArrayOf(0, 1, 2, 3)
                ),
                Cat(
                    id = UUID.randomUUID(),
                    name = "Cat 3",
                    breed = "Breed 3",
                    temperament = "Temperament 3",
                    origin = "Origin 3",
                    lifeExpectancy = "Life Expectancy 3",
                    imageUrl = "https://cdn2.thecatapi.com/images/MTYwNjQwMw.jpg",
                    qrCodePath = "qrCodePath",
                    qrCodeByteArray = byteArrayOf(0, 1, 2, 3)
                ),
            )
        ),
        onNavigateToQR = {},
        onNavigateToSettings = {},
        onNavigateToDetail = {},
    )
}
