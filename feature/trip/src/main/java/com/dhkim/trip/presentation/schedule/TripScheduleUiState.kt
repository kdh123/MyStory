package com.dhkim.trip.presentation.schedule

import com.dhkim.trip.domain.model.TripPlace
import com.dhkim.trip.domain.model.TripType

data class TripScheduleUiState(
    val progress: Float = 0.333f,
    val type: TripType = TripType.Alone,
    val startDate: String = "",
    val endDate: String = "",
    val tripPlaces: List<TripPlace> = listOf()
)
