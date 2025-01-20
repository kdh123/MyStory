package com.dhkim.trip.domain.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class Trip(
    val id: String = "",
    val type: Int = 0,
    val startDate: String = "",
    val endDate: String = "",
    val places: List<String> = listOf(),
    val images: List<TripImage> = listOf(),
    val videos: List<TripVideo> = listOf(),
    val isNextTrip: Boolean = false,
    val isDomestic: Boolean = true,
    val isInit: Boolean = false,
) {
    fun tripPlaces(): ImmutableList<TripPlace> {
        val domesticPlaces = TripPlace.DomesticPlace.entries.map { it.placeName }
        return places.map { place ->
            if (domesticPlaces.contains(place)) {
                TripPlace.DomesticPlace.entries.first { it.placeName == place }
            } else {
                TripPlace.AbroadPlace.entries.first { it.placeName == place }
            }
        }.toImmutableList()
    }
}
