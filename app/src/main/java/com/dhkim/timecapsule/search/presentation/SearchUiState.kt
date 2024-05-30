package com.dhkim.timecapsule.search.presentation

import androidx.paging.PagingData
import com.dhkim.timecapsule.search.domain.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SearchUiState(
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val places: StateFlow<PagingData<Place>> = MutableStateFlow(PagingData.empty()),
    val query: String = ""
)
