package com.dhkim.trip.tripHome

import com.dhkim.core.trip.domain.model.Trip
import com.dhkim.core.trip.domain.usecase.DeleteTripUseCase
import com.dhkim.core.trip.domain.usecase.GetAllTripsUseCase
import com.dhkim.testing.FakeTripRepository
import com.dhkim.trip.presentation.tripHome.TripAction
import com.dhkim.trip.presentation.tripHome.TripViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TripViewModelTest {

    private lateinit var viewModel: TripViewModel

    private val tripRepository = FakeTripRepository()

    private val getAllTripsUseCase = GetAllTripsUseCase(tripRepository)

    private val deleteTripUseCase = DeleteTripUseCase(tripRepository)

    @Before
    fun setup() {
        viewModel = TripViewModel(
            getAllTripsUseCase = getAllTripsUseCase,
            deleteTripUseCase = deleteTripUseCase,
            ioDispatcher = UnconfinedTestDispatcher()
        )
    }

    @Test
    fun `uiState 테스트`() = runBlocking {
        viewModel.uiState.first()
        delay(100)

        val items = viewModel.uiState.value.trips?.filter {
            it.data !is String
        }

        assertEquals(items?.count { (it.data as Trip).isNextTrip }, 2)
        assertEquals(items?.count { !(it.data as Trip).isNextTrip }, 4)
    }

    @Test
    fun `여행 아이템 삭제 테스트`() = runBlocking {
        viewModel.uiState.first()
        viewModel.onAction(TripAction.DeleteTrip(tripId = "id0"))
        viewModel.uiState.restart()

        val items = viewModel.uiState.value.trips?.filter {
            it.data !is String
        }
        assertEquals(items?.count { (it.data as Trip).isNextTrip }, 1)
        assertEquals(items?.count { !(it.data as Trip).isNextTrip }, 4)
    }
}