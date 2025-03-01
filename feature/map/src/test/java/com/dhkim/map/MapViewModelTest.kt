package com.dhkim.map

import androidx.paging.testing.asSnapshot
import com.dhkim.MainDispatcherRule
import com.dhkim.location.domain.model.Category
import com.dhkim.location.domain.model.Place
import com.dhkim.location.domain.usecase.GetNearPlacesByKeywordUseCase
import com.dhkim.location.domain.usecase.GetPlacesByCategoryUseCase
import com.dhkim.map.presentation.MapAction
import com.dhkim.map.presentation.MapViewModel
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
class MapViewModelTest {

    private val getPlacesByCategoryUseCase = GetPlacesByCategoryUseCase(locationRepository = FakeLocationRepository())

    private val getNearPlaceByKeywordUseCase = GetNearPlacesByKeywordUseCase(locationRepository = FakeLocationRepository())

    private lateinit var viewModel: MapViewModel

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        viewModel = MapViewModel(getNearPlaceByKeywordUseCase, getPlacesByCategoryUseCase)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `키워드 검색 결과 테스트`() = runTest {
        viewModel.onAction(MapAction.SearchPlacesByKeyword("맛집", "0.0", "0.0"))

        advanceTimeBy(1500L)

        val uiState = viewModel.uiState.value
        val places = uiState.places.asSnapshot()
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `카테고리 검색 결과 테스트`() = runTest {
        viewModel.onAction(MapAction.SearchPlacesByCategory(Category.Cafe, "0.0", "0.0"))

        advanceTimeBy(1500L)

        val uiState = viewModel.uiState.value
        val places = uiState.places.asSnapshot()
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