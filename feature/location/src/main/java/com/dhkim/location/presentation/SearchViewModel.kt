@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package com.dhkim.location.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.dhkim.location.domain.usecase.GetNearPlacesByKeywordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class SearchViewModel @Inject constructor(
    private val getNearPlacesByKeywordUseCase: GetNearPlacesByKeywordUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val lat = savedStateHandle.get<String>("lat") ?: "37.572389"
    val lng = savedStateHandle.get<String>("lng") ?: "126.9769117"

    private val loadingFlow = MutableStateFlow(false)
    private val query = MutableStateFlow("")
    val uiState: StateFlow<SearchUiState> = query.debounce(1_000)
        .flatMapLatest {
            loadingFlow.value = false
            getNearPlacesByKeywordUseCase(query = it, lat = lat, lng = lng)
        }
        .cachedIn(viewModelScope)
        .combine(loadingFlow) { pagingData, isLoading ->
            SearchUiState(isLoading = isLoading, places = flowOf(pagingData).stateIn(viewModelScope))
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchUiState()
        )

    fun onQuery(s: String) {
        query.value = s
        loadingFlow.value = true
    }
}