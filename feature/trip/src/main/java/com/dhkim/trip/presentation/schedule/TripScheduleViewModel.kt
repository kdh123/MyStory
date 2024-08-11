package com.dhkim.trip.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.trip.domain.TripRepository
import com.dhkim.trip.domain.model.Trip
import com.dhkim.trip.domain.model.TripPlace
import com.dhkim.trip.presentation.tripHome.TripScheduleSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripScheduleViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripScheduleUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<TripScheduleSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    fun onAction(action: TripScheduleAction) {
        when (action) {
            is TripScheduleAction.UpdateProgress -> {
                _uiState.value = _uiState.value.copy(progress = action.progress)
            }

            is TripScheduleAction.UpdateType -> {
                _uiState.value = _uiState.value.copy(type = action.type)
            }

            is TripScheduleAction.UpdateStartDate -> {
                _uiState.value = _uiState.value.copy(startDate = action.startDate)
            }

            is TripScheduleAction.UpdateEndDate -> {
                _uiState.value = _uiState.value.copy(endDate = action.endDate)
            }

            is TripScheduleAction.UpdatePlaces -> {
                updatePlaces(place = action.place)
            }

            TripScheduleAction.SaveTrip -> {
                saveTrip()
            }
        }
    }

    private fun updatePlaces(place: TripPlace) {
        val updatePlaces = _uiState.value.tripPlaces
            .toMutableList()
            .apply {
                if (_uiState.value.tripPlaces.contains(place)) {
                    remove(place)
                } else {
                    add(place)
                }
            }

        _uiState.value = _uiState.value.copy(tripPlaces = updatePlaces)
    }

    private fun saveTrip() {
        viewModelScope.launch(Dispatchers.IO) {
            val trip = with(_uiState.value) {
                Trip(
                    id = "${System.currentTimeMillis()}",
                    type = type.type,
                    startDate = startDate,
                    endDate = endDate,
                    places = tripPlaces.map {
                        when (it) {
                            is TripPlace.DomesticPlace -> {
                                it.placeName
                            }

                            is TripPlace.AbroadPlace -> {
                                it.placeName
                            }
                        }
                    },
                )
            }

            tripRepository.saveTrip(trip)
            _sideEffect.emit(TripScheduleSideEffect.Complete)
        }
    }
}