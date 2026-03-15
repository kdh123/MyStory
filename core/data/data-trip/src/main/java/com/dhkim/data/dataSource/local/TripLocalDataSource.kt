package com.dhkim.data.dataSource.local

import com.dhkim.domain.model.Trip
import kotlinx.coroutines.flow.Flow

interface TripLocalDataSource {

    fun getAllTrip(): Flow<List<Trip>>
    fun getTrip(id: String): Flow<Trip?>
    suspend fun saveTrip(trip: Trip)
    suspend fun updateTrip(trip: Trip)
    suspend fun deleteTrip(id: String)
}