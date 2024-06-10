package com.example.abgabe.ui.views

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.abgabe.viewmodels.CameraScreenViewModel
import com.google.zxing.integration.android.IntentIntegrator

object CameraUI {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun QrCodeScannerScreen(
        viewModel: CameraScreenViewModel,
        onNavigateToOverview: () -> Unit,
        onCatFound: (String) -> Unit
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
                            "QR Code Scanner", maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    },
                    actions = {
                        IconButton(onClick = { onNavigateToOverview() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowForwardIos,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                )
            },
            bottomBar = {},
            content = {
            Column(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val validCatFound by viewModel.validCatFound.observeAsState()
                    validCatFound?.let {
                        if (!it) {
                            Text("No cat found")
                        }
                    }

                    val context = LocalContext.current
                    Button(onClick = {
                        IntentIntegrator(context as Activity).initiateScan()
                    }) {
                        Text("Scan QR Code")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val scannedCatId by viewModel.scannedCatId.observeAsState()
                    scannedCatId?.let {
                        Text("Scanned Cat ID: $it")
                        onCatFound(it)
                    }
                }
            }
        )
    }
}