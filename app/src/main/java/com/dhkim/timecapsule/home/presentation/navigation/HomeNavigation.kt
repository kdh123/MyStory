package com.dhkim.timecapsule.home.presentation.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.timecapsule.home.HomeScreen
import com.dhkim.timecapsule.main.Screen
import com.dhkim.timecapsule.search.domain.Place

fun NavGraphBuilder.homeNavigation(
    onCategorySelected: (Boolean) -> Unit,
    onNavigateToSearch: (Double, Double) -> Unit,
    onInitSavedState: () -> Unit
) {
    composable(Screen.Home.route) {
        val place = it.savedStateHandle.get<Place>("place")

        HomeScreen(
            place = remember {
                place
            },
            showBottomNav = remember {
                onCategorySelected
            },
            onNavigateToSearch = remember {
                onNavigateToSearch
            },
            onInitSavedState = remember {
                onInitSavedState
            }
        )
    }
}