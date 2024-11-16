package com.dhkim.home.presentation.detail

import androidx.compose.runtime.Stable
import com.dhkim.home.domain.model.TimeCapsule

@Stable
data class TimeCapsuleDetailUiState(
    val isLoading: Boolean = true,
    val isReceived: Boolean = false,
    val timeCapsule: TimeCapsule = TimeCapsule()
) {
    val writer = if (!isReceived) {
        "${timeCapsule.sender} (나)"
    } else {
        "${timeCapsule.host.nickname} (친구)"
    }
}