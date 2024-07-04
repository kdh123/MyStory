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
import com.dhkim.friend.presentation.changeInfo.ChangeFriendInfoScreen
import com.dhkim.friend.presentation.changeInfo.ChangeFriendInfoSideEffect
import com.dhkim.friend.presentation.changeInfo.ChangeFriendInfoViewModel

const val FRIEND_ROUTE = "friend"
const val CHANGE_FRIEND_INFO_ROUTE = "changeFriendInfo"

fun NavController.navigateToFriend() {
    navigate(FRIEND_ROUTE) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavController.navigateToChangeFriendInfo(userId: String) {
    navigate("$CHANGE_FRIEND_INFO_ROUTE/$userId")
}

fun NavGraphBuilder.friendNavigation(
    onNavigateToChangeInfo: (String) -> Unit,
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
            onNavigateToChangeInfo = onNavigateToChangeInfo,
            onBack = onBack
        )
    }
}

fun NavGraphBuilder.changeFriendInfoNavigation(
    onBack: () -> Unit
) {
    composable("$CHANGE_FRIEND_INFO_ROUTE/{userId}") {
        val viewModel = hiltViewModel<ChangeFriendInfoViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val sideEffect by viewModel.sideEffect.collectAsStateWithLifecycle(initialValue = ChangeFriendInfoSideEffect.None)
        val userId = it.arguments?.getString("userId") ?: ""

        ChangeFriendInfoScreen(
            userId = userId,
            uiState = uiState,
            sideEffect = sideEffect,
            onInit = viewModel::initInfo,
            onEditNickname = viewModel::onEdit,
            onSave = viewModel::editFriendInfo,
            onBack = onBack
        )
    }
}