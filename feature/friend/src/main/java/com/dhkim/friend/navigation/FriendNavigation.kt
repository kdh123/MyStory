package com.dhkim.friend.navigation

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
import com.dhkim.friend.FriendScreen
import com.dhkim.friend.FriendViewModel
import com.dhkim.friend.changeInfo.ChangeFriendInfoScreen
import com.dhkim.friend.changeInfo.ChangeFriendInfoViewModel
import com.dhkim.ui.Popup
import com.dhkim.user.domain.model.Friend

const val FRIEND_MAIN_ROUTE = "mainFriend"
const val FRIEND_ROUTE = "friend"
const val CHANGE_FRIEND_INFO_ROUTE = "changeFriendInfo"

fun NavGraphBuilder.friendScreen(
    onNavigateToChangeInfo: (Friend) -> Unit,
    onNavigateToAddTimeCapsule: (friendId: String) -> Unit,
    onBack: () -> Unit,
    showPopup: (Popup?) -> Unit,
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
                sideEffect = { sideEffect },
                onAction = remember(viewModel) {
                    viewModel::onAction
                },
                onNavigateToAddTimeCapsule = onNavigateToAddTimeCapsule,
                onNavigateToChangeInfo = onNavigateToChangeInfo,
                onBack = onBack,
                showPopup = showPopup,
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
                sideEffect = { sideEffect },
                initInfo = remember(viewModel) {
                    viewModel::initInfo
                },
                onEditNickname = remember(viewModel) {
                    viewModel::onEdit
                },
                onSave = remember(viewModel) {
                    viewModel::editFriendInfo
                },
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