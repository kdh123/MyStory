package com.dhkim.timecapsule.timecapsule.presentation

sealed interface TimeCapsuleSideEffect {

    object None: TimeCapsuleSideEffect
    data class Message(val message: String): TimeCapsuleSideEffect
}