package com.dhkim.trip.detail

import com.dhkim.trip.FakeTripLocalDataSource
import com.dhkim.trip.data.dataSource.local.TripLocalDataSource
import com.dhkim.trip.data.dataSource.local.TripRepositoryImpl
import com.dhkim.trip.data.di.TripModule
import com.dhkim.trip.domain.repository.TripRepository
import com.dhkim.trip.presentation.detail.TripDetailAction
import com.dhkim.trip.presentation.detail.TripDetailViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import junit.framework.TestCase.assertEquals
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

@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
@HiltAndroidTest
@UninstallModules(TripModule::class)
class TripDetailViewModelTest {

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

    private lateinit var viewModel: TripDetailViewModel

    @Inject
    lateinit var tripRepository: TripRepository

    @Before
    fun setup() {
        hiltRule.inject()
        viewModel = TripDetailViewModel(tripRepository = tripRepository, ioDispatcher = UnconfinedTestDispatcher())
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

        assertEquals(viewModel.uiState.value.images?.size, 6)
        assertEquals(viewModel.tripAllImages.value.size, 19)
    }
}