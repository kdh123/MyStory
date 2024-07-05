package com.dhkim.notification

import com.dhkim.home.domain.ReceivedTimeCapsule

data class NotificationUiState(
    val isLoading: Boolean = false,
    val timeCapsules: List<ReceivedTimeCapsule> = listOf()
)