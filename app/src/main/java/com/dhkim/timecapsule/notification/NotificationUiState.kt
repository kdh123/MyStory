package com.dhkim.timecapsule.notification

import com.dhkim.timecapsule.timecapsule.domain.ReceivedTimeCapsule

data class NotificationUiState(
    val isLoading: Boolean = false,
    val timeCapsules: List<ReceivedTimeCapsule> = listOf()
)