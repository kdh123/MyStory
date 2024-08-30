package com.dhkim.database

data class TripImageDto(
    val id: String = "",
    val date: String = "",
    val memo: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val address: String = "",
    val imageUrl: String = ""
)
