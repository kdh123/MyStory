package com.dhkim.trip.domain.usecase

import com.dhkim.trip.domain.model.Trip
import com.dhkim.trip.domain.repository.TripRepository
import javax.inject.Inject

class UpdateTripUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {

    suspend operator fun invoke(trip: Trip) {
        tripRepository.updateTrip(trip)
    }
}