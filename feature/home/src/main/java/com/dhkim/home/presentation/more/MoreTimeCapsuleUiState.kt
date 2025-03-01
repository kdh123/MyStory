package com.dhkim.home.presentation.more

import androidx.compose.runtime.Stable
import com.dhkim.story.domain.model.TimeCapsule

@Stable
data class MoreTimeCapsuleUiState(
    val isLoading: Boolean = false,
    val timeCapsules: List<TimeCapsule> = listOf()
)
