package com.dhkim.core.trip.domain.usecase

import com.dhkim.core.trip.domain.model.Trip
import com.dhkim.core.trip.domain.repository.TripRepository
import javax.inject.Inject

class SaveTripUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {

    suspend operator fun invoke(trip: Trip) {
        tripRepository.saveTrip(trip)
    }
}