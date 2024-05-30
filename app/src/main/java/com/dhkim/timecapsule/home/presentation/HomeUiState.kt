package com.dhkim.timecapsule.home.presentation

import androidx.compose.runtime.Stable
import androidx.paging.PagingData
import com.dhkim.timecapsule.home.domain.Category
import com.dhkim.timecapsule.search.domain.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
data class HomeUiState(
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val query: String = "",
    val places: StateFlow<PagingData<Place>> = MutableStateFlow(PagingData.empty()),
    val category: Category = Category.None
)