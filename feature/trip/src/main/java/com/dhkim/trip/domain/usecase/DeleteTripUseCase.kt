package com.dhkim.trip.domain.usecase

import com.dhkim.trip.domain.repository.TripRepository
import javax.inject.Inject

class DeleteTripUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {

    suspend operator fun invoke(id: String) {
        tripRepository.deleteTrip(id = id)
    }
}