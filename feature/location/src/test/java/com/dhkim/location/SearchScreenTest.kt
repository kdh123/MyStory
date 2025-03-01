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
import com.dhkim.location.domain.model.Place
import com.dhkim.location.domain.usecase.GetNearPlacesByKeywordUseCase
import com.dhkim.location.presentation.SearchScreen
import com.dhkim.location.presentation.SearchViewModel
import com.dhkim.testing.FakeLocationRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SearchScreenTest {

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val composeRule = createComposeRule()

    private val getNearPlacesByKeywordUseCase = GetNearPlacesByKeywordUseCase(locationRepository = FakeLocationRepository())

    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
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

        val documents = mutableListOf<Place>().apply {
            repeat(15) {
                add(
                    Place(
                        address = "서울시 강남구$it",
                        category = "categoryName$it",
                        distance = "$it",
                        id = "placeId$it",
                        phone = "010-1234-1234",
                        name = if (it == 2) "롯데타워" else "장소$it",
                        url = "url$it",
                        lat = "34.3455",
                        lng = "123.4233"
                    )
                )
            }
        }

        val isContain = places.map { it.name }.contains("롯데타워")

        assertEquals(places, documents)
        assertEquals(isContain, true)
    }
}

@OptIn(ExperimentalTestApi::class)
fun ComposeContentTestRule.waitUntilExists(
    matcher: SemanticsMatcher,
    timeoutMillis: Long = 1000L
) = waitUntilNodeCount(matcher, 1, timeoutMillis)