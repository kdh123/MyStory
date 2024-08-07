package com.dhkim.trip.domain.model

import androidx.compose.runtime.Stable

@Stable
data class TripVideo(
    val date: String = "",
    val address: String = "",
    val videoUrl: String = ""
)
