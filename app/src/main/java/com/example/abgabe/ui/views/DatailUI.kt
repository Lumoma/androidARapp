package com.example.abgabe.ui.views

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.abgabe.data.local.Cat
import com.example.abgabe.viewmodels.DetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DetailUI {

    @Composable
fun DetailScreen(
    viewModel: DetailViewModel, // Pass the ViewModel as a parameter
    context: Context,
    id: String?,
    onNavigateToOverview: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = id) {
        coroutineScope.launch(Dispatchers.IO) {
            viewModel.setCatId(id ?: return@launch)
        }
    }
        ContentScreen(viewModel, onNavigateToOverview, context = context)
}

    @Composable
    fun ErrorScreen(innerPadding: PaddingValues) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text("Cat not found.")
            Text("Scan another QR code.")
            Spacer(modifier = Modifier.weight(1f))
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
@Composable
    fun ContentScreen(
        viewModel: DetailViewModel,
        onNavigateToOverview: () -> Unit,
        context: Context,
    ){
        val showDialog by viewModel.catEditFlow.collectAsState()
        val catName by viewModel.catNameFlow.collectAsState()
        val catBreed by viewModel.catBreedFlow.collectAsState()
        val catTemperament by viewModel.catTemperamentFlow.collectAsState()
        val catOrigin by viewModel.catOriginFlow.collectAsState()
        val catLifeExpectancy by viewModel.catLifeExpectancyFlow.collectAsState()

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                            Text(catName, maxLines = 1, overflow = TextOverflow.Clip)
                    },
                    navigationIcon = {
                        IconButton(onClick = { onNavigateToOverview() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBackIosNew,
                                contentDescription = "go back to overview"
                            )
                        }
                    },
                )
            },
            bottomBar = {
                BottomAppBar {
                    Row(
                        Modifier.fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        FloatingActionButton(onClick = {viewModel.openEditWindow()}) {
                            Icon(Icons.Default.Edit, contentDescription = "Add")
                        }
                        FloatingActionButton(onClick = {
                            onNavigateToOverview()
                            viewModel.deleteCatFromDatabase()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            },
        ) { innerPadding ->
            val uiState by viewModel.uiState.collectAsState()

            when (val state = uiState) {
                is DetailViewModel.DetailsUiState.Loading -> {
                    OverviewUI.LoadingScreen("Loading Cat Details...")
                }
                is DetailViewModel.DetailsUiState.Content -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(innerPadding)
                    ) {
                        item {
                            Card(
                                modifier = Modifier
                                    .padding(top = 16.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                AsyncImage(
                                    model = state.cat.imageUrl,
                                    contentDescription = "Image from URL",
                                    modifier = Modifier
                                        .height(300.dp)
                                        .fillMaxWidth(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        item { Text("Name: ${state.cat.name}") }
                        item { Text("Breed: ${state.cat.breed}") }
                        item { Text("Temperament: ${state.cat.temperament}") }
                        item { Text("Origin: ${state.cat.origin}") }
                        item { Text("Life Expectancy: ${state.cat.lifeExpectancy}") }
                        item { DisplayQRCodeLink(qrCodeFilePath = state.cat.qrCodePath, context = context ) }
                        item { DisplayQRCode(qrCodeImage = state.cat.qrCodeByteArray) }
                    }
                }
                is DetailViewModel.DetailsUiState.Edit -> {
                    AlertDialog(
                        onDismissRequest = { viewModel.catEditFlow.value = false },
                        title = { Text("Edit Cat") },
                        text = {
                            Column {
                                TextField(
                                    value = catName,
                                    onValueChange = state.onEditName,
                                    label = { Text("Name") },
                                    singleLine = true
                                )
                                TextField(
                                    value = catBreed,
                                    onValueChange = state.onEditBreed,
                                    label = { Text("Breed") },
                                    singleLine = true
                                )
                                TextField(
                                    value = catTemperament,
                                    onValueChange = state.onEditTemperament,
                                    label = { Text("Temperament") },
                                    singleLine = true
                                )
                                TextField(
                                    value = catOrigin,
                                    onValueChange = state.onEditOrigin,
                                    label = { Text("Origin") },
                                    singleLine = true
                                )
                                TextField(
                                    value = catLifeExpectancy,
                                    onValueChange = state.onEditLifeExpectancy,
                                    label = { Text("Life Expectancy") },
                                    singleLine = true
                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = state.onSaveChanges) {
                                Text("Confirm")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { viewModel.catEditFlow.value = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun DisplayQRCodeLink(qrCodeFilePath: String, context: Context) {
        val uri = remember { Uri.parse(qrCodeFilePath) }

        IconButton(onClick = {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "image/png")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)
        }) {
            Icon(Icons.Filled.Folder, contentDescription = "QR Code")
        }
    }

    @Composable
    fun DisplayQRCode(qrCodeImage: ByteArray) {
        val bitmap = BitmapFactory.decodeByteArray(qrCodeImage, 0, qrCodeImage.size)
        val imageBitmap = bitmap.asImageBitmap()

        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Image(
                bitmap = imageBitmap,
                contentDescription = "QR Code",
                modifier = Modifier
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }
    }
}