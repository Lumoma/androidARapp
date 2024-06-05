package com.example.abgabe.ui.views

import android.app.Activity
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import com.example.abgabe.utils.QrCodeScannerViewModel
import com.google.zxing.integration.android.IntentIntegrator

object QRCodeUI {

    @Composable
    fun QrCodeScannerScreen(
        viewModel: QrCodeScannerViewModel,
        onCatFound: (String) -> Unit
    ) {
        val context = LocalContext.current
        Button(onClick = {
            IntentIntegrator(context as Activity).initiateScan()
        }) {
            Text("Scan QR Code")
        }

        val scannedCatId by viewModel.scannedCatId.observeAsState()
        scannedCatId?.let { onCatFound(it) }
    }

    @Composable
    fun QrCodeScannerScreen2(
        viewModel: QrCodeScannerViewModel,
        onCatFound: (String) -> Unit
    ) {
        val context = LocalContext.current
        Button(onClick = {
            val integrator = IntentIntegrator(context as Activity)
            integrator.setPrompt("Scan a barcode or QR Code")
            integrator.setBeepEnabled(false)
            integrator.setBarcodeImageEnabled(true)
            integrator.initiateScan()
        }) {
            Text("Scan QR Code")
        }

        val scannedCatId by viewModel.scannedCatId.observeAsState()
        scannedCatId?.let { onCatFound(it) }
    }
}