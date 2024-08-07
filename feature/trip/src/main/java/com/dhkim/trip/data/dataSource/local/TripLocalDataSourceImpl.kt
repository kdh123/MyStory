package com.dhkim.trip.data.dataSource.local

import com.dhkim.database.AppDatabase
import com.dhkim.trip.domain.model.Trip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class TripLocalDataSourceImpl @Inject constructor(
    private val db: AppDatabase
) : TripLocalDataSource {

    private val trips = MutableStateFlow(listOf<Trip>())

    init {
        val trips = mutableListOf<Trip>().apply {
            repeat(5) {
                add(
                    Trip(
                        id = "id$it",
                        startDate = "2024-05-30",
                        endDate = "2024-06-10",
                        places = listOf("서울, 부산"),
                        images = listOf(),
                        videos = listOf()
                    )
                )
            }
        }

        this.trips.value = trips
    }

    override fun getAllTrip(): Flow<List<Trip>> {
        return trips
    }

    override fun getTrip(id: String): Trip? {
        return trips.value[0]
    }

    override fun saveTrip(trip: Trip) {
        val updateTrips = trips.value.toMutableList().apply {
            add(trip)
        }

        trips.value = updateTrips
    }

    override fun updateTrip(trip: Trip) {
        val updateTripIndex = trips.value.indexOfFirst { it.id == trip.id }
        val updateTrips = trips.value.toMutableList().apply {
            set(updateTripIndex, trip)
        }

        trips.value = updateTrips
    }

    override fun deleteTrip(id: String) {
        trips.value = trips.value.filter { it.id != id }
    }
}