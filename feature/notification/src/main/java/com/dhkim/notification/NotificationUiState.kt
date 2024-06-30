package com.dhkim.notification

import com.dhkim.timecapsule.domain.ReceivedTimeCapsule

data class NotificationUiState(
    val isLoading: Boolean = false,
    val timeCapsules: List<ReceivedTimeCapsule> = listOf()
)