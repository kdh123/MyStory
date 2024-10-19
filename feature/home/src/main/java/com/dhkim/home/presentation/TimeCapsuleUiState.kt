package com.dhkim.home.presentation

import androidx.compose.runtime.Stable
import com.dhkim.home.domain.model.TimeCapsuleItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class TimeCapsuleUiState(
    val isLoading: Boolean = true,
    val timeCapsules: ImmutableList<TimeCapsuleItem> = persistentListOf()
)