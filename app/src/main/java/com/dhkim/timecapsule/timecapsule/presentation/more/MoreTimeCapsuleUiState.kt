package com.dhkim.timecapsule.timecapsule.presentation.more

import androidx.compose.runtime.Stable
import com.dhkim.timecapsule.timecapsule.domain.TimeCapsule

@Stable
data class MoreTimeCapsuleUiState(
    val isLoading: Boolean = false,
    val timeCapsules: List<TimeCapsule> = listOf()
)
