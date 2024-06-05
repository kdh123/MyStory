package com.dhkim.timecapsule.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dhkim.timecapsule.home.HomeAction
import com.dhkim.timecapsule.home.domain.Category
import com.dhkim.timecapsule.search.domain.Place
import com.dhkim.timecapsule.search.domain.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<HomeSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnSearchPlacesByCategory -> {
                with(action) {
                    searchPlacesByCategory(
                        category = category,
                        lat = lat,
                        lng = lng
                    )
                }
            }
        }
    }

    fun searchPlacesByCategory(category: Category, lat: String, lng: String) {
        viewModelScope.launch {
            searchRepository.getPlaceByCategory(
                category = category,
                lat = lat,
                lng = lng
            ).cachedIn(viewModelScope)
                .collect {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        query = category.type,
                        category = category,
                        places = flowOf(it).stateIn(viewModelScope),
                        selectedPlace = null
                    )
                    _sideEffect.emit(HomeSideEffect.BottomSheet(isHide = false))
                }
        }
    }

    fun searchPlacesByKeyword(query: String, lat: String, lng: String) {
        viewModelScope.launch {
            searchRepository.getPlaceByKeyword(
                query = query,
                lat = lat,
                lng = lng
            ).cachedIn(viewModelScope)
                .collect {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        query = query,
                        category = Category.Popular,
                        places = flowOf(it).stateIn(viewModelScope),
                        selectedPlace = null
                    )
                    _sideEffect.emit(HomeSideEffect.BottomSheet(isHide = false))
                }
        }
    }

    fun selectPlace(place: Place) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                query = place.name,
                category = Category.None,
                selectedPlace = place,
                places = MutableStateFlow(PagingData.empty())
            )

            _sideEffect.emit(HomeSideEffect.BottomSheet(isHide = true))
        }
    }

    fun closeSearch(selectPlace: Boolean) {
        _uiState.value = if (selectPlace) {
            _uiState.value.copy(
                places = MutableStateFlow(PagingData.empty()),
                category = Category.None
            )
        } else {
            _uiState.value.copy(
                query = "",
                places = MutableStateFlow(PagingData.empty()),
                category = Category.None,
                selectedPlace = null
            )
        }

        viewModelScope.launch {
            _sideEffect.emit(HomeSideEffect.BottomSheet(isHide = true))
        }
    }
}