package com.dhkim.timecapsule.profile.presentation

import androidx.compose.runtime.Stable
import com.dhkim.timecapsule.profile.domain.Friend

@Stable
data class ProfileUiState(
    val isLoading: Boolean = true,
    val userId: String = "",
    val friends: List<Friend> = listOf(),
    val requests: List<UserId> = listOf()
)