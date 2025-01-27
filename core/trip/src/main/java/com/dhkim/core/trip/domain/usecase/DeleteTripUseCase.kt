package com.dhkim.core.trip.domain.usecase

import com.dhkim.core.trip.domain.repository.TripRepository
import javax.inject.Inject

class DeleteTripUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {

    suspend operator fun invoke(id: String) {
        tripRepository.deleteTrip(id = id)
    }
}