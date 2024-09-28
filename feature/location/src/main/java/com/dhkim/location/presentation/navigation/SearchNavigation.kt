package com.dhkim.location.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.paging.compose.collectAsLazyPagingItems
import com.dhkim.location.domain.Place
import com.dhkim.location.presentation.SearchScreen
import com.dhkim.location.presentation.SearchViewModel

const val SEARCH_ROUTE = "search"

fun NavGraphBuilder.searchScreen(onBack: (Place) -> Unit) {
    composable(
        route = "$SEARCH_ROUTE/{lat}/{lng}",
        arguments = listOf(
            navArgument("lat") {
                defaultValue = "37.572389"
            },
            navArgument("lng") {
                defaultValue = "126.9769117"
            }
        )
    ) {
        val viewModel = hiltViewModel<SearchViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val searchResult = uiState.places.collectAsLazyPagingItems()

        SearchScreen(
            uiState = uiState,
            searchResult = searchResult,
            onQuery = viewModel::onQuery,
            onBack = onBack
        )
    }
}

fun NavController.navigateToSearch(lat: Double, lng: Double) {
    navigate("$SEARCH_ROUTE/$lat/$lng")
}