package com.example.abgabe

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.abgabe.data.local.AppDatabase
import com.example.abgabe.data.remote.CatGenerator
import com.example.abgabe.ui.theme.AbgabeTheme
import com.example.abgabe.ui.views.CatOverviewUI
import com.example.abgabe.ui.views.QRCodeUI.QrCodeScannerScreen
import com.example.abgabe.ui.views.SettingsUI.HandleDatabaseContent
import com.example.abgabe.utils.QrCodeScannerViewModel
import com.example.abgabe.viewmodels.CatOverviewViewModel
import com.example.abgabe.viewmodels.DetailViewModel
import com.example.abgabe.viewmodels.RandomCatImageViewModel
import com.example.abgabe.viewmodels.SettingsViewModel
import com.google.zxing.integration.android.IntentIntegrator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // ViewModels
    private val homeScreenViewModel: CatOverviewViewModel by viewModels()
    private val detailScreenViewModel: DetailViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val randomCatScreen: RandomCatImageViewModel by viewModels()
    private val qrCodeScannerViewModel: QrCodeScannerViewModel by viewModels()

    @Inject
    lateinit var db: AppDatabase

    /*
    // AR
    private lateinit var arScreen: ARScreen
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
            // Kameraberechtigung zur Laufzeit anfordern
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
            }
            val arScreenFactory = ARScreenFactory()
            arScreen = arScreenFactory.createARScreen(this)
            //setContentView(R.layout.activity_main)
        */

        //TODO: val fitToScanView: ImageView = findViewById(R.id.fit_to_scan_view)
        enableEdgeToEdge()
        setContent {
            AbgabeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "API") {
                        composable("API") {
                            val uiState by homeScreenViewModel.uiState.collectAsState()
                            CatOverviewUI.Content(
                                uiState = uiState,
                                onNavigateToAR = { navController.navigate("AR") },
                                onNavigateToDatabase = { navController.navigate("RandomCatPictureGenerator") },
                                onNavigateToSettings = { navController.navigate("Settings") },
                                onNavigateToDetail = { id -> navController.navigate("Detail/$id") }
                            )
                        }
                        composable("AR") {
                            //TODO: arScreen.DisplayAR()
                            QrCodeScannerScreen(
                                viewModel = qrCodeScannerViewModel,
                                onCatFound = {
                                        id -> navController.navigate("Detail/$id")
                                }
                            )
                        }
                        composable("RandomCatPictureGenerator") {
                            randomCatScreen.DisplayCatJson()
                        }
                        composable("Settings") {
                            val uiState = settingsViewModel.uiState.collectAsState()
                            HandleDatabaseContent(viewModel = settingsViewModel, uiState = uiState.value, onNavigateToOverview = {
                                navController.navigate("API")
                            })
                        }
                        composable("Detail/{id}") {
                            val id = navController.currentBackStackEntry?.arguments?.getString("id")
                            detailScreenViewModel.DetailScreen(id = id)
                        }
                    }
                }
                /*
                FeatureThatRequiresCameraPermission()
                 */
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
               qrCodeScannerViewModel.onQrCodeScanned(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}

/*
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun FeatureThatRequiresCameraPermission() {

    // Camera permission state
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    if (cameraPermissionState.status.isGranted) {
        Text("Camera permission Granted")
    } else {
        Column {
            val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
                // If the user has denied the permission but the rationale can be shown,
                // then gently explain why the app requires this permission
                "The camera is important for this app. Please grant the permission."
            } else {
                // If it's the first time the user lands on this feature, or the user
                // doesn't want to be asked again for this permission, explain that the
                // permission is required
                "Camera permission required for this feature to be available. " +
                        "Please grant the permission"
            }
            Text(textToShow)
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Request permission")
            }
        }
    }
}

*/