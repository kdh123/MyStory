package com.dhkim.trip.presentation.detail

import com.dhkim.trip.domain.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    private val tripRepository: TripRepository
) {

    private val _uiState = MutableStateFlow(TripDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: TripDetailAction) {
        when (action) {
            is TripDetailAction.LoadTrip -> {

            }

            is TripDetailAction.UpdateTrip -> {

            }


            is TripDetailAction.DeleteTrip -> {}

        }
    }

}