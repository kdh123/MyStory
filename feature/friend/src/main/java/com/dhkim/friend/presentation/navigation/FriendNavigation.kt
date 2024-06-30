package com.dhkim.friend.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.friend.presentation.FriendSideEffect
import com.dhkim.friend.presentation.FriendViewModel
import com.dhkim.friend.presentation.ProfileScreen

const val FRIEND_ROUTE = "friend"

fun NavController.navigateToFriend() {
    navigate(FRIEND_ROUTE) {
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
    composable(FRIEND_ROUTE) {
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