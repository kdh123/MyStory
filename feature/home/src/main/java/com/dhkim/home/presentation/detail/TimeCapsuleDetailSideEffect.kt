package com.dhkim.home.presentation.detail


sealed interface TimeCapsuleDetailSideEffect {

    data class Message(val message: String) : TimeCapsuleDetailSideEffect
    data class Completed(val isCompleted: Boolean): TimeCapsuleDetailSideEffect
}