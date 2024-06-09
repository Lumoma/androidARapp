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
import com.example.abgabe.ui.theme.AbgabeTheme
import com.example.abgabe.ui.views.DetailUI
import com.example.abgabe.ui.views.OverviewUI
import com.example.abgabe.ui.views.QRCodeUI.QrCodeScannerScreen
import com.example.abgabe.ui.views.SettingsUI
import com.example.abgabe.utils.QrCodeScannerViewModel
import com.example.abgabe.viewmodels.OverviewViewModel
import com.example.abgabe.viewmodels.DetailViewModel
import com.example.abgabe.viewmodels.SettingsViewModel
import com.google.zxing.integration.android.IntentIntegrator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // ViewModels
    private val homeScreenViewModel: OverviewViewModel by viewModels()
    private val detailScreenViewModel: DetailViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
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
        enableEdgeToEdge()
        setContent {
            AbgabeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "Overview") {
                        composable("Overview") {
                            val uiState by homeScreenViewModel.uiState.collectAsState()
                            OverviewUI.OverviewScreen(
                                viewModel = homeScreenViewModel,
                                uiState = uiState,
                                onNavigateToQR = { navController.navigate("QR") },
                                onNavigateToSettings = { navController.navigate("Settings") },
                                onNavigateToDetail = { id -> navController.navigate("Detail/$id") },
                                onGenerateNewPictureURL = { homeScreenViewModel.showAddCat() },
                                context = this@MainActivity
                            )
                        }
                        composable("QR") {
                            QrCodeScannerScreen(
                                viewModel = qrCodeScannerViewModel,
                                onCatFound = {
                                        id -> navController.navigate("Detail/$id")
                                }
                            )
                        }
                        composable("Settings") {
                            val uiState by settingsViewModel.uiState.collectAsState()
                            SettingsUI.SettingsScreen(viewModel = settingsViewModel, uiState = uiState, onNavigateToOverview = {
                                navController.navigate("Overview")
                            }, context = this@MainActivity)
                        }
                        composable("Detail/{id}") {
                            val id = navController.currentBackStackEntry?.arguments?.getString("id")
                            DetailUI.DetailScreen( viewModel = detailScreenViewModel, context = this@MainActivity, id = id, onNavigateToOverview = {
                                navController.navigate("Overview") {homeScreenViewModel.loadCats()}
                            })
                        }
                    }
                }
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