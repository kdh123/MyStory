package com.dhkim.timecapsule.timecapsule.presentation.detail

import androidx.compose.runtime.Stable
import com.dhkim.timecapsule.timecapsule.domain.TimeCapsule

@Stable
data class TimeCapsuleOpenUiState(
    val isLoading: Boolean = false,
    val isReceived: Boolean = false,
    val timeCapsule: TimeCapsule = TimeCapsule()
)