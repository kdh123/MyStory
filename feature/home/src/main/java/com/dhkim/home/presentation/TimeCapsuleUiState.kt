package com.dhkim.home.presentation

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class TimeCapsuleUiState(
    val isLoading: Boolean = true,
    val isNothing: Boolean = true,
    val timeCapsules: ImmutableList<TimeCapsuleItem> = persistentListOf()
)

@Stable
data class TimeCapsuleItem(
    val id: Int = 0,
    val type: TimeCapsuleType = TimeCapsuleType.NoneTimeCapsule,
    val data: Any? = null
)

enum class TimeCapsuleType {
    Title,
    SubTitle,
    NoneTimeCapsule,
    OpenableTimeCapsule,
    UnopenedTimeCapsule,
    OpenedTimeCapsule,
    InviteFriend,
    Line,
}
