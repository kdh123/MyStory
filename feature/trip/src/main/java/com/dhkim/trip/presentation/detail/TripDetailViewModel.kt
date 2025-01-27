package com.dhkim.trip.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.common.Dispatcher
import com.dhkim.common.TimeCapsuleDispatchers
import com.dhkim.core.trip.domain.model.Trip
import com.dhkim.core.trip.domain.model.TripImage
import com.dhkim.core.trip.domain.model.toTripType
import com.dhkim.core.trip.domain.usecase.DeleteTripImageUseCase
import com.dhkim.core.trip.domain.usecase.DeleteTripUseCase
import com.dhkim.core.trip.domain.usecase.GetTripUseCase
import com.dhkim.core.trip.domain.usecase.UpdateTripUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    private val getTripUseCase: GetTripUseCase,
    private val deleteTripUseCase: DeleteTripUseCase,
    private val updateTripUseCase: UpdateTripUseCase,
    private val deleteTripImageUseCase: DeleteTripImageUseCase,
    @Dispatcher(TimeCapsuleDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<TripDetailSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    var tripAllImages = MutableStateFlow<List<TripImage>>(listOf())
        private set

    fun onAction(action: TripDetailAction) {
        when (action) {
            is TripDetailAction.InitTrip -> initTrip(tripId = action.tripId)
            is TripDetailAction.LoadImages -> loadImages(tripId = action.tripId, images = action.images)
            is TripDetailAction.UpdateTrip -> updateTrip(trip = action.trip)
            is TripDetailAction.DeleteTrip -> deleteTrip(tripId = action.tripId)
            is TripDetailAction.SelectDate -> selectDate(selectedIndex = action.selectedIndex)
            is TripDetailAction.DeleteImage -> deleteTripImage(tripId = action.tripId, imageId = action.imageId)
        }
    }

    private fun initTrip(tripId: String) {
        viewModelScope.launch(ioDispatcher) {
            getTripUseCase(tripId = tripId)
                .catch { }
                .collect { currentTrip ->
                    currentTrip ?: return@collect

                    val title = StringBuilder()
                    currentTrip.places.forEachIndexed { index, place ->
                        title.append(place)
                        if (index < currentTrip.places.size - 1) {
                            title.append(", ")
                        }
                    }
                    title.append(" 여행")
                    val currentIndex = _uiState.value.selectedIndex
                    if (!currentTrip.isInit) {
                        with(currentTrip) {
                            _uiState.update {
                                it.copy(
                                    isLoading = true,
                                    isInit = true,
                                    title = "$title",
                                    selectedIndex = if (currentIndex < 0) 0 else currentIndex,
                                    startDate = startDate,
                                    endDate = endDate,
                                    type = type.toTripType().desc
                                )
                            }
                        }
                        _sideEffect.send(
                            TripDetailSideEffect.LoadImages(
                                startDate = currentTrip.startDate,
                                endDate = currentTrip.endDate
                            )
                        )
                    } else {
                        with(currentTrip) {
                            tripAllImages.value = currentTrip.images
                            val strDate: String = if (_uiState.value.selectedIndex <= 0) {
                                currentTrip.startDate
                            } else {
                                val date = _uiState.value.tripDates[_uiState.value.selectedIndex].date
                                "${date.first}-${date.second}-${date.third}"
                            }
                            val images = tripAllImages.value.filter { it.date == strDate }.toImmutableList()

                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isInit = true,
                                    selectedIndex = if (currentIndex < 0) 0 else currentIndex,
                                    title = "$title",
                                    type = currentTrip.type.toTripType().desc,
                                    startDate = startDate,
                                    endDate = endDate,
                                    images = images
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun deleteTripImage(tripId: String, imageId: String) {
        viewModelScope.launch(ioDispatcher) {
            tripAllImages.value = tripAllImages.value.filter { it.id != imageId }
            deleteTripImageUseCase(tripId = tripId, imageId = imageId)
        }
    }

    private fun updateTrip(trip: Trip) {
        viewModelScope.launch(ioDispatcher) {
            updateTripUseCase(trip = trip.copy(images = listOf(), videos = listOf(), isInit = false))
            _sideEffect.send(
                TripDetailSideEffect.LoadImages(
                    startDate = trip.startDate,
                    endDate = trip.endDate
                )
            )
        }
    }

    private fun deleteTrip(tripId: String) {
        viewModelScope.launch(ioDispatcher) {
            deleteTripUseCase(id = tripId)
        }
    }

    private fun selectDate(selectedIndex: Int) {
        viewModelScope.launch(ioDispatcher) {
            val date = _uiState.value.tripDates[selectedIndex].date
            val strDate = "${date.first}-${date.second}-${date.third}"
            val images = tripAllImages.value.filter { it.date == strDate }.toImmutableList()
            _uiState.update { it.copy(selectedIndex = selectedIndex, images = images) }
        }
    }

    private fun loadImages(tripId: String, images: List<TripImage>) {
        viewModelScope.launch(ioDispatcher) {
            val currentTrip = getTripUseCase(tripId = tripId).firstOrNull() ?: return@launch

            tripAllImages.value = images
            val title = StringBuilder()
            currentTrip.places.forEachIndexed { index, place ->
                title.append(place)
                if (index < currentTrip.places.size - 1) title.append(", ")
            }
            title.append(" 여행")

            with(currentTrip) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        title = "$title",
                        type = currentTrip.type.toTripType().desc,
                        startDate = startDate,
                        endDate = endDate,
                        selectedIndex = 0,
                    )
                }
            }
            selectDate(0)
            updateTripUseCase(currentTrip.copy(images = tripAllImages.value, isInit = true))
        }
    }
}