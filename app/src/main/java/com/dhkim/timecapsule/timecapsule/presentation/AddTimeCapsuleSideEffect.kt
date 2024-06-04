package com.dhkim.timecapsule.timecapsule.presentation

sealed interface AddTimeCapsuleSideEffect {

    data class Message(val message: String): AddTimeCapsuleSideEffect
    data class Completed(val isCompleted: Boolean): AddTimeCapsuleSideEffect
}