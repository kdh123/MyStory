package com.dhkim.timecapsule.profile.presentation

import androidx.compose.runtime.Stable
import com.dhkim.timecapsule.profile.domain.User

@Stable
data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: User = User(),
    val searchResult: SearchResult = SearchResult()
)

data class SearchResult(
    val query: String = "",
    val userId: String?= "",
    val isMe: Boolean = false
)