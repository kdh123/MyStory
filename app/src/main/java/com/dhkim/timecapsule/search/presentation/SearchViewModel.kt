@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package com.dhkim.timecapsule.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.dhkim.timecapsule.search.domain.SearchRepository
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    private val query = MutableStateFlow("")
    private var currentLocation = LatLng(37.572389, 126.9769117)

    init {
        viewModelScope.launch {
            query.debounce(1000L).flatMapLatest {
                searchRepository.getPlaceByKeyword(
                    query = it,
                    lat = "${currentLocation.latitude}",
                    lng = "${currentLocation.longitude}"
                )
            }.cachedIn(viewModelScope)
                .catch { }
                .collectLatest { result ->
                    _uiState.value = _uiState.value.copy(isLoading = false, places = flowOf(result).stateIn(viewModelScope))
                }
        }
    }


    fun setCurrentLocation(location: LatLng) {
        currentLocation = location
    }

    fun query(s: String) {
        query.value = s
        _uiState.value = _uiState.value.copy(isLoading = true, query = s)
    }
}