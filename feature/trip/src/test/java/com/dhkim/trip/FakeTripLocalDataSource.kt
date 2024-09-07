package com.dhkim.trip

import com.dhkim.database.TripImageDto
import com.dhkim.database.entity.TripEntity
import com.dhkim.trip.data.dataSource.local.TripLocalDataSource
import com.dhkim.trip.data.toTrip
import com.dhkim.trip.domain.model.Trip
import com.dhkim.trip.domain.model.TripType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class FakeTripLocalDataSource @Inject constructor() : TripLocalDataSource {

    private val trips = MutableStateFlow(listOf<Trip>())

    init {
        val images = mutableListOf<TripImageDto>().apply {
            repeat(20) {
                add(
                    TripImageDto(
                        id = "trip$it",
                        date = if(it % 3 == 0) {
                            "2024-03-01"
                        } else if (it % 7 == 0) {
                            "2024-03-02"
                        } else {
                            "2024-03-03"
                        },
                        memo = "good trip$it",
                        lat = 37.572389,
                        lng = 126.9769117,
                        address = "",
                        imageUrl = "imageUrl$it"
                    )
                )
            }
        }

        val trips = mutableListOf<TripEntity>().apply {
            repeat(6) {
                add(
                    if (it % 3 == 0) {
                        TripEntity(
                            id = "id$it",
                            type = TripType.Alone.type,
                            startDate = "2024-03-01",
                            endDate = "2024-03-03",
                            places = listOf("서울", "부산"),
                            images = images,
                            videos = listOf()
                        )
                    } else {
                        TripEntity(
                            id = "id$it",
                            type = TripType.Alone.type,
                            startDate = "2024-03-01",
                            endDate = "2024-03-03",
                            places = listOf("서울", "부산"),
                            images = images,
                            videos = listOf()
                        )
                    }
                )
            }
        }

        this.trips.value = trips.map { it.toTrip() }
    }

    override suspend fun getAllTrip(): Flow<List<Trip>> {
        return trips
    }

    override suspend fun getTrip(id: String): Flow<Trip?> {
        return flowOf(trips.value.first { it.id == id })
    }

    override suspend fun saveTrip(trip: Trip) {
        val updateTrips = trips.value.toMutableList().apply {
            add(trip)
        }

        trips.value = updateTrips
    }

    override suspend fun updateTrip(trip: Trip) {
        val updateTripIndex = trips.value.indexOfFirst { it.id == trip.id }
        val updateTrips = trips.value.toMutableList().apply {
            set(updateTripIndex, trip)
        }

        trips.value = updateTrips
    }

    override suspend fun deleteTrip(id: String) {
        trips.value = trips.value.filter { it.id != id }
    }
}