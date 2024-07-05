package com.dhkim.home.presentation

sealed interface TimeCapsuleSideEffect {

    data object None: TimeCapsuleSideEffect
    data class NavigateToOpen(val id: String, val isReceived: Boolean): TimeCapsuleSideEffect
    data class NavigateToDetail(val id: String, val isReceived: Boolean): TimeCapsuleSideEffect
    data class Message(val message: String): TimeCapsuleSideEffect
}