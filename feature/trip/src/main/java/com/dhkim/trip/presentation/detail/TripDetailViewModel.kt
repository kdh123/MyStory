package com.dhkim.trip.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.trip.domain.TripRepository
import com.dhkim.trip.domain.model.TripImage
import com.dhkim.trip.domain.model.toTripType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<TripDetailSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    private val tripAllImages = MutableStateFlow<List<TripImage>>(listOf())

    fun onAction(action: TripDetailAction) {
        when (action) {
            is TripDetailAction.InitTrip -> {
                if (uiState.value.tripDates.isEmpty()) {
                    initTrip(tripId = action.tripId)
                }
            }

            is TripDetailAction.LoadImages -> {
                loadImages(tripId = action.tripId, images = action.images)
            }

            is TripDetailAction.UpdateTrip -> {

            }

            is TripDetailAction.DeleteTrip -> {}

            is TripDetailAction.SelectDate -> {
                selectDate(selectedIndex = action.selectedIndex)
            }
        }
    }

    private fun selectDate(selectedIndex: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val date = _uiState.value.tripDates[selectedIndex].date
            val strDate = "${date.first}-${date.second}-${date.third}"
            val images = tripAllImages.value.filter { it.date == strDate }.toImmutableList()
            _uiState.value = _uiState.value.copy(selectedIndex = selectedIndex, images = images)
        }
    }

    private fun initTrip(tripId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentTrip = tripRepository.getTrip(id = tripId)

            if (currentTrip != null) {
                val title = StringBuilder()
                currentTrip.places.forEachIndexed { index, place ->
                    title.append(place)
                    if (index < currentTrip.places.size - 1) {
                        title.append(", ")
                    }
                }
                title.append(" 여행")

                if (currentTrip.images.isEmpty()) {
                    with(currentTrip) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            title = "$title",
                            startDate = startDate,
                            endDate = endDate,
                            type = type.toTripType().desc
                        )
                    }

                    _sideEffect.emit(
                        TripDetailSideEffect.LoadImages(
                            startDate = currentTrip.startDate,
                            endDate = currentTrip.endDate
                        )
                    )
                } else {
                    with(currentTrip) {
                        tripAllImages.value = currentTrip.images

                        val strDate = currentTrip.startDate
                        val images = tripAllImages.value.filter { it.date == strDate }.toImmutableList()

                        _uiState.value = _uiState.value.copy(
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

    private fun loadImages(tripId: String, images: List<TripImage>) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentTrip = tripRepository.getTrip(id = tripId)

            if (currentTrip != null) {
                tripAllImages.value = images
                val title = StringBuilder()
                currentTrip.places.forEachIndexed { index, place ->
                    title.append(place)
                    if (index < currentTrip.places.size - 1) {
                        title.append(", ")
                    }
                }
                title.append(" 여행")

                with(currentTrip) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        title = "$title",
                        type = currentTrip.type.toTripType().desc,
                        startDate = startDate,
                        endDate = endDate,
                        selectedIndex = 0,
                    )
                }
                selectDate(0)
                tripRepository.updateTrip(currentTrip.copy(images = tripAllImages.value))
            }
        }
    }
}