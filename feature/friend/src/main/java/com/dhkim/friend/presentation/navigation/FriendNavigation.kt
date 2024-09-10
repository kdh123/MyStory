package com.dhkim.friend.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dhkim.friend.presentation.FriendScreen
import com.dhkim.friend.presentation.FriendViewModel
import com.dhkim.friend.presentation.changeInfo.ChangeFriendInfoScreen
import com.dhkim.friend.presentation.changeInfo.ChangeFriendInfoViewModel
import com.dhkim.user.domain.Friend

const val FRIEND_MAIN_ROUTE = "mainFriend"
const val FRIEND_ROUTE = "friend"
const val CHANGE_FRIEND_INFO_ROUTE = "changeFriendInfo"

fun NavGraphBuilder.friendScreen(
    onNavigateToChangeInfo: (Friend) -> Unit,
    onAddTimeCapsule: (friendId: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(startDestination = FRIEND_ROUTE, route = FRIEND_MAIN_ROUTE) {
        composable(FRIEND_ROUTE) {
            val viewModel = hiltViewModel<FriendViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val sideEffect = remember {
                viewModel.sideEffect
            }

            FriendScreen(
                uiState = uiState,
                sideEffect = sideEffect,
                onQuery = viewModel::onQuery,
                onSearchUser = viewModel::searchUser,
                onAddFriend = viewModel::addFriend,
                onAcceptFriend = viewModel::acceptFriend,
                onDeleteFriend = viewModel::deleteFriend,
                onAddTimeCapsule = onAddTimeCapsule,
                onCreateCode = viewModel::createCode,
                onNavigateToChangeInfo = onNavigateToChangeInfo,
                onBack = onBack,
                modifier = modifier
            )
        }

        composable("$CHANGE_FRIEND_INFO_ROUTE/{id}/{nickname}/{profileImage}/{uuid}/{isPending}") {
            val id = it.arguments?.getString("id") ?: ""
            val nickname = it.arguments?.getString("nickname") ?: ""
            val profileImage = it.arguments?.getString("profileImage") ?: ""
            val uuid = it.arguments?.getString("uuid") ?: ""
            val isPending = it.arguments?.getString("isPending") ?: ""
            val friend = Friend(id, nickname, profileImage, uuid, isPending.toBoolean())

            val viewModel = hiltViewModel<ChangeFriendInfoViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val sideEffect = remember {
                viewModel.sideEffect
            }

            ChangeFriendInfoScreen(
                friend = friend,
                uiState = uiState,
                sideEffect = sideEffect,
                initInfo = viewModel::initInfo,
                onEditNickname = viewModel::onEdit,
                onSave = viewModel::editFriendInfo,
                onBack = onBack
            )
        }
    }
}

fun NavController.navigateToFriend() {
    navigate(FRIEND_ROUTE) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavController.navigateToChangeFriendInfo(friend: Friend) {
    friend.run {
        navigate("$CHANGE_FRIEND_INFO_ROUTE/$id/$nickname/$profileImage/$uuid/$isPending")
    }
}