package com.dhkim.core.trip.domain.usecase

import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class DeleteTripImageUseCase @Inject constructor(
    private val getTripUseCase: GetTripUseCase,
    private val updateTripUseCase: UpdateTripUseCase
) {

    suspend operator fun invoke(tripId: String, imageId: String) {
        val trip = getTripUseCase(tripId).firstOrNull() ?: return
        val updatedImages = trip.images.filter { it.id != imageId }
        updateTripUseCase(trip.copy(images = updatedImages))
    }
}