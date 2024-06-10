package com.dhkim.timecapsule.timecapsule.presentation

sealed interface AddTimeCapsuleSideEffect {

    data class Message(val message: String): AddTimeCapsuleSideEffect
    data class ShowPlaceBottomSheet(val show: Boolean): AddTimeCapsuleSideEffect
    data class Completed(val isCompleted: Boolean): AddTimeCapsuleSideEffect
}