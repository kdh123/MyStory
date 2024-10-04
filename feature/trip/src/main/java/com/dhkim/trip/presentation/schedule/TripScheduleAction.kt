package com.dhkim.trip.presentation.schedule

import com.dhkim.trip.domain.model.TripPlace
import com.dhkim.trip.domain.model.TripType

sealed interface TripScheduleAction {

    data class UpdateProgress(val progress: Float) : TripScheduleAction
    data class UpdateType(val type: TripType) : TripScheduleAction
    data class UpdateStartDate(val startDate: String) : TripScheduleAction
    data class UpdateEndDate(val endDate: String) : TripScheduleAction
    data class UpdatePlaces(val place: TripPlace) : TripScheduleAction
    data object SaveTrip : TripScheduleAction
    data object UpdateTrip : TripScheduleAction
}