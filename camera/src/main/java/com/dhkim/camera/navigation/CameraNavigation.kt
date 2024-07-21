package com.dhkim.camera.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.camera.CameraScreen
import com.dhkim.camera.CameraSideEffect
import com.dhkim.camera.CameraViewModel

typealias savedUrl = String

fun NavGraphBuilder.cameraNavigation(
    folderName: String = "",
    onNext: ((savedUrl) -> Unit)? = null,
    onBack: (() -> Unit)? = null,
) {
    composable("camera") {
        val viewModel = hiltViewModel<CameraViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val sideEffect by viewModel.sideEffect.collectAsStateWithLifecycle(initialValue = CameraSideEffect.None)

        CameraScreen(
            uiState = uiState,
            sideEffect = sideEffect,
            folderName = folderName,
            onSavingPhoto = viewModel::onSavingPhoto,
            onSavedPhoto = viewModel::onSavedPhoto,
            onTakePhoto = viewModel::onTakePhoto,
            onNext = onNext,
            onBack = onBack
        )
    }
}