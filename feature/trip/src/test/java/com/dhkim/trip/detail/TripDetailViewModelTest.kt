package com.dhkim.trip.detail

import com.dhkim.core.trip.domain.usecase.DeleteTripImageUseCase
import com.dhkim.core.trip.domain.usecase.DeleteTripUseCase
import com.dhkim.core.trip.domain.usecase.GetTripUseCase
import com.dhkim.core.trip.domain.usecase.UpdateTripUseCase
import com.dhkim.testing.FakeTripRepository
import com.dhkim.trip.presentation.detail.TripDetailAction
import com.dhkim.trip.presentation.detail.TripDetailViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TripDetailViewModelTest {

    private lateinit var viewModel: TripDetailViewModel

    private val tripRepository = FakeTripRepository()

    private val getTripUseCase = GetTripUseCase(tripRepository)

    private val deleteTripUseCase = DeleteTripUseCase(tripRepository)

    private val updateTripUseCase = UpdateTripUseCase(tripRepository)

    private val deleteTripImageUseCase = DeleteTripImageUseCase(getTripUseCase, updateTripUseCase)

    @Before
    fun setup() {
        viewModel = TripDetailViewModel(
            getTripUseCase = getTripUseCase,
            deleteTripUseCase = deleteTripUseCase,
            updateTripUseCase = updateTripUseCase,
            deleteTripImageUseCase = deleteTripImageUseCase,
            ioDispatcher = UnconfinedTestDispatcher()
        )
    }

    @Test
    fun `여행 이미지가 존재할 때 - 다음 여행`() = runBlocking {
        viewModel.onAction(TripDetailAction.InitTrip(tripId = "id0"))
        delay(100)
        assertEquals(viewModel.uiState.value.images?.size, 0)
    }

    @Test
    fun `여행 이미지가 존재할 때 - 지난 여행`() = runBlocking {
        viewModel.onAction(TripDetailAction.InitTrip(tripId = "id1"))
        delay(100)
        assertEquals(viewModel.uiState.value.images?.size, 7)
    }

    @Test
    fun `특정 날짜 선택`() = runBlocking {
        viewModel.onAction(TripDetailAction.InitTrip(tripId = "id1"))
        delay(100)
        viewModel.onAction(TripDetailAction.SelectDate(selectedIndex = 1))
        delay(100)

        assertEquals(viewModel.uiState.value.images?.size, 2)
    }

    @Test
    fun `이미지 삭제`() = runBlocking {
        viewModel.onAction(TripDetailAction.InitTrip(tripId = "id1"))
        delay(100)
        viewModel.onAction(TripDetailAction.DeleteImage(tripId = "id1", imageId = "trip0"))
        delay(100)

        assertEquals(viewModel.uiState.value.images?.size, 7)
        assertEquals(viewModel.tripAllImages.value.size, 19)
    }
}