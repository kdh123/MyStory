@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package com.dhkim.location.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.dhkim.location.domain.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.onStart {
        init()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchUiState()
    )

    private val query = MutableStateFlow("")

    private fun init() {
        val lat = savedStateHandle.get<String>("lat") ?: "37.572389"
        val lng = savedStateHandle.get<String>("lng") ?: "126.9769117"

        viewModelScope.launch {
            query.debounce(1_000).flatMapLatest {
                locationRepository.getNearPlaceByKeyword(
                    query = it,
                    lat = lat,
                    lng = lng
                )
            }.cachedIn(viewModelScope)
                .catch { }
                .collectLatest { result ->
                    _uiState.value = _uiState.value.copy(isLoading = false, places = flowOf(result).stateIn(viewModelScope))
                }
        }
    }

    fun onQuery(s: String) {
        query.value = s
        _uiState.value = _uiState.value.copy(isLoading = true, query = s)
    }
}