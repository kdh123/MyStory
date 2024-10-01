package com.dhkim.trip.presentation.schedule

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.common.onetimeRestartableStateFlow
import com.dhkim.trip.domain.TripRepository
import com.dhkim.trip.domain.model.Trip
import com.dhkim.trip.domain.model.TripPlace
import com.dhkim.trip.domain.model.toTripType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripScheduleViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tripId = savedStateHandle.get<String>("tripId") ?: ""
    private val _uiState = MutableStateFlow(TripScheduleUiState())
    val uiState = _uiState.onStart {
        if (tripId.isNotEmpty()) {
            init(tripId = tripId)
        }
    }.onetimeRestartableStateFlow(
        scope = viewModelScope,
        initialValue = TripScheduleUiState()
    )

    private val _sideEffect = Channel<TripScheduleSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

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

            TripScheduleAction.UpdateTrip -> {
                updateTrip(tripId = tripId)
            }
        }
    }

    private fun init(tripId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val trip = tripRepository.getTrip(tripId).firstOrNull() ?: return@launch

            trip.run {
                val domesticPlaces = TripPlace.DomesticPlace.entries.map { it.placeName }
                val updatePlaces = places.map { place ->
                    if (domesticPlaces.contains(place)) {
                        TripPlace.DomesticPlace.entries.first { it.placeName == place }
                    } else {
                        TripPlace.AbroadPlace.entries.first { it.placeName == place }
                    }
                }.toImmutableList()

                _uiState.value = _uiState.value.copy(
                    type = type.toTripType(),
                    startDate = startDate,
                    endDate = endDate,
                    tripPlaces = updatePlaces
                )
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
            }.toImmutableList()

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
            _sideEffect.send(TripScheduleSideEffect.Complete)
        }
    }

    private fun updateTrip(tripId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val trip = with(_uiState.value) {
                Trip(
                    id = tripId,
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

            tripRepository.updateTrip(trip = trip.copy(images = listOf(), videos = listOf()))
            _sideEffect.send(TripScheduleSideEffect.Complete)
        }
    }
}