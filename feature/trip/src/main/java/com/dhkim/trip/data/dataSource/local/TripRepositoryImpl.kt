package com.dhkim.trip.data.dataSource.local

import com.dhkim.trip.domain.TripRepository
import com.dhkim.trip.domain.model.Trip
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TripRepositoryImpl @Inject constructor(
    private val localDataSource: TripLocalDataSource
) : TripRepository {

    override fun getAllTrip(): Flow<List<Trip>> {
        return localDataSource.getAllTrip()
    }

    override fun getTrip(id: String): Trip? {
        return localDataSource.getTrip(id)
    }

    override fun saveTrip(trip: Trip) {
        localDataSource.saveTrip(trip)
    }

    override fun updateTrip(trip: Trip) {
        localDataSource.updateTrip(trip)
    }

    override fun deleteTrip(id: String) {
        localDataSource.deleteTrip(id)
    }
}