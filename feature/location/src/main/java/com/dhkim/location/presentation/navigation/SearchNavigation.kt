package com.dhkim.location.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import com.dhkim.location.domain.Place
import com.dhkim.location.presentation.SearchScreen
import com.dhkim.location.presentation.SearchViewModel
import com.naver.maps.geometry.LatLng

const val SEARCH_ROUTE = "search"

fun NavGraphBuilder.searchScreen(onBack: (Place) -> Unit) {
    composable("$SEARCH_ROUTE/{lat}/{lng}") { backStackEntry ->
        val lat = (backStackEntry.arguments?.getString("lat") ?: "0.0").toDouble()
        val lng = (backStackEntry.arguments?.getString("lng") ?: "0.0").toDouble()

        val viewModel = hiltViewModel<SearchViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val searchResult = uiState.places.collectAsLazyPagingItems()

        SearchScreen(
            uiState = uiState,
            searchResult = searchResult,
            latLng = LatLng(lat, lng),
            onSetCurrentLocation = viewModel::setCurrentLocation,
            onQuery = viewModel::onQuery,
            onBack = onBack
        )
    }
}

fun NavController.navigateToSearch(lat: Double, lng: Double) {
    navigate("$SEARCH_ROUTE/$lat/$lng")
}