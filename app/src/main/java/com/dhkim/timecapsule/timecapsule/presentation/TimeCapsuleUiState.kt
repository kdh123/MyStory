package com.dhkim.timecapsule.timecapsule.presentation

import androidx.compose.runtime.Stable
import com.dhkim.timecapsule.timecapsule.domain.MyTimeCapsule
import com.dhkim.timecapsule.timecapsule.domain.ReceivedTimeCapsule
import com.dhkim.timecapsule.timecapsule.domain.SendTimeCapsule

@Stable
data class TimeCapsuleUiState(
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val myTimeCapsules: List<MyTimeCapsule> = listOf(),
    val sendTimeCapsules: List<SendTimeCapsule> = listOf(),
    val receivedTimeCapsules: List<ReceivedTimeCapsule> = listOf(),
)
