package com.dhkim.domain.usecase

import com.dhkim.domain.model.Trip
import com.dhkim.domain.repository.TripRepository
import javax.inject.Inject

class SaveTripUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {

    suspend operator fun invoke(trip: Trip) {
        tripRepository.saveTrip(trip)
    }
}