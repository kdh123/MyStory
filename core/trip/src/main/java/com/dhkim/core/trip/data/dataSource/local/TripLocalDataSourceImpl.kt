package com.dhkim.core.trip.data.dataSource.local

import com.dhkim.database.AppDatabase
import com.dhkim.core.trip.data.toTrip
import com.dhkim.core.trip.data.toTripEntity
import com.dhkim.core.trip.domain.model.Trip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class TripLocalDataSourceImpl @Inject constructor(
    private val db: AppDatabase
) : TripLocalDataSource {

    private val service = db.tripDao()

    override fun getAllTrip(): Flow<List<Trip>> {
        return service.getAllTrip().map { trips ->
            trips?.map { it.toTrip() } ?: listOf()
        }
    }

    override fun getTrip(id: String): Flow<Trip?> {
        return service.getTrip(id).map { it?.toTrip() }
    }

    override suspend fun saveTrip(trip: Trip) {
        service.saveTrip(tripEntity = trip.toTripEntity())
    }

    override suspend fun updateTrip(trip: Trip) {
        service.updateTrip(tripEntity = trip.toTripEntity())
    }

    override suspend fun deleteTrip(id: String) {
        service.deleteTrip(id)
    }
}