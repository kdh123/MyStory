package com.dhkim.trip.schedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dhkim.core.trip.domain.usecase.GetTripUseCase
import com.dhkim.core.trip.domain.usecase.SaveTripUseCase
import com.dhkim.core.trip.domain.usecase.UpdateTripUseCase
import com.dhkim.testing.FakeTripRepository
import com.dhkim.trip.presentation.schedule.TripScheduleScreen
import com.dhkim.trip.presentation.schedule.TripScheduleViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TripScheduleScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

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
            ioDispatcher = Dispatchers.IO
        )
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Screen 테스트`() = runTest {
        composeTestRule.setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val sideEffect = remember {
                viewModel.sideEffect
            }

            TripScheduleScreen(
                isEdit = false,
                uiState = uiState,
                sideEffect = sideEffect,
                onAction = viewModel::onAction,
                onBack = {}
            )
        }

        composeTestRule.waitUntilAtLeastOneExists(
            hasText("어떤 여행을 계획하고 있나요?"),
            300
        )

        composeTestRule.onNodeWithTag("tripType1")
            .performClick()

        composeTestRule.onNodeWithTag("tripTypeNextBtn")
            .performClick()

        composeTestRule.waitUntilAtLeastOneExists(
            hasText("여행하려는 장소가 어디인가요?"),
            300
        )

        composeTestRule.onNodeWithTag("인천")
            .performClick()

        composeTestRule.onNodeWithTag("tripPlaceNextBtn")
            .performClick()

        composeTestRule.waitUntilAtLeastOneExists(
            hasText("여행 일정이 어떻게 되나요?"),
            300
        )
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `국내,해외 타입 클릭 테스트`() = runTest {
        composeTestRule.setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val sideEffect = remember {
                viewModel.sideEffect
            }

            TripScheduleScreen(
                isEdit = false,
                uiState = uiState,
                sideEffect = sideEffect,
                onAction = viewModel::onAction,
                onBack = {}
            )
        }

        composeTestRule.waitUntilAtLeastOneExists(
            hasText("어떤 여행을 계획하고 있나요?"),
            300
        )

        composeTestRule.onNodeWithTag("tripType1")
            .performClick()

        composeTestRule.onNodeWithTag("tripTypeNextBtn")
            .performClick()

        composeTestRule.waitUntilAtLeastOneExists(
            hasText("여행하려는 장소가 어디인가요?"),
            300
        )

        composeTestRule.onNodeWithTag("abroad")
            .performClick()

        composeTestRule.waitUntilAtLeastOneExists(
            hasText("일본"),
            300
        )

        composeTestRule.onNodeWithTag("domestic")
            .performClick()

        composeTestRule.waitUntilAtLeastOneExists(
            hasText("인천"),
            300
        )
    }
}