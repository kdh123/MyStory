package com.dhkim.timecapsule.setting.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.timecapsule.setting.presentation.SettingScreen
import com.dhkim.timecapsule.setting.presentation.SettingViewModel

const val SETTING_ROUTE = "setting"

fun NavController.navigateToSetting() {
    navigate(SETTING_ROUTE)
}

fun NavGraphBuilder.settingNavigation(onBack: () -> Unit) {
    composable(SETTING_ROUTE) {
        val viewModel = hiltViewModel<SettingViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        SettingScreen(
            uiState = uiState,
            onNotificationChanged = viewModel::changeNotificationSetting,
            onBack = onBack
        )
    }
}