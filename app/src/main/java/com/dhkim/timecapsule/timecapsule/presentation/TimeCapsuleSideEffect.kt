package com.dhkim.timecapsule.timecapsule.presentation

sealed interface TimeCapsuleSideEffect {

    object None: TimeCapsuleSideEffect
    data class NavigateToDetail(val id: String, val isReceived: Boolean): TimeCapsuleSideEffect
    data class Message(val message: String): TimeCapsuleSideEffect
}