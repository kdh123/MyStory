package com.dhkim.trip.tripHome

import androidx.compose.runtime.getValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dhkim.core.trip.domain.usecase.DeleteTripUseCase
import com.dhkim.core.trip.domain.usecase.GetAllTripsUseCase
import com.dhkim.testing.FakeTripRepository
import com.dhkim.trip.presentation.tripHome.TripScreen
import com.dhkim.trip.presentation.tripHome.TripViewModel
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TripScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

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

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Screen 테스트`() = runTest {
        composeTestRule.setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            TripScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
                onNavigateToSchedule = {},
                onNavigateToDetail = {},
                showPopup = {}
            )
        }

        composeTestRule.waitUntilAtLeastOneExists(
            hasText("서울 - 부산 여행"),
            300
        )

        composeTestRule.waitUntilAtLeastOneExists(
            hasText("2026-03-01 - 2026-03-03"),
            300
        )
    }
}