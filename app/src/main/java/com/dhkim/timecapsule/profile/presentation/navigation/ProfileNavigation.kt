package com.dhkim.timecapsule.profile.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.timecapsule.main.Screen
import com.dhkim.timecapsule.profile.presentation.ProfileScreen

fun NavGraphBuilder.profileNavigation(
    onBack: () -> Unit
) {
    composable(Screen.Profile.route) {
        ProfileScreen(onBack = onBack)
    }
}