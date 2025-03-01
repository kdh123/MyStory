package com.dhkim.testing

import com.dhkim.core.trip.data.dataSource.local.TripLocalDataSource
import com.dhkim.core.trip.domain.model.Trip
import com.dhkim.core.trip.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow

class FakeTripRepository(
    private val localDataSource: TripLocalDataSource = FakeTripLocalDataSource()
): TripRepository {

    override fun getAllTrip(): Flow<List<Trip>> {
        return localDataSource.getAllTrip()
    }

    override fun getTrip(id: String): Flow<Trip?> {
        return localDataSource.getTrip(id)
    }

    override suspend fun saveTrip(trip: Trip) {
        localDataSource.saveTrip(trip)
    }

    override suspend fun updateTrip(trip: Trip) {
        localDataSource.updateTrip(trip)
    }

    override suspend fun deleteTrip(id: String) {
        localDataSource.deleteTrip(id)
    }
}