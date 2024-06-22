package com.dhkim.timecapsule.profile.presentation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.timecapsule.main.Screen
import com.dhkim.timecapsule.profile.presentation.ProfileScreen
import com.dhkim.timecapsule.profile.presentation.ProfileViewModel

fun NavController.navigateToProfile() {
    navigate(Screen.Profile.route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavGraphBuilder.profileNavigation(
    onBack: () -> Unit
) {
    composable(Screen.Profile.route) {
        val viewModel = hiltViewModel<ProfileViewModel>()

        ProfileScreen(
            onDeleteFriend = viewModel::deleteFriend,
            onBack = onBack
        )
    }
}