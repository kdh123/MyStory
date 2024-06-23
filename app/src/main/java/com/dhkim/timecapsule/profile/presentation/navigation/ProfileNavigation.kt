package com.dhkim.timecapsule.profile.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.timecapsule.main.Screen
import com.dhkim.timecapsule.profile.presentation.ProfileScreen
import com.dhkim.timecapsule.profile.presentation.ProfileSideEffect
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
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val sideEffect by viewModel.sideEffect.collectAsStateWithLifecycle(initialValue = ProfileSideEffect.None)

        ProfileScreen(
            uiState = uiState,
            sideEffect = sideEffect,
            onQuery = viewModel::onQuery,
            onSearchUser = viewModel::searchUser,
            onAddFriend = viewModel::addFriend,
            onAcceptFriend = viewModel::acceptFriend,
            onDeleteFriend = viewModel::deleteFriend,
            onBack = onBack
        )
    }
}