package com.dhkim.trip.domain

import com.dhkim.trip.domain.model.Trip
import kotlinx.coroutines.flow.Flow

interface TripRepository {

    suspend fun getAllTrip(): Flow<List<Trip>>
    suspend fun getTrip(id: String): Flow<Trip?>
    suspend fun saveTrip(trip: Trip)
    suspend fun updateTrip(trip: Trip)
    suspend fun deleteTrip(id: String)
}