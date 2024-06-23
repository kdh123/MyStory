package com.dhkim.timecapsule.friend.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.timecapsule.main.Screen
import com.dhkim.timecapsule.friend.presentation.ProfileScreen
import com.dhkim.timecapsule.friend.presentation.FriendSideEffect
import com.dhkim.timecapsule.friend.presentation.FriendViewModel

fun NavController.navigateToFriend() {
    navigate(Screen.Friend.route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavGraphBuilder.friendNavigation(
    onBack: () -> Unit
) {
    composable(Screen.Friend.route) {
        val viewModel = hiltViewModel<FriendViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val sideEffect by viewModel.sideEffect.collectAsStateWithLifecycle(initialValue = FriendSideEffect.None)

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