package com.dhkim.location

import androidx.lifecycle.SavedStateHandle
import androidx.paging.testing.asSnapshot
import com.dhkim.location.domain.model.Place
import com.dhkim.location.domain.usecase.GetNearPlacesByKeywordUseCase
import com.dhkim.location.presentation.SearchViewModel
import com.dhkim.testing.FakeLocationRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SearchViewModelTest {

    private lateinit var viewModel: SearchViewModel

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private val getNearPlacesByKeywordUseCase = GetNearPlacesByKeywordUseCase(locationRepository = FakeLocationRepository())

    @Before
    fun setup() {
        val savedStateHandle = SavedStateHandle().apply {
            set("lat", "37.572389")
            set("lng", "126.9769117")
        }

        viewModel = SearchViewModel(getNearPlacesByKeywordUseCase, savedStateHandle)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `UI 상태 테스트`() = runTest {
        viewModel.uiState.first()
        advanceTimeBy(100)
        viewModel.onQuery("롯데타워")
        advanceTimeBy(1_500)

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