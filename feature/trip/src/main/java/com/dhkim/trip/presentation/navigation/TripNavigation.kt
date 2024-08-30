package com.dhkim.trip.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dhkim.trip.presentation.detail.TripDetailScreen
import com.dhkim.trip.presentation.detail.TripDetailViewModel
import com.dhkim.trip.presentation.tripHome.TripScreen
import com.dhkim.trip.presentation.tripHome.TripViewModel
import com.dhkim.trip.presentation.schedule.TripScheduleScreen
import com.dhkim.trip.presentation.schedule.TripScheduleViewModel

const val TRIP_ROUTE = "trip"
const val TRIP_SCHEDULE_ROUTE = "trip_schedule"
const val TRIP_DETAIL_ROUTE = "trip_route"

fun NavGraphBuilder.tripNavigation(
    modifier: Modifier = Modifier,
    onNavigateToSchedule: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    composable(TRIP_ROUTE) {
        val viewModel = hiltViewModel<TripViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        TripScreen(
            uiState = uiState,
            onAction = viewModel::onAction,
            modifier = modifier,
            onNavigateToSchedule = onNavigateToSchedule,
            onNavigateToDetail = onNavigateToDetail
        )
    }
}

fun NavController.navigateToTripSchedule() {
    navigate(TRIP_SCHEDULE_ROUTE)
}

fun NavGraphBuilder.tripScheduleNavigation(
    onBack: () -> Unit
) {
    composable(TRIP_SCHEDULE_ROUTE) {
        val viewModel = hiltViewModel<TripScheduleViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val sideEffect = remember {
            viewModel.sideEffect
        }

        TripScheduleScreen(
            uiState = uiState,
            sideEffect = sideEffect,
            onAction = viewModel::onAction,
            onBack = onBack
        )
    }
}

fun NavController.navigateToTripDetail(tripId: String) {
    navigate("$TRIP_DETAIL_ROUTE/$tripId")
}

fun NavGraphBuilder.tripDetailNavigation(
    onBack: () -> Unit
) {
    composable("$TRIP_DETAIL_ROUTE/{tripId}") {
        val tripId = it.arguments?.getString("tripId") ?: ""
        val viewModel = hiltViewModel<TripDetailViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val sideEffect = remember {
            viewModel.sideEffect
        }

        TripDetailScreen(
            tripId = tripId,
            uiState = uiState,
            sideEffect = sideEffect,
            onAction = viewModel::onAction,
            onBack = onBack
        )
    }
}