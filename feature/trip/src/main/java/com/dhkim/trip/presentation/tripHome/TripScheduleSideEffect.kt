package com.dhkim.trip.presentation.tripHome

sealed interface TripScheduleSideEffect {

    data object Complete: TripScheduleSideEffect
}