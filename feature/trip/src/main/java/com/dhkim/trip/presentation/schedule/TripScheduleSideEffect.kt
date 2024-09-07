package com.dhkim.trip.presentation.schedule

sealed interface TripScheduleSideEffect {

    data object Complete: TripScheduleSideEffect
}