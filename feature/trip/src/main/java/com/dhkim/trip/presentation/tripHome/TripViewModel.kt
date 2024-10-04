package com.dhkim.trip.presentation.tripHome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.common.onetimeRestartableStateIn
import com.dhkim.trip.domain.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    private var initJob: Job? = null

    private val _uiState = MutableStateFlow(TripUiState())
    val uiState = _uiState.onStart {
        init()
    }.onetimeRestartableStateIn(
        scope = viewModelScope,
        initialValue = TripUiState(),
        isOnetime = false
    )

    private fun init() {
        initJob?.cancel()
        initJob = viewModelScope.launch {
            tripRepository.getAllTrip()
                .catch {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .collect { trips ->
                    val partition = trips.partition { it.isNextTrip }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        nextTrips = partition.first.toImmutableList(),
                        prevTrips = partition.second.toImmutableList(),
                    )
                }
        }
    }

    fun onAction(action: TripAction) {
        when (action) {
            is TripAction.DeleteTrip -> {
                deleteTrip(tripId = action.tripId)
            }
        }
    }

    private fun deleteTrip(tripId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            tripRepository.deleteTrip(id = tripId)
        }
    }
}