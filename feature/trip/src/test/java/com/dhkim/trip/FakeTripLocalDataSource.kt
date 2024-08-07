package com.dhkim.trip

import com.dhkim.database.entity.TripEntity
import com.dhkim.trip.data.dataSource.local.TripLocalDataSource
import com.dhkim.trip.data.toTrip
import com.dhkim.trip.domain.model.Trip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class FakeTripLocalDataSource @Inject constructor() : TripLocalDataSource {

    private val trips = MutableStateFlow(listOf<Trip>())

    init {
        val trips = mutableListOf<TripEntity>().apply {
            repeat(6) {
                add(
                    if (it % 3 == 0) {
                        TripEntity(
                            id = "id$it",
                            startDate = "2024-09-04",
                            endDate = "2024-09-10",
                            places = listOf("서울", "부산"),
                            images = listOf(),
                            videos = listOf()
                        )
                    } else {
                        TripEntity(
                            id = "id$it",
                            startDate = "2024-05-04",
                            endDate = "2024-05-10",
                            places = listOf("서울", "부산"),
                            images = listOf(),
                            videos = listOf()
                        )
                    }
                )
            }
        }

        this.trips.value = trips.map { it.toTrip() }
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