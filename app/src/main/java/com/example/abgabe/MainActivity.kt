package com.example.abgabe

import android.content.pm.PackageManager
import android.os.Bundle
import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.abgabe.ar.common.rendering.ARScreenFactory
import com.example.abgabe.data.local.AppDatabase
import com.example.abgabe.data.remote.CatGenerator
import com.example.abgabe.ui.theme.AbgabeTheme
import com.example.abgabe.ui.views.ARScreen
import com.example.abgabe.ui.views.DetailScreen
import com.example.abgabe.ui.views.CatOverviewScreen
import com.example.abgabe.ui.views.RandomCatScreen
import com.example.abgabe.ui.views.SettingsScreen

class MainActivity : ComponentActivity() {

    // ViewModels
    private val homeScreenViewModel: CatOverviewScreen by viewModels()
    private val detailScreenViewModel: DetailScreen by viewModels()
    private val settingsScreenViewModel: SettingsScreen by viewModels()
    private val randomCatScreen: RandomCatScreen by viewModels()
    private val catGenerator = CatGenerator()

    // AR
    private lateinit var arScreen: ARScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "cat-db-name")
            .build()

        /*
        // Kameraberechtigung zur Laufzeit anfordern
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
        }

        val arScreenFactory = ARScreenFactory()
        arScreen = arScreenFactory.createARScreen(this)


         */
        //setContentView(R.layout.activity_main)
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
                            homeScreenViewModel.HomeScreen(
                                onNavigateToAR = {
                                    navController.navigate("AR")
                                },
                                onNavigateToDatabase = {
                                    navController.navigate("Database")
                                },
                                onNavigateToSettings = {
                                    navController.navigate("Settings")
                                },
                                onNavigateToDetail = { id ->
                                    navController.navigate("Detail/$id")
                                },
                                catGenerator = catGenerator,
                                catDatabase = db
                            )
                        }
                        composable("AR") {
                            AndroidView(
                                factory = { arScreen },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        composable("Database") {
                            randomCatScreen.DisplayCatJson(db, catGenerator)
                        }
                        composable("Settings") {
                            settingsScreenViewModel.ClearDatabase(db = db)
                        }
                        composable("Detail/{id}") {
                            val id = navController.currentBackStackEntry?.arguments?.getString("id")
                            detailScreenViewModel.DetailScreen(id = id, db = db)
                        }
                    }
                }
                /*
                FeatureThatRequiresCameraPermission()
                 */
            }
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