package com.dhkim.timecapsule.timecapsule.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.timecapsule.main.Screen
import com.dhkim.timecapsule.timecapsule.TimeCapsuleScreen
import com.dhkim.timecapsule.timecapsule.presentation.AddTimeCapsuleScreen
import com.dhkim.timecapsule.timecapsule.presentation.TimeCapsuleDetailScreen
import com.dhkim.timecapsule.timecapsule.presentation.TimeCapsuleSideEffect
import com.dhkim.timecapsule.timecapsule.presentation.TimeCapsuleViewModel

fun NavGraphBuilder.timeCapsuleNavigation(modifier: Modifier = Modifier) {
    composable(Screen.TimeCapsule.route) {
        val viewModel = hiltViewModel<TimeCapsuleViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val sideEffect by viewModel.sideEffect.collectAsStateWithLifecycle(TimeCapsuleSideEffect.None)

        TimeCapsuleScreen(
            uiState = uiState,
            modifier = modifier,
            shareTimeCapsule = viewModel::shareTimeCapsule,
            openTimeCapsule = viewModel::openTimeCapsule
        )
    }
}

fun NavGraphBuilder.timeCapsuleDetailNavigation(
    onBack: () -> Unit,
) {
    composable("addTimeCapsule") { backStackEntry ->
        val imageUrl = backStackEntry.savedStateHandle.get<String>("imageUrl") ?: ""

        TimeCapsuleDetailScreen(imageUrl)
    }
}

fun NavGraphBuilder.addTimeCapsuleNavigation(
    onNavigateToCamera: () -> Unit,
    onBack: () -> Unit,
) {
    composable("addTimeCapsule") { backStackEntry ->
        val imageUrl = backStackEntry.savedStateHandle.get<String>("imageUrl") ?: ""

        AddTimeCapsuleScreen(
            imageUrl = imageUrl,
            onNavigateToCamera = onNavigateToCamera,
            onBack = onBack
        )
    }
}