package com.dhkim.timecapsule.timecapsule.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.timecapsule.main.Screen
import com.dhkim.timecapsule.search.domain.Place
import com.dhkim.timecapsule.timecapsule.TimeCapsuleScreen
import com.dhkim.timecapsule.timecapsule.presentation.add.AddTimeCapsuleScreen
import com.dhkim.timecapsule.timecapsule.presentation.detail.TimeCapsuleDetailScreen
import com.dhkim.timecapsule.timecapsule.presentation.TimeCapsuleSideEffect
import com.dhkim.timecapsule.timecapsule.presentation.TimeCapsuleViewModel
import com.dhkim.timecapsule.timecapsule.presentation.add.AddTimeCapsuleSideEffect
import com.dhkim.timecapsule.timecapsule.presentation.add.AddTimeCapsuleViewModel
import com.dhkim.timecapsule.timecapsule.presentation.detail.TimeCapsuleDetailSideEffect
import com.dhkim.timecapsule.timecapsule.presentation.detail.TimeCapsuleDetailViewModel
import com.dhkim.timecapsule.timecapsule.presentation.detail.TimeCapsuleOpenScreen

fun NavGraphBuilder.timeCapsuleNavigation(
    onNavigateToAdd: () -> Unit,
    onNavigateToOpen: (timeCapsuleId: String, isReceived: Boolean) -> Unit,
    onNavigateToDetail: (timeCapsuleId: String, isReceived: Boolean) -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToSetting: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    composable(Screen.TimeCapsule.route) {
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
            onNavigateToProfile = onNavigateToProfile
        )
    }
}

fun NavGraphBuilder.timeCapsuleDetailNavigation(onBack: () -> Unit) {
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
    composable("addTimeCapsule") { backStackEntry ->
        val viewModel = hiltViewModel<AddTimeCapsuleViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val sideEffect by viewModel.sideEffect.collectAsStateWithLifecycle(initialValue = AddTimeCapsuleSideEffect.None)
        val place = backStackEntry.savedStateHandle.get<Place>("place") ?: Place()
        val imageUrl = backStackEntry.savedStateHandle.get<String>("imageUrl") ?: ""

        AddTimeCapsuleScreen(
            uiState = uiState,
            sideEffect = sideEffect,
            imageUrl = imageUrl,
            place = place,
            onSaveTimeCapsule = viewModel::saveTimeCapsule,
            onSetCheckShare = viewModel::setCheckSend,
            onSetCheckLocation = viewModel::setCheckLocation,
            onSetSelectImageIndex = viewModel::setSelectImageIndex,
            onSetOpenDate = viewModel::setOpenDate,
            onTyping = viewModel::typing,
            onCheckSharedFriend = viewModel::checkSharedFriend,
            onQuery = viewModel::onQuery,
            onPlaceClick = viewModel::onPlaceClick,
            onSearchAddress = viewModel::searchAddress,
            onInitPlace = viewModel::initPlace,
            onAddImage = viewModel::addImage,
            onNavigateToCamera = onNavigateToCamera,
            onBack = onBack
        )
    }
}