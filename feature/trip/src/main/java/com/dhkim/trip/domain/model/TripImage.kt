package com.dhkim.trip.domain.model

import androidx.compose.runtime.Stable

@Stable
data class TripImage(
    val date: String = "",
    val memo: String = "",
    val address: String = "",
    val imageUrl: String = ""
)
