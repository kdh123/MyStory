package com.dhkim.core.trip.data.dataSource.local

import com.dhkim.core.trip.domain.model.Trip
import kotlinx.coroutines.flow.Flow

internal interface TripLocalDataSource {

    fun getAllTrip(): Flow<List<Trip>>
    fun getTrip(id: String): Flow<Trip?>
    suspend fun saveTrip(trip: Trip)
    suspend fun updateTrip(trip: Trip)
    suspend fun deleteTrip(id: String)
}