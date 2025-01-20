package com.dhkim.trip.presentation.tripHome

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.common.Dispatcher
import com.dhkim.common.TimeCapsuleDispatchers
import com.dhkim.common.onetimeRestartableStateIn
import com.dhkim.trip.domain.repository.TripRepository
import com.dhkim.trip.domain.model.Trip
import com.dhkim.trip.domain.usecase.DeleteTripUseCase
import com.dhkim.trip.domain.usecase.GetAllTripsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    private val getAllTripsUseCase: GetAllTripsUseCase,
    private val deleteTripUseCase: DeleteTripUseCase,
    @Dispatcher(TimeCapsuleDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    val uiState = getAllTripsUseCase()
        .flowOn(ioDispatcher)
        .map { it.toUiState() }
        .onetimeRestartableStateIn(
            scope = viewModelScope,
            initialValue = TripUiState(),
            isOnetime = false
        )

    fun onAction(action: TripAction) {
        when (action) {
            is TripAction.DeleteTrip -> {
                deleteTrip(tripId = action.tripId)
            }
        }
    }

    private fun deleteTrip(tripId: String) {
        viewModelScope.launch(ioDispatcher) {
            deleteTripUseCase(id = tripId)
        }
    }
}

@Stable
data class TripItem(
    val id: String,
    val data: Any
)

fun List<Trip>.toUiState(): TripUiState {
    val partition = partition { it.isNextTrip }
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

    return TripUiState(
        isLoading = false,
        trips = items.toImmutableList()
    )
}