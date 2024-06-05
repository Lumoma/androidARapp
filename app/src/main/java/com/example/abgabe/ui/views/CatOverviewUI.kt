package com.example.abgabe.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.BottomAppBar
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.abgabe.data.local.Cat
import com.example.abgabe.ui.states.CatOverviewUiState
import java.util.UUID

object CatOverviewUI {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Content(
        uiState: CatOverviewUiState,
        onNavigateToQR: () -> Unit,
        onNavigateToRandomCatPicture: () -> Unit,
        onNavigateToSettings: () -> Unit,
        onNavigateToDetail: (String) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

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
                        IconButton(onClick = { /* do something */  }) {
                            Icon(
                                imageVector = Icons.Filled.ThumbUp,
                                contentDescription = "Localized description"
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
                    actions = {
                        IconButton(onClick = { onNavigateToQR() }) {
                            Icon(
                                Icons.Filled.ViewInAr,
                                contentDescription = "Localized description",
                            )
                        }
                        IconButton(onClick = { { /* do something */ } }) {
                            Icon(
                                Icons.Filled.ImageSearch,
                                contentDescription = "Localized description",
                            )
                        }
                        IconButton(onClick = { /* do something */ }) {
                            Icon(
                                Icons.Filled.QrCodeScanner,
                                contentDescription = "Localized description",
                            )
                        }
                        IconButton(onClick = { onNavigateToRandomCatPicture() }) {
                            Icon(
                                Icons.Filled.Autorenew,
                                contentDescription = "Localized description",
                            )
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = { /* do something */ }) {
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
                        CircularProgressIndicator()
                        Text("Loading cats...")
                    }

                    CatOverviewUiState.EmptyDatabase -> {
                        Text("Database is empty, go to settings to generate cats")
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
            Column { // Verwenden Sie eine Spalte, um das Bild und den Text zu stapeln
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
                    qrCodeImage = byteArrayOf(0, 1, 2, 3)
                ),
                Cat(
                    id = UUID.randomUUID(),
                    name = "Cat 2",
                    breed = "Breed 2",
                    temperament = "Temperament 2",
                    origin = "Origin 2",
                    lifeExpectancy = "Life Expectancy 2",
                    imageUrl = "https://cdn2.thecatapi.com/images/MTYwNjQwMw.jpg",
                    qrCodeImage = byteArrayOf(0, 1, 2, 3)
                ),
                Cat(
                    id = UUID.randomUUID(),
                    name = "Cat 3",
                    breed = "Breed 3",
                    temperament = "Temperament 3",
                    origin = "Origin 3",
                    lifeExpectancy = "Life Expectancy 3",
                    imageUrl = "https://cdn2.thecatapi.com/images/MTYwNjQwMw.jpg",
                    qrCodeImage = byteArrayOf(0, 1, 2, 3)
                ),
            )
        ),
        onNavigateToQR = {},
        onNavigateToRandomCatPicture = {},
        onNavigateToSettings = {},
        onNavigateToDetail = {},
    )
}
