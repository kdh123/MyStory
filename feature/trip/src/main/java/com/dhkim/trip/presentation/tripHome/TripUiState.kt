package com.dhkim.trip.presentation.tripHome

import androidx.compose.runtime.Stable
import com.dhkim.trip.domain.model.Trip
import kotlinx.collections.immutable.ImmutableList

@Stable
data class TripUiState(
    val isLoading: Boolean = true,
    val prevTrips: ImmutableList<Trip>? = null,
    val nextTrips: ImmutableList<Trip>? = null
)
