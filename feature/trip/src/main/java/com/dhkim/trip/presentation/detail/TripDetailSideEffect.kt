package com.dhkim.trip.presentation.detail

sealed interface TripDetailSideEffect {

    data class LoadImages(val startDate: String, val endDate: String): TripDetailSideEffect
}