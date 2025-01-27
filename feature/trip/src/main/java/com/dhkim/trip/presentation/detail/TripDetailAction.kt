package com.dhkim.trip.presentation.detail

import com.dhkim.core.trip.domain.model.Trip
import com.dhkim.core.trip.domain.model.TripImage

sealed interface TripDetailAction {

    data class InitTrip(val tripId: String): TripDetailAction
    data class LoadImages(val tripId: String, val images: List<TripImage>): TripDetailAction
    data class UpdateTrip(val trip: Trip): TripDetailAction
    data class DeleteTrip(val tripId: String): TripDetailAction
    data class DeleteImage(val tripId: String, val imageId: String): TripDetailAction
    data class SelectDate(val selectedIndex: Int): TripDetailAction
}