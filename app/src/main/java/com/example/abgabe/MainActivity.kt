package com.example.abgabe

import android.content.pm.PackageManager
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLSurfaceView.Renderer
import android.os.Bundle
import android.util.Log
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
import com.example.abgabe.ar.common.helpers.DisplayRotationHelper
import com.example.abgabe.ar.common.helpers.SnackbarHelper
import com.example.abgabe.ar.common.helpers.TrackingStateHelper
import com.example.abgabe.ar.common.rendering.AugmentedImageRenderer
import com.example.abgabe.ar.common.rendering.BackgroundRenderer
import com.example.abgabe.data.local.AppDatabase
import com.example.abgabe.data.remote.CatGenerator
import com.example.abgabe.ui.theme.AbgabeTheme
import com.example.abgabe.ui.views.DetailScreen
import com.example.abgabe.ui.views.CatOverviewScreen
import com.example.abgabe.ui.views.RandomCatScreen
import com.example.abgabe.ui.views.SettingsScreen
import com.google.ar.core.Anchor
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Frame
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import java.io.IOException
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

private lateinit var displayRotationHelper: DisplayRotationHelper
private lateinit var backgroundRenderer: BackgroundRenderer
private lateinit var augmentedImageRenderer: AugmentedImageRenderer
private var trackingStateHelper = TrackingStateHelper(null)
private val messageSnackbarHelper = SnackbarHelper()
private lateinit var session: Session
private val augmentedImageMap: MutableMap<Int, Pair<AugmentedImage, Anchor>> = mutableMapOf()


class MainActivity : ComponentActivity(), Renderer {
    private val homeScreenViewModel: CatOverviewScreen by viewModels()
    private val detailScreenViewModel: DetailScreen by viewModels()
    private val settingsScreenViewModel: SettingsScreen by viewModels()
    private val randomCatScreen: RandomCatScreen by viewModels()
    private val catGenerator = CatGenerator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "cat-db-name")
            .build()

        // Kameraberechtigung zur Laufzeit anfordern
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
        }

        displayRotationHelper = DisplayRotationHelper(this)
        backgroundRenderer = BackgroundRenderer()
        augmentedImageRenderer = AugmentedImageRenderer()
        session = Session(this)
        trackingStateHelper = TrackingStateHelper(this)

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
                                factory = { context ->
                                    GLSurfaceView(context).apply {
                                        preserveEGLContextOnPause = true
                                        setEGLContextClientVersion(2)
                                        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
                                        setRenderer(this@MainActivity)
                                        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                                    }
                                }
                            )
                        }
                        composable("Database") {
                            randomCatScreen.DisplayCatJson(db, catGenerator)
                        }
                        composable("Settings") {
                            settingsScreenViewModel.ClearDatabase(db = db)
                        }
                        composable("Detail/{id}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id")
                            detailScreenViewModel.DetailScreen(id = id!!, db = db)
                        }
                    }
                }
                /*
                FeatureThatRequiresCameraPermission()
                 */
            }
        }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)


        // Prepare the rendering objects. This involves reading shaders, so may throw an IOException.
        try {
            // Create the texture and pass it to ARCore session to be filled during update().
            backgroundRenderer.createOnGlThread(this)
            augmentedImageRenderer.createOnGlThread(this)
        } catch (e: IOException) {
            Log.e(
                "Abgabe",
                "Failed to read an asset file",
                e
            )
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        displayRotationHelper.onSurfaceChanged(width, height)
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        // Clear screen to notify driver it should not load any pixels from previous frame.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        if (session == null) {
            return
        }


        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        displayRotationHelper.updateSessionIfNeeded(session)

        try {
            session.setCameraTextureName(backgroundRenderer.textureId)

            // Obtain the current frame from ARSession. When the configuration is set to
            // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
            // camera framerate.
            val frame: Frame = session.update()
            val camera = frame.camera

            // Keep the screen unlocked while tracking, but allow it to lock when tracking stops.
            trackingStateHelper.updateKeepScreenOnFlag(camera.trackingState)

            // If frame is ready, render camera preview image to the GL surface.
            backgroundRenderer.draw(frame)

            // Get projection matrix.
            val projmtx = FloatArray(16)
            camera.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f)

            // Get camera matrix and draw.
            val viewmtx = FloatArray(16)
            camera.getViewMatrix(viewmtx, 0)

            // Compute lighting from average intensity of the image.
            val colorCorrectionRgba = FloatArray(4)
            frame.lightEstimate.getColorCorrection(colorCorrectionRgba, 0)

            // Visualize augmented images.
            drawAugmentedImages(frame, projmtx, viewmtx, colorCorrectionRgba)
        } catch (t: Throwable) {
            // Avoid crashing the application due to unhandled exceptions.
            Log.e(
                "Abgabe",
                "Exception on the OpenGL thread",
                t
            )
        }
    }

    private fun drawAugmentedImages(
        frame: Frame,
        projmtx: FloatArray,
        viewmtx: FloatArray,
        colorCorrectionRgba: FloatArray
    ) {
        val updatedAugmentedImages = frame.getUpdatedTrackables(AugmentedImage::class.java)

        // Iterate to update augmentedImageMap, remove elements we cannot draw.
        for (augmentedImage in updatedAugmentedImages) {
            when (augmentedImage.trackingState) {
                TrackingState.PAUSED -> {
                    // When an image is in PAUSED state, but the camera is not PAUSED, it has been detected,
                    // but not yet tracked.
                    val text = String.format("Detected Image %d", augmentedImage.index)
                    messageSnackbarHelper.showMessage(this, text)
                }

                TrackingState.TRACKING -> {
                    //TODO
                    // Have to switch to UI Thread to update View.
                    /*
                    runOnUiThread {
                        fitToScanView.visibility = View.GONE
                    }
                    */

                    // Create a new anchor for newly found images.
                    if (!augmentedImageMap.containsKey(augmentedImage.index)) {
                        val centerPoseAnchor = augmentedImage.createAnchor(augmentedImage.centerPose)
                        augmentedImageMap[augmentedImage.index] = Pair(augmentedImage, centerPoseAnchor)
                    }
                }

                TrackingState.STOPPED -> {
                    augmentedImageMap.remove(augmentedImage.index)
                }

                else -> {
                }
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