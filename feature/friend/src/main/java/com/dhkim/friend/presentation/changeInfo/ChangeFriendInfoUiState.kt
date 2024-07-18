package com.dhkim.friend.presentation.changeInfo

import com.dhkim.user.domain.Friend

data class ChangeFriendInfoUiState(
    val isLoading: Boolean = false,
    val friend: Friend = Friend()
)
