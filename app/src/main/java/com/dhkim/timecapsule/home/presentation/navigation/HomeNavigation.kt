package com.dhkim.timecapsule.home.presentation.navigation

import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.timecapsule.home.HomeScreen
import com.dhkim.timecapsule.main.Screen
import com.dhkim.timecapsule.search.domain.Place

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.homeNavigation(
    scaffoldState: BottomSheetScaffoldState,
    onNavigateToSearch: (Double, Double) -> Unit,
    onSelectPlace: (Place?) -> Unit,
    onInitSavedState: () -> Unit
) {
    composable(Screen.Home.route) {
        val place = it.savedStateHandle.get<Place>("place")

        HomeScreen(
            scaffoldState = scaffoldState,
            place = remember {
                place
            },
            onNavigateToSearch = remember {
                onNavigateToSearch
            },
            onSelectPlace = remember {
                onSelectPlace
            },
            onInitSavedState = remember {
                onInitSavedState
            }
        )
    }
}