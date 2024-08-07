package com.dhkim.trip.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.trip.presentation.TripScreen
import com.dhkim.trip.presentation.TripViewModel

const val TRIP_ROUTE = "trip"

fun NavGraphBuilder.tripNavigation(
    modifier: Modifier = Modifier
) {

    composable(TRIP_ROUTE) {
        val viewModel = hiltViewModel<TripViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        TripScreen(
            uiState = uiState,
            modifier = modifier
        )
    }
}