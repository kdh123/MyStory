package com.dhkim.timecapsule.timecapsule.presentation

import androidx.compose.runtime.Stable
import com.dhkim.timecapsule.timecapsule.domain.TimeCapsule

@Stable
data class TimeCapsuleUiState(
    val isLoading: Boolean = true,
    val isCompleted: Boolean = false,
    val openableTimeCapsules: List<TimeCapsule> = listOf(),
    val openedTimeCapsules: List<TimeCapsule> = listOf(),
    val unOpenedTimeCapsules: List<TimeCapsule> = listOf(),
    val unOpenedMyTimeCapsules: List<TimeCapsule> = listOf(),
    val unOpenedReceivedTimeCapsules: List<TimeCapsule> = listOf(),
)
