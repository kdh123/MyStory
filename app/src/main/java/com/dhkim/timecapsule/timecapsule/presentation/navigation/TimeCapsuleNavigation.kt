package com.dhkim.timecapsule.timecapsule.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.timecapsule.main.Screen
import com.dhkim.timecapsule.timecapsule.TimeCapsuleScreen
import com.dhkim.timecapsule.timecapsule.presentation.AddTimeCapsuleScreen

fun NavGraphBuilder.timeCapsuleNavigation() {
    composable(Screen.TimeCapsule.route) {
        TimeCapsuleScreen()
    }
}

fun NavGraphBuilder.addTimeCapsuleNavigation(
    onNavigateToCamera: () -> Unit,
    onBack: () -> Unit
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