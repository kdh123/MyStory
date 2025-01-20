package com.dhkim.map.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.location.domain.model.Place
import com.dhkim.map.presentation.MapScreen
import com.dhkim.map.presentation.MapViewModel

const val MAP_ROUTE = "map"

fun NavGraphBuilder.mapScreen(
    onNavigateToSearch: (Double, Double) -> Unit,
    onNavigateToAdd: (Place) -> Unit,
    onHideBottomNav: (Place?) -> Unit,
    onInitSavedState: () -> Unit
) {
    composable(MAP_ROUTE) {
        val viewModel = hiltViewModel<MapViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val sideEffect = remember {
            viewModel.sideEffect
        }
        val place = it.savedStateHandle.get<Place>("place")

        MapScreen(
            uiState = uiState,
            sideEffect = { sideEffect },
            place = { place },
            onAction = remember(viewModel) {
                viewModel::onAction
            },
            onNavigateToSearch = onNavigateToSearch,
            onHideBottomNav = onHideBottomNav,
            onInitSavedState = onInitSavedState,
            onNavigateToAddScreen = onNavigateToAdd
        )
    }
}