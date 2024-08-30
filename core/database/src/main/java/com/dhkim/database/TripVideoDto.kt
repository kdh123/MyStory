package com.dhkim.database

data class TripVideoDto(
    val date: String = "",
    val memo: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val address: String = "",
    val videoUrl: String = ""
)
