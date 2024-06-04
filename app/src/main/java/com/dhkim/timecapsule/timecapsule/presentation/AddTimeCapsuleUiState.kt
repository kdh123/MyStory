package com.dhkim.timecapsule.timecapsule.presentation

import androidx.compose.runtime.Stable

@Stable
data class AddTimeCapsuleUiState(
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val date: String = "",
    val content: String = "",
    val imageUrls: List<String> = listOf(),
    val openDate: String = "",
    val address: String = "",
    val checkLocation: Boolean = false,
    val isSend: Boolean = false,
    val receiver: String = ""
)