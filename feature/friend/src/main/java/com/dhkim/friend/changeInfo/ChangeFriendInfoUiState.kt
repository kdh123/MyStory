package com.dhkim.friend.changeInfo

import com.dhkim.domain.model.Friend

data class ChangeFriendInfoUiState(
    val isLoading: Boolean = false,
    val friend: Friend = Friend()
)
