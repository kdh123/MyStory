package com.dhkim.location

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.SavedStateHandle
import androidx.paging.compose.collectAsLazyPagingItems
import com.dhkim.location.data.dataSource.remote.LocationApi
import com.dhkim.location.data.dataSource.remote.LocationRemoteDataSource
import com.dhkim.location.data.di.LocationApiModule
import com.dhkim.location.data.di.LocationModule
import com.dhkim.location.data.model.PlaceDocument
import com.dhkim.location.data.repository.LocationRepositoryImpl
import com.dhkim.location.domain.repository.LocationRepository
import com.dhkim.location.domain.model.Place
import com.dhkim.location.domain.usecase.GetNearPlacesByKeywordUseCase
import com.dhkim.location.presentation.SearchScreen
import com.dhkim.location.presentation.SearchViewModel
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
import kotlinx.coroutines.test.advanceTimeBy
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
@UninstallModules(LocationModule::class, LocationApiModule::class)
class SearchScreenTest {

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class FakeTimeCapsuleModule {

        @Binds
        @Singleton
        abstract fun bindLocationRepository(locationRepositoryImpl: LocationRepositoryImpl): LocationRepository

        @Binds
        @Singleton
        abstract fun bindLocationRemoteDataSource(locationRemoteDataSource: FakeLocationRemoteDataSource): com.dhkim.location.data.dataSource.remote.LocationRemoteDataSource

        @Binds
        @Singleton
        abstract fun bindFakeLocationApi(fakeLocationApi: FakeLocationApi): com.dhkim.location.data.dataSource.remote.LocationApi
    }

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val composeRule = createComposeRule()

    @Inject
    lateinit var getNearPlacesByKeywordUseCase: GetNearPlacesByKeywordUseCase

    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        hiltRule.inject()
        val savedStateHandle = SavedStateHandle().apply {
            set("lat", "37.572389")
            set("lng", "126.9769117")
        }
        viewModel = SearchViewModel(getNearPlacesByKeywordUseCase = getNearPlacesByKeywordUseCase, savedStateHandle = savedStateHandle)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `검색 결과 테스트`() = runTest {
        var places = listOf<Place>()

        composeRule.setContent {
            val uiState by viewModel.uiState.collectAsState()
            val searchResult = uiState.places.collectAsLazyPagingItems()

            places = searchResult.itemSnapshotList.items

            SearchScreen(
                uiState = uiState,
                searchResult = searchResult,
                onQuery = viewModel::onQuery,
                onBack = {}
            )
        }

        composeRule.onNodeWithTag("searchBar").performTextInput("롯데타워")
        advanceTimeBy(1500L)


        composeRule.waitUntilExists(hasTestTag("searchResult"), timeoutMillis = 3_000L)

        val documents = mutableListOf<PlaceDocument>().apply {
            repeat(15) {
                add(
                    PlaceDocument(
                        address_name = "서울시 강남구$it",
                        category_group_code = "code$it",
                        category_group_name = "group$it",
                        category_name = "categoryName$it",
                        distance = "$it",
                        id = "placeId$it",
                        phone = "010-1234-1234",
                        place_name = "장소$it",
                        place_url = "url$it",
                        road_address_name = "강남로$it",
                        x = "34.3455",
                        y = "123.4233"
                    )
                )
            }
        }.map {
            it.toPlace()
        }

        val isContain = documents.map {
            places.contains(it)
        }.firstOrNull { !it }

        assertEquals(isContain, null)
    }
}

@OptIn(ExperimentalTestApi::class)
fun ComposeContentTestRule.waitUntilExists(
    matcher: SemanticsMatcher,
    timeoutMillis: Long = 1000L
) = waitUntilNodeCount(matcher, 1, timeoutMillis)