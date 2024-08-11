package com.dhkim.trip.presentation.detail

import com.dhkim.trip.domain.model.Trip

sealed interface TripDetailAction {

    data class LoadTrip(val tripId: String): TripDetailAction
    data class UpdateTrip(val trip: Trip): TripDetailAction
    data class DeleteTrip(val tripId: String): TripDetailAction
}