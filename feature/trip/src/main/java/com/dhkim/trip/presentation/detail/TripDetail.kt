package com.dhkim.trip.presentation.detail

import androidx.compose.runtime.Stable
import com.dhkim.core.trip.domain.model.TripImage
import com.dhkim.core.trip.domain.model.TripVideo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class TripDetail(
    val startDate: String = "",
    val endDate: String = "",
    val images: ImmutableList<TripImage> = persistentListOf(),
    val videos: ImmutableList<TripVideo> = persistentListOf()
)
