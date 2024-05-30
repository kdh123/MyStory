package com.dhkim.timecapsule.timecapsule.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.timecapsule.main.Screen
import com.dhkim.timecapsule.timecapsule.TimeCapsuleScreen

fun NavGraphBuilder.timeCapsuleNavigation() {
    composable(Screen.TimeCapsule.route) {
        TimeCapsuleScreen()
    }
}