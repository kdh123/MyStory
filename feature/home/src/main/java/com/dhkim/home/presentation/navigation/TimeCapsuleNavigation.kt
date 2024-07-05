package com.dhkim.home.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.location.domain.Place
import com.dhkim.home.presentation.TimeCapsuleScreen
import com.dhkim.home.presentation.TimeCapsuleSideEffect
import com.dhkim.home.presentation.TimeCapsuleViewModel
import com.dhkim.home.presentation.add.AddTimeCapsuleScreen
import com.dhkim.home.presentation.add.AddTimeCapsuleSideEffect
import com.dhkim.home.presentation.add.AddTimeCapsuleViewModel
import com.dhkim.home.presentation.detail.ImageDetailScreen
import com.dhkim.home.presentation.detail.TimeCapsuleDetailScreen
import com.dhkim.home.presentation.detail.TimeCapsuleDetailSideEffect
import com.dhkim.home.presentation.detail.TimeCapsuleDetailViewModel
import com.dhkim.home.presentation.detail.TimeCapsuleOpenScreen
import com.dhkim.home.presentation.more.MoreTimeCapsuleScreen
import com.dhkim.home.presentation.more.MoreTimeCapsuleViewModel

const val TIME_CAPSULE_ROUTE = "timeCapsule"
const val ADD_TIME_CAPSULE_ROUTE = "addTimeCapsule"
const val IMAGE_DETAIL_ROUTE = "imageDetail"
const val MORE_TIME_CAPSULE = "moreTimeCapsule"

fun NavGraphBuilder.imageDetailNavigation() {
    composable("${IMAGE_DETAIL_ROUTE}/{currentIndex}/{images}") {
        val currentIndex = it.arguments?.getString("currentIndex") ?: ""
        val images = it.arguments?.getString("images") ?: ""

        ImageDetailScreen(currentIndex = currentIndex.toInt(), images = images.split(","))
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

fun NavGraphBuilder.moreTimeCapsuleNavigation(
    onNavigateToDetail: (timeCapsuleId: String, isReceived: Boolean) -> Unit,
    onBack: () -> Unit
) {
    composable(MORE_TIME_CAPSULE) {
        val viewModel = hiltViewModel<MoreTimeCapsuleViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        MoreTimeCapsuleScreen(
            uiState = uiState,
            onNavigateToDetail = onNavigateToDetail,
            onBack = onBack
        )
    }
}

fun NavGraphBuilder.timeCapsuleNavigation(
    onNavigateToAdd: () -> Unit,
    onNavigateToOpen: (timeCapsuleId: String, isReceived: Boolean) -> Unit,
    onNavigateToDetail: (timeCapsuleId: String, isReceived: Boolean) -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToSetting: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    composable(TIME_CAPSULE_ROUTE) {
        val viewModel = hiltViewModel<TimeCapsuleViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val sideEffect by viewModel.sideEffect.collectAsStateWithLifecycle(TimeCapsuleSideEffect.None)

        TimeCapsuleScreen(
            uiState = uiState,
            sideEffect = sideEffect,
            modifier = modifier,
            onDeleteTimeCapsule = viewModel::deleteTimeCapsule,
            onNavigateToAdd = onNavigateToAdd,
            onNavigateToOpen = onNavigateToOpen,
            onNavigateToDetail = onNavigateToDetail,
            onNavigateToNotification = onNavigateToNotification,
            onNavigateToSetting = onNavigateToSetting,
            onNavigateToProfile = onNavigateToProfile,
            onNavigateToMore = onNavigateToMore
        )
    }
}

fun NavGraphBuilder.timeCapsuleDetailNavigation(
    onNavigateToImageDetail: (String, String) -> Unit,
    onBack: () -> Unit
) {
    composable("timeCapsuleDetail/{id}/{isReceived}") { backStackEntry ->
        val id = backStackEntry.arguments?.getString("id") ?: ""
        val isReceived = (backStackEntry.arguments?.getString("isReceived") ?: "false").toBoolean()
        val viewModel = hiltViewModel<TimeCapsuleDetailViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val sideEffect by viewModel.sideEffect.collectAsStateWithLifecycle(initialValue = TimeCapsuleDetailSideEffect.None)

        TimeCapsuleDetailScreen(
            timeCapsuleId = id,
            isReceived = isReceived,
            uiState = uiState,
            sideEffect = sideEffect,
            onNavigateToImageDetail = onNavigateToImageDetail,
            onBack = onBack,
            onDelete = viewModel::deleteTImeCapsule,
            init = viewModel::init
        )
    }
}

fun NavGraphBuilder.timeCapsuleOpenNavigation(onNavigateToDetail: (timeCapsuleId: String, isReceived: Boolean) -> Unit) {
    composable("timeCapsuleOpen/{id}/{isReceived}") { backStackEntry ->
        val id = backStackEntry.arguments?.getString("id") ?: ""
        val isReceived = (backStackEntry.arguments?.getString("isReceived") ?: "false").toBoolean()
        val viewModel = hiltViewModel<TimeCapsuleDetailViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        TimeCapsuleOpenScreen(
            timeCapsuleId = id,
            isReceived = isReceived,
            uiState = uiState,
            onNavigateToDetail = onNavigateToDetail,
            init = viewModel::init
        )
    }
}

fun NavGraphBuilder.addTimeCapsuleNavigation(
    onNavigateToCamera: () -> Unit,
    onBack: () -> Unit,
) {
    composable("addTimeCapsule/{friendId}") { backStackEntry ->
        val viewModel = hiltViewModel<AddTimeCapsuleViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val sideEffect by viewModel.sideEffect.collectAsStateWithLifecycle(initialValue = AddTimeCapsuleSideEffect.None)
        val place = backStackEntry.savedStateHandle.get<Place>("place") ?: Place()
        val imageUrl = backStackEntry.savedStateHandle.get<String>("imageUrl") ?: ""
        val friendId = backStackEntry.arguments?.getString("friendId") ?: ""

        AddTimeCapsuleScreen(
            uiState = uiState,
            sideEffect = sideEffect,
            imageUrl = imageUrl,
            place = place,
            friendId = friendId.ifBlank { "" },
            onSaveTimeCapsule = viewModel::saveTimeCapsule,
            onSetCheckShare = viewModel::setCheckShare,
            onSetCheckLocation = viewModel::setCheckLocation,
            onSetSelectImageIndex = viewModel::setSelectImageIndex,
            onSetOpenDate = viewModel::setOpenDate,
            onTyping = viewModel::typing,
            onCheckSharedFriend = viewModel::checkSharedFriend,
            onQuery = viewModel::onQuery,
            onPlaceClick = viewModel::onPlaceClick,
            onSearchAddress = viewModel::searchAddress,
            onInitPlace = viewModel::initPlace,
            onAddFriend = viewModel::addFriend,
            onAddImage = viewModel::addImage,
            onNavigateToCamera = onNavigateToCamera,
            onBack = onBack
        )
    }
}