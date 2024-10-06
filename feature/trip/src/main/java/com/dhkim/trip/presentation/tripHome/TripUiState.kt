package com.dhkim.trip.presentation.tripHome

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList

@Stable
data class TripUiState(
    val isLoading: Boolean = true,
    val trips: ImmutableList<TripItem>? = null
)
