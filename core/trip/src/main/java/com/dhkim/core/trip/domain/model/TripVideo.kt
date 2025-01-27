package com.dhkim.core.trip.domain.model

data class TripVideo(
    val date: String = "",
    val memo: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val address: String = "",
    val videoUrl: String = ""
)
