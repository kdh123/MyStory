package com.dhkim.trip.schedule

import androidx.lifecycle.SavedStateHandle
import com.dhkim.trip.FakeTripLocalDataSource
import com.dhkim.trip.data.dataSource.local.TripLocalDataSource
import com.dhkim.trip.data.dataSource.local.TripRepositoryImpl
import com.dhkim.trip.data.di.TripModule
import com.dhkim.trip.domain.TripRepository
import com.dhkim.trip.domain.model.TripPlace
import com.dhkim.trip.presentation.schedule.TripScheduleAction
import com.dhkim.trip.presentation.schedule.TripScheduleViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
@HiltAndroidTest
@UninstallModules(TripModule::class)
class TripScheduleViewModelTest {

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

    private lateinit var viewModel: TripScheduleViewModel
    @Inject lateinit var tripRepository: TripRepository

    @Before
    fun setup() {
        hiltRule.inject()
        viewModel = TripScheduleViewModel(
            tripRepository = tripRepository,
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
        viewModel.onAction(TripScheduleAction.UpdatePlaces(TripPlace.DomesticPlace.Seoul))
        viewModel.onAction(TripScheduleAction.UpdatePlaces(TripPlace.DomesticPlace.Gyeongi))
        viewModel.onAction(TripScheduleAction.UpdatePlaces(TripPlace.AbroadPlace.USA))

        delay(100)
        assertEquals(viewModel.uiState.value.tripPlaces.size, 3)
    }
}