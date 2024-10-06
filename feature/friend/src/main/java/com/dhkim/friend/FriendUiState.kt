package com.dhkim.friend

import androidx.compose.runtime.Stable
import com.dhkim.user.model.User

@Stable
data class FriendUiState(
    val isLoading: Boolean = true,
    val isCreatingCode: Boolean = false,
    val myInfo: User = User(),
    val searchResult: SearchResult = SearchResult()
)

data class SearchResult(
    val query: String = "",
    val userId: String?= "",
    val userProfileImage: String = "0",
    val isMe: Boolean = false
)