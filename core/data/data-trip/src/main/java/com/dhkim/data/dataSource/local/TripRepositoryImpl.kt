package com.dhkim.data.dataSource.local

import com.dhkim.domain.model.Trip
import com.dhkim.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class TripRepositoryImpl @Inject constructor(
    private val localDataSource: TripLocalDataSource
) : TripRepository {

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