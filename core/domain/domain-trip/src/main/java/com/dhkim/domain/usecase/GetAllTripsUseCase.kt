package com.dhkim.domain.usecase

import com.dhkim.domain.model.Trip
import com.dhkim.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTripsUseCase @Inject constructor(
    private val tripRepository: TripRepository
) {

    operator fun invoke(): Flow<List<Trip>> {
        return tripRepository.getAllTrip()
    }
}