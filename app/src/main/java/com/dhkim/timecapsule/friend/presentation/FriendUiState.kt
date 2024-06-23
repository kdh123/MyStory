package com.dhkim.timecapsule.friend.presentation

import androidx.compose.runtime.Stable
import com.dhkim.timecapsule.user.domain.User

@Stable
data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: User = User(),
    val searchResult: SearchResult = SearchResult()
)

data class SearchResult(
    val query: String = "",
    val userId: String?= "",
    val userProfileImage: String = "0",
    val isMe: Boolean = false
)