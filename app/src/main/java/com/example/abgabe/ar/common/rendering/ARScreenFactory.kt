package com.example.abgabe.ar.common.rendering

import android.content.Context
import com.example.abgabe.ar.common.helpers.DisplayRotationHelper
import com.example.abgabe.ar.common.helpers.SnackbarHelper
import com.example.abgabe.ar.common.helpers.TrackingStateHelper
import com.example.abgabe.ui.views.ARScreen
import com.google.ar.core.Anchor
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Session

class ARScreenFactory {
    fun createARScreen(context: Context): ARScreen {
        val displayRotationHelper = DisplayRotationHelper(context)
        val backgroundRenderer = BackgroundRenderer()
        val augmentedImageRenderer = AugmentedImageRenderer()
        val trackingStateHelper = TrackingStateHelper(null)
        val messageSnackbarHelper = SnackbarHelper()
        val session = Session(context)
        val augmentedImageMap: MutableMap<Int, Pair<AugmentedImage, Anchor>> = mutableMapOf()

        return ARScreen(
            context,
            displayRotationHelper,
            backgroundRenderer,
            augmentedImageRenderer,
            trackingStateHelper,
            messageSnackbarHelper,
            session,
            augmentedImageMap
        )
    }
}