package com.dhkim.trip.domain

import com.dhkim.trip.domain.model.Trip
import kotlinx.coroutines.flow.Flow

interface TripRepository {

    fun getAllTrip(): Flow<List<Trip>>
    fun getTrip(id: String): Trip?
    fun saveTrip(trip: Trip)
    fun updateTrip(trip: Trip)
    fun deleteTrip(id: String)
}