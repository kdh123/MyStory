package com.dhkim.trip.data.dataSource.local

import com.dhkim.database.AppDatabase
import com.dhkim.trip.data.toTrip
import com.dhkim.trip.data.toTripEntity
import com.dhkim.trip.domain.model.Trip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TripLocalDataSourceImpl @Inject constructor(
    private val db: AppDatabase
) : TripLocalDataSource {

    private val service = db.tripDao()

    override fun getAllTrip(): Flow<List<Trip>> {
        return service.getAllTrip().map { trips ->
            trips?.map { it.toTrip() } ?: listOf()
        }
    }

    override fun getTrip(id: String): Trip? {
        return service.getTrip(id)?.toTrip()
    }

    override fun saveTrip(trip: Trip) {
        service.saveTrip(tripEntity = trip.toTripEntity())
    }

    override fun updateTrip(trip: Trip) {
        service.updateTrip(tripEntity = trip.toTripEntity())
    }

    override fun deleteTrip(id: String) {
        service.deleteTrip(id)
    }
}