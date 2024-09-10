package com.dhkim.map.presentation.navigation

import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.location.domain.Place
import com.dhkim.map.presentation.MapScreen
import com.dhkim.map.presentation.MapViewModel

const val MAP_ROUTE = "map"

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.mapScreen(
    scaffoldState: BottomSheetScaffoldState,
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
            sideEffect = sideEffect,
            scaffoldState = scaffoldState,
            place = remember {
                place
            },
            onSelectPlace = viewModel::selectPlace,
            onSearchPlaceByQuery = viewModel::searchPlacesByKeyword,
            onSearchPlaceByCategory = viewModel::searchPlacesByCategory,
            onCloseSearch = viewModel::closeSearch,
            onNavigateToSearch = remember {
                onNavigateToSearch
            },
            onHideBottomNav = remember {
                onHideBottomNav
            },
            onInitSavedState = remember {
                onInitSavedState
            },
            onNavigateToAddScreen = remember {
                onNavigateToAdd
            }
        )
    }
}