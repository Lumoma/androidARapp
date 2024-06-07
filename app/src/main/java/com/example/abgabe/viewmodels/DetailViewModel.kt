package com.example.abgabe.viewmodels

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCode2
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.example.abgabe.data.local.AppDatabase
import com.example.abgabe.data.local.Cat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val database: AppDatabase,
) : ViewModel() {
    var cat by mutableStateOf<Cat?>(null)
    var catId: UUID? = null

    init {
        refreshCat()
    }

    private fun setCatId(id: String) {
        catId = UUID.fromString(id)
        refreshCat()
    }

    private fun refreshCat() {
        viewModelScope.launch {
            catId?.let { database.catDao().getCatByIdFlow(it).collect { cat = it } }
        }
    }

    @Composable
    fun DetailScreen(
        context: Context,
        id: String?,
        onNavigateToOverview: () -> Unit,
    ) {
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(key1 = id) {
            coroutineScope.launch(Dispatchers.IO) {
                setCatId(id ?: return@launch)
            }
        }
        cat?.let {
            CatData( onNavigateToOverview = onNavigateToOverview, context = context)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CatData(
        onNavigateToOverview: () -> Unit,
        context: Context,
    ){
        val coroutineScope = rememberCoroutineScope()
        var showDialog by remember(cat) { mutableStateOf(false) }
        var catName by remember(cat?.id) { mutableStateOf(cat?.name ?: "") }
        var catBreed by remember(cat?.id) { mutableStateOf(cat?.breed ?: "") }
        var catTemperament by remember(cat?.id) { mutableStateOf(cat?.temperament ?: "") }
        var catOrigin by remember(cat?.id) { mutableStateOf(cat?.origin ?: "") }
        var catLifeExpectancy by remember(cat?.id) { mutableStateOf(cat?.lifeExpectancy ?: "") }


        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text(cat?.name?:"", maxLines = 1, overflow = TextOverflow.Clip
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
                        FloatingActionButton(onClick = { showDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Add")
                        }
                    }
                }
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        AsyncImage(
                            model = cat?.imageUrl ?: "",
                            contentDescription = "Image from URL",
                            modifier = Modifier
                                .height(300.dp)
                                .fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                item { Text("Name: ${cat?.name}") }
                item { Text("Breed: ${cat?.breed}") }
                item { Text("Temperament: ${cat?.temperament}") }
                item { Text("Origin: ${cat?.origin}") }
                item { Text("Life Expectancy: ${cat?.lifeExpectancy}") }
                item { cat?.let { DisplayQRCodeLink(qrCodeFilePath = it.qrCodePath, context = context ) } }
               item { cat?.let { DisplayQRCode(qrCodeImage = it.qrCodeByteArray) } }
            }
        }

        if (showDialog) {
            val keyboardController = LocalSoftwareKeyboardController.current

            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Edit Cat") },
                text = {
                    Column {
                        TextField(
                            value = catName,
                            onValueChange = { catName = it },
                            label = { Text("Name") },
                            singleLine = true
                        )
                        TextField(
                            value = catBreed,
                            onValueChange = { catBreed = it },
                            label = { Text("Breed") },
                            singleLine = true
                        )
                        TextField(
                            value = catTemperament,
                            onValueChange = { catTemperament = it },
                            label = { Text("Temperament") },
                            singleLine = true
                        )
                        TextField(
                            value = catOrigin,
                            onValueChange = { catOrigin = it },
                            label = { Text("Origin") },
                            singleLine = true
                        )
                        TextField(
                            value = catLifeExpectancy,
                            onValueChange = { catLifeExpectancy = it },
                            label = { Text("Life Expectancy") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            cat?.copy(name = catName, breed = catBreed, temperament = catTemperament, origin = catOrigin, lifeExpectancy = catLifeExpectancy)
                                ?.let { database.catDao().updateCatInfos(it) }
                        }
                        refreshCat()
                        showDialog = false
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
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
        Icon(Icons.Filled.QrCode2, contentDescription = "QR Code")
    }
}

    @Composable
    fun DisplayQRCode(qrCodeImage: ByteArray) {
        val bitmap = BitmapFactory.decodeByteArray(qrCodeImage, 0, qrCodeImage.size)
        val imageBitmap = bitmap.asImageBitmap()

        Card(
            modifier = Modifier
                .padding(4.dp)
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

@Preview
@Composable
fun PreviewDetailScreen() {

}