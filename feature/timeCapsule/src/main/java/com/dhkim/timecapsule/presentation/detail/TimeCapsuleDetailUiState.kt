package com.dhkim.timecapsule.presentation.detail

import androidx.compose.runtime.Stable
import com.dhkim.timecapsule.domain.TimeCapsule

@Stable
data class TimeCapsuleDetailUiState(
    val isLoading: Boolean = false,
    val isReceived: Boolean = false,
    val timeCapsule: TimeCapsule = TimeCapsule()
)