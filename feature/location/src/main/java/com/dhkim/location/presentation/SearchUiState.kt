package com.dhkim.location.presentation

import androidx.compose.runtime.Stable
import androidx.paging.PagingData
import com.dhkim.location.domain.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
data class SearchUiState(
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val places: StateFlow<PagingData<Place>> = MutableStateFlow(PagingData.empty()),
    val query: String = ""
)
