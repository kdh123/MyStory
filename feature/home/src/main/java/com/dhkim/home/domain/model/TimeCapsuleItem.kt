package com.dhkim.home.domain.model

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