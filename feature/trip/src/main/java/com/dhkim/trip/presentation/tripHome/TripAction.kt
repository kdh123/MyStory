package com.dhkim.trip.presentation.tripHome

sealed interface TripAction {

    data class DeleteTrip(val tripId: String): TripAction
}