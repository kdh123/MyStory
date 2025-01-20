package com.dhkim.trip.domain.usecase

import com.dhkim.trip.domain.repository.TripRepository
import javax.inject.Inject

class LoadTripImagesUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {

    suspend operator fun invoke(tripId: String) {
        tripRepository.getTrip(id = tripId)
    }

}