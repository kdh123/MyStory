package com.dhkim.trip.domain.model

data class Trip(
    val id: String = "",
    val type: Int = 0,
    val startDate: String = "",
    val endDate: String = "",
    val places: List<String> = listOf(),
    val images: List<TripImage> = listOf(),
    val videos: List<TripVideo> = listOf(),
    val isNextTrip: Boolean = false,
    val isDomestic: Boolean = true
)
