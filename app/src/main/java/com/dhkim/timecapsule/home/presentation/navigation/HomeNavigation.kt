package com.dhkim.timecapsule.home.presentation.navigation

import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.timecapsule.home.HomeScreen
import com.dhkim.timecapsule.home.presentation.HomeSideEffect
import com.dhkim.timecapsule.home.presentation.HomeViewModel
import com.dhkim.timecapsule.main.Screen
import com.dhkim.timecapsule.search.domain.Place

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.homeNavigation(
    scaffoldState: BottomSheetScaffoldState,
    onNavigateToSearch: (Double, Double) -> Unit,
    onNavigateToAdd: (Place) -> Unit,
    onHideBottomNav: (Place?) -> Unit,
    onInitSavedState: () -> Unit
) {
    composable(Screen.Home.route) {
        val viewModel = hiltViewModel<HomeViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val sideEffect by viewModel.sideEffect.collectAsStateWithLifecycle(initialValue = HomeSideEffect.None)
        val place = it.savedStateHandle.get<Place>("place")

        HomeScreen(
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