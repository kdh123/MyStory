package com.dhkim.home.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dhkim.home.presentation.TimeCapsuleScreen
import com.dhkim.home.presentation.TimeCapsuleViewModel
import com.dhkim.home.presentation.add.AddTimeCapsuleScreen
import com.dhkim.home.presentation.add.AddTimeCapsuleViewModel
import com.dhkim.home.presentation.detail.ImageDetailScreen
import com.dhkim.home.presentation.detail.TimeCapsuleDetailAction
import com.dhkim.home.presentation.detail.TimeCapsuleDetailScreen
import com.dhkim.home.presentation.detail.TimeCapsuleDetailViewModel
import com.dhkim.home.presentation.detail.TimeCapsuleOpenScreen
import com.dhkim.home.presentation.more.MoreTimeCapsuleScreen
import com.dhkim.home.presentation.more.MoreTimeCapsuleViewModel
import com.dhkim.location.domain.Place

const val TIME_CAPSULE_MAIN_ROUTE = "mainTimeCapsule"
const val TIME_CAPSULE_OPEN_ROUTE = "timeCapsuleOpen"
const val TIME_CAPSULE_ROUTE = "timeCapsule"
const val ADD_TIME_CAPSULE_ROUTE = "addTimeCapsule"
const val IMAGE_DETAIL_ROUTE = "imageDetail"
const val MORE_TIME_CAPSULE = "moreTimeCapsule"
const val TIME_CAPSULE_DETAIL = "timeCapsuleDetail"

fun NavGraphBuilder.addTimeCapsuleScreen(
    onBack: () -> Unit,
) {
    composable("addTimeCapsule/{friendId}") { backStackEntry ->
        val viewModel = hiltViewModel<AddTimeCapsuleViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val sideEffect = remember {
            viewModel.sideEffect
        }
        val place = backStackEntry.savedStateHandle.get<Place>("place") ?: Place()
        val imageUrl = backStackEntry.savedStateHandle.get<String>("imageUrl") ?: ""
        val friendId = backStackEntry.arguments?.getString("friendId") ?: ""

        AddTimeCapsuleScreen(
            uiState = uiState,
            sideEffect = { sideEffect },
            onAction = remember(viewModel) {
                viewModel::onAction
            },
            imageUrl = imageUrl,
            place = place,
            friendId = friendId.ifBlank { "" },
            onBack = onBack
        )
    }
}

fun NavGraphBuilder.timeCapsuleScreen(
    onNavigateToAdd: () -> Unit,
    onNavigateToOpen: (timeCapsuleId: String, isReceived: Boolean) -> Unit,
    onNavigateToDetail: (timeCapsuleId: String, isReceived: Boolean) -> Unit,
    onNavigateToDetailFromOpen: (timeCapsuleId: String, isReceived: Boolean) -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToSetting: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToMore: () -> Unit,
    onNavigateToImageDetail: (String, String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(startDestination = TIME_CAPSULE_ROUTE, route = TIME_CAPSULE_MAIN_ROUTE) {
        composable(TIME_CAPSULE_ROUTE) {
            val viewModel = hiltViewModel<TimeCapsuleViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val sideEffect = remember {
                {
                    viewModel.sideEffect
                }
            }

            TimeCapsuleScreen(
                uiState = uiState,
                sideEffect = sideEffect,
                modifier = modifier,
                onDeleteTimeCapsule = remember(viewModel) {
                    viewModel::deleteTimeCapsule
                },
                onNavigateToAdd = onNavigateToAdd,
                onNavigateToOpen = onNavigateToOpen,
                onNavigateToDetail = onNavigateToDetail,
                onNavigateToNotification = onNavigateToNotification,
                onNavigateToSetting = onNavigateToSetting,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToMore = onNavigateToMore
            )
        }

        composable("${IMAGE_DETAIL_ROUTE}/{currentIndex}/{images}") {
            val currentIndex = it.arguments?.getString("currentIndex") ?: ""
            val images = it.arguments?.getString("images") ?: ""

            ImageDetailScreen(currentIndex = currentIndex.toInt(), images = images.split(","))
        }

        composable(MORE_TIME_CAPSULE) {
            val viewModel = hiltViewModel<MoreTimeCapsuleViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            MoreTimeCapsuleScreen(
                uiState = uiState,
                onNavigateToDetail = onNavigateToDetail,
                onBack = onBack
            )
        }

        composable("timeCapsuleDetail/{id}/{isReceived}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val isReceived =
                (backStackEntry.arguments?.getString("isReceived") ?: "false").toBoolean()
            val viewModel = hiltViewModel<TimeCapsuleDetailViewModel>()
            viewModel.onAction(action = TimeCapsuleDetailAction.InitParams(id, isReceived))
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val sideEffect = remember {
                viewModel.sideEffect
            }

            TimeCapsuleDetailScreen(
                timeCapsuleId = id,
                isReceived = isReceived,
                uiState = uiState,
                sideEffect = { sideEffect },
                onAction = remember(viewModel) {
                    viewModel::onAction
                },
                onNavigateToImageDetail = onNavigateToImageDetail,
                onBack = onBack
            )
        }

        composable("$TIME_CAPSULE_OPEN_ROUTE/{id}/{isReceived}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val isReceived =
                (backStackEntry.arguments?.getString("isReceived") ?: "false").toBoolean()
            val viewModel = hiltViewModel<TimeCapsuleDetailViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            TimeCapsuleOpenScreen(
                timeCapsuleId = id,
                isReceived = isReceived,
                uiState = uiState,
                onAction = remember(viewModel) {
                    viewModel::onAction
                },
                onNavigateToDetail = onNavigateToDetailFromOpen,
            )
        }
    }
}

fun NavController.navigateToOpenTimeCapsule(id: String, isReceived: Boolean) {
    navigate("$TIME_CAPSULE_OPEN_ROUTE/$id/${isReceived}")
}

fun NavController.navigateToDetail(id: String, isReceived: Boolean) {
    navigate("$TIME_CAPSULE_DETAIL/$id/${isReceived}")
}

fun NavController.navigateToDetailFromOpen(id: String, isReceived: Boolean) {
    navigate("$TIME_CAPSULE_DETAIL/$id/${isReceived}") {
        val currentRoute = currentDestination?.route ?: return@navigate
        popUpTo(currentRoute) {
            inclusive = true
        }
    }
}

fun NavController.navigateToImageDetail(currentIndex: String, images: String) {
    navigate("$IMAGE_DETAIL_ROUTE/$currentIndex/$images")
}

fun NavController.navigateToMore() {
    navigate(MORE_TIME_CAPSULE)
}

fun NavController.navigateToAddTimeCapsule(friendId: String) {
    navigate("$ADD_TIME_CAPSULE_ROUTE/$friendId")
}
