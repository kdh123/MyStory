package com.dhkim.timecapsule.timecapsule.presentation

import androidx.compose.runtime.Stable
import androidx.paging.PagingData
import com.dhkim.timecapsule.search.domain.Place
import com.dhkim.timecapsule.timecapsule.domain.SharedFriend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
data class AddTimeCapsuleUiState(
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val date: String = "",
    val content: String = "",
    val imageUrls: List<String> = listOf(),
    val openDate: String = "",
    val lat: String = "",
    val lng: String ="",
    val address: String = "",
    val checkLocation: Boolean = false,
    val isShare: Boolean = false,
    val sharedFriends: List<SharedFriend> = listOf(),
    val placeQuery: String = "",
    val placeResult: StateFlow<PagingData<Place>> = MutableStateFlow(PagingData.empty()),
)