package com.dhkim.trip.presentation.tripHome

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.common.onetimeRestartableStateFlow
import com.dhkim.trip.domain.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripUiState())
    val uiState = _uiState.onStart {
        init()
    }.onetimeRestartableStateFlow(
        scope = viewModelScope,
        initialValue = TripUiState(),
        isOnetime = false
    )

    private fun init() {
        viewModelScope.launch {
            tripRepository.getAllTrip()
                .catch {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .collect { trips ->
                    val partition = trips.partition { it.isNextTrip }
                    val nextTrips = partition.first
                    val prevTrips = partition.second
                    val items = mutableListOf<TripItem>()
                    if (nextTrips.isNotEmpty()) {
                        items.add(TripItem(id = "${UUID.randomUUID()}", data = "다음 여행"))
                        items.addAll(nextTrips.map { TripItem(id = it.id, data = it) })
                    }
                    if (prevTrips.isNotEmpty()) {
                        items.add(TripItem(id = "${UUID.randomUUID()}", data = "지난 여행"))
                        items.addAll(prevTrips.map { TripItem(id = it.id, data = it) })
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        trips = items.toImmutableList()
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

@Stable
data class TripItem(
    val id: String,
    val data: Any
)