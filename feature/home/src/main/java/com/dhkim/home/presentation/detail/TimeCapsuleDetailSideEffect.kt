package com.dhkim.home.presentation.detail


sealed interface TimeCapsuleDetailSideEffect {

    data object None: TimeCapsuleDetailSideEffect
    data class Completed(val isCompleted: Boolean): TimeCapsuleDetailSideEffect
}