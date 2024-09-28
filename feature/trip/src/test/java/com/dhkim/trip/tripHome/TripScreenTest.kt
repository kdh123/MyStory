package com.dhkim.trip.tripHome

import androidx.compose.runtime.getValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dhkim.trip.FakeTripLocalDataSource
import com.dhkim.trip.data.dataSource.local.TripLocalDataSource
import com.dhkim.trip.data.dataSource.local.TripRepositoryImpl
import com.dhkim.trip.data.di.TripModule
import com.dhkim.trip.domain.TripRepository
import com.dhkim.trip.presentation.tripHome.TripScreen
import com.dhkim.trip.presentation.tripHome.TripViewModel
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
class TripScreenTest {

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

    private lateinit var viewModel: TripViewModel
    @Inject
    lateinit var tripRepository: TripRepository

    @Before
    fun setup() {
        hiltRule.inject()
        viewModel = TripViewModel(tripRepository = tripRepository)
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
            hasText("2024-09-04 - 2024-09-10"),
            300
        )
    }
}