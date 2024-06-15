package com.dhkim.timecapsule.notification.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.timecapsule.notification.NotificationScreen
import com.dhkim.timecapsule.notification.NotificationViewModel

fun NavController.navigateToNotification() {
    navigate("notification")
}

fun NavGraphBuilder.notificationNavigation(
    onNavigateToTimeCapsule: () -> Unit,
    onBack: () -> Unit
) {
    composable("notification") {
        val viewModel = hiltViewModel<NotificationViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        NotificationScreen(
            uiState = uiState,
            onNavigateToTimeCapsule = onNavigateToTimeCapsule,
            onBack = onBack
        )
    }
}