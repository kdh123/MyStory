package com.dhkim.friend.presentation

import androidx.compose.runtime.Stable
import com.dhkim.user.domain.User

@Stable
data class FriendUiState(
    val isLoading: Boolean = true,
    val myInfo: User = User(),
    val searchResult: SearchResult = SearchResult()
)

data class SearchResult(
    val query: String = "",
    val userId: String?= "",
    val userProfileImage: String = "0",
    val isMe: Boolean = false
)