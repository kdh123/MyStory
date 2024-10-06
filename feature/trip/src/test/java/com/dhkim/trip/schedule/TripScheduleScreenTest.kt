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
import com.dhkim.trip.FakeTripLocalDataSource
import com.dhkim.trip.data.dataSource.local.TripLocalDataSource
import com.dhkim.trip.data.dataSource.local.TripRepositoryImpl
import com.dhkim.trip.data.di.TripModule
import com.dhkim.trip.domain.TripRepository
import com.dhkim.trip.presentation.schedule.TripScheduleScreen
import com.dhkim.trip.presentation.schedule.TripScheduleViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject
import javax.inject.Singleton

@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
@HiltAndroidTest
@UninstallModules(TripModule::class)
class TripScheduleScreenTest {

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class TripModule {

        @Binds
        @Singleton
        abstract fun bindTripRepository(tripRepositoryImpl: TripRepositoryImpl): TripRepository

        @Binds
        @Singleton
        abstract fun bindTripLocalDataSource(fakeTripLocalDataSourceImpl: FakeTripLocalDataSource): TripLocalDataSource
    }

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: TripScheduleViewModel

    @Inject
    lateinit var tripRepository: TripRepository

    @Before
    fun setup() {
        hiltRule.inject()
        viewModel = TripScheduleViewModel(tripRepository = tripRepository, savedStateHandle = SavedStateHandle())
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