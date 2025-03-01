package com.dhkim.trip.schedule

import androidx.lifecycle.SavedStateHandle
import com.dhkim.core.trip.domain.usecase.GetTripUseCase
import com.dhkim.core.trip.domain.usecase.SaveTripUseCase
import com.dhkim.core.trip.domain.usecase.UpdateTripUseCase
import com.dhkim.testing.FakeTripRepository
import com.dhkim.trip.presentation.schedule.TripScheduleAction
import com.dhkim.trip.presentation.schedule.TripScheduleViewModel
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
class TripScheduleViewModelTest {

    private lateinit var viewModel: TripScheduleViewModel

    private val tripRepository = FakeTripRepository()

    private val getTripUseCase = GetTripUseCase(tripRepository)

    private val saveTripUseCase = SaveTripUseCase(tripRepository)

    private val updateTripUseCase = UpdateTripUseCase(tripRepository)

    @Before
    fun setup() {
        viewModel = TripScheduleViewModel(
            getTripUseCase = getTripUseCase,
            saveTripUseCase = saveTripUseCase,
            updateTripUseCase = updateTripUseCase,
            savedStateHandle = SavedStateHandle(),
            ioDispatcher = UnconfinedTestDispatcher()
        )
    }

    @Test
    fun `여행 시작 날짜 업데이트 테스트`() = runBlocking {
        viewModel.uiState.first()
        viewModel.onAction(TripScheduleAction.UpdateStartDate("2024-04-03"))
        delay(100)
        assertEquals(viewModel.uiState.value.startDate, "2024-04-03")
    }

    @Test
    fun `여행 장소 선택 테스트`() = runBlocking {
        viewModel.uiState.first()
        viewModel.onAction(TripScheduleAction.UpdatePlaces(com.dhkim.core.trip.domain.model.TripPlace.DomesticPlace.Seoul))
        viewModel.onAction(TripScheduleAction.UpdatePlaces(com.dhkim.core.trip.domain.model.TripPlace.DomesticPlace.Gyeongi))
        viewModel.onAction(TripScheduleAction.UpdatePlaces(com.dhkim.core.trip.domain.model.TripPlace.AbroadPlace.USA))

        delay(100)
        assertEquals(viewModel.uiState.value.tripPlaces.size, 3)
    }
}