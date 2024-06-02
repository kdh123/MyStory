package com.dhkim.camera.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.camera.CameraScreen

typealias savedUrl = String
fun NavGraphBuilder.cameraNavigation(
    folderName: String = "",
    onNext: ((savedUrl) -> Unit)? = null
) {
    composable("camera") {
        CameraScreen(folderName, remember {
            onNext
        })
    }
}