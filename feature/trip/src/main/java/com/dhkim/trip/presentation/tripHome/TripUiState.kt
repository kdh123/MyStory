package com.dhkim.trip.presentation.tripHome

import androidx.compose.runtime.Stable
import com.dhkim.trip.domain.model.Trip
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class TripUiState(
    val isLoading: Boolean = false,
    val prevTrips: ImmutableList<Trip> = persistentListOf(),
    val nextTrips: ImmutableList<Trip> = persistentListOf()
)
