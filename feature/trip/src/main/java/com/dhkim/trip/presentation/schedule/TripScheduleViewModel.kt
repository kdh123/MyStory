package com.dhkim.trip.presentation.schedule

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.common.Dispatcher
import com.dhkim.common.TimeCapsuleDispatchers
import com.dhkim.common.onetimeRestartableStateIn
import com.dhkim.trip.domain.model.Trip
import com.dhkim.trip.domain.model.TripPlace
import com.dhkim.trip.domain.model.toTripType
import com.dhkim.trip.domain.usecase.GetTripUseCase
import com.dhkim.trip.domain.usecase.SaveTripUseCase
import com.dhkim.trip.domain.usecase.UpdateTripUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripScheduleViewModel @Inject constructor(
    private val getTripUseCase: GetTripUseCase,
    private val saveTripUseCase: SaveTripUseCase,
    private val updateTripUseCase: UpdateTripUseCase,
    private val savedStateHandle: SavedStateHandle,
    @Dispatcher(TimeCapsuleDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val tripId = savedStateHandle.get<String>("tripId") ?: ""
    private val _uiState = MutableStateFlow(TripScheduleUiState())
    val uiState = _uiState.onStart { init(tripId) }
        .onetimeRestartableStateIn(
            scope = viewModelScope,
            initialValue = TripScheduleUiState()
        )

    private val _sideEffect = Channel<TripScheduleSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    fun onAction(action: TripScheduleAction) {
        when (action) {
            is TripScheduleAction.UpdateProgress -> _uiState.update { it.copy(progress = action.progress) }
            is TripScheduleAction.UpdateType -> _uiState.update { it.copy(type = action.type) }
            is TripScheduleAction.UpdateStartDate -> _uiState.update { it.copy(startDate = action.startDate) }
            is TripScheduleAction.UpdateEndDate -> _uiState.update { it.copy(endDate = action.endDate) }
            is TripScheduleAction.UpdatePlaces -> updatePlaces(place = action.place)
            TripScheduleAction.SaveTrip -> saveTrip()
            TripScheduleAction.UpdateTrip -> updateTrip(tripId = tripId)
        }
    }

    private fun init(tripId: String) {
        viewModelScope.launch(ioDispatcher) {
            val trip = getTripUseCase(tripId).firstOrNull() ?: return@launch
            with(trip) {
                _uiState.value = _uiState.value.copy(
                    type = type.toTripType(),
                    startDate = startDate,
                    endDate = endDate,
                    tripPlaces = tripPlaces()
                )
            }
        }
    }

    private fun updatePlaces(place: TripPlace) {
        val updatePlaces = _uiState.value.tripPlaces
            .toMutableList()
            .apply { if (_uiState.value.tripPlaces.contains(place)) remove(place) else add(place) }
            .toImmutableList()

        _uiState.value = _uiState.value.copy(tripPlaces = updatePlaces)
    }

    private fun saveTrip() {
        viewModelScope.launch(ioDispatcher) {
            val trip = with(_uiState.value) {
                Trip(
                    id = "${System.currentTimeMillis()}",
                    type = type.type,
                    startDate = startDate,
                    endDate = endDate,
                    places = tripPlaces.map {
                        when (it) {
                            is TripPlace.DomesticPlace -> it.placeName
                            is TripPlace.AbroadPlace -> it.placeName
                        }
                    },
                )
            }

            saveTripUseCase(trip = trip)
            _sideEffect.send(TripScheduleSideEffect.Complete)
        }
    }

    private fun updateTrip(tripId: String) {
        viewModelScope.launch(ioDispatcher) {
            val trip = with(_uiState.value) {
                Trip(
                    id = tripId,
                    type = type.type,
                    startDate = startDate,
                    endDate = endDate,
                    places = tripPlaces.map {
                        when (it) {
                            is TripPlace.DomesticPlace -> it.placeName
                            is TripPlace.AbroadPlace -> it.placeName
                        }
                    },
                )
            }

            updateTripUseCase(trip = trip.copy(images = listOf(), videos = listOf()))
            _sideEffect.send(TripScheduleSideEffect.Complete)
        }
    }
}