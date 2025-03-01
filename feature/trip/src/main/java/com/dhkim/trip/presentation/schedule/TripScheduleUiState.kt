package com.dhkim.trip.presentation.schedule

import androidx.compose.runtime.Stable
import com.dhkim.core.trip.domain.model.TripPlace
import com.dhkim.core.trip.domain.model.TripType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class TripScheduleUiState(
    val progress: Float = 0.333f,
    val type: TripType = TripType.Alone,
    val startDate: String = "",
    val endDate: String = "",
    val tripPlaces: ImmutableList<TripPlace> = persistentListOf()
)
