package com.dhkim.timecapsule.home.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.timecapsule.home.HomeScreen
import com.dhkim.timecapsule.main.Screen

fun NavGraphBuilder.homeNavigation(
    onCategorySelected: (Boolean) -> Unit,
    onNavigateToSearch: (Double, Double) -> Unit
) {
    composable(Screen.Home.route) {
        HomeScreen(
            onCategorySelected = onCategorySelected,
            onNavigateToSearch = onNavigateToSearch
        )
    }
}