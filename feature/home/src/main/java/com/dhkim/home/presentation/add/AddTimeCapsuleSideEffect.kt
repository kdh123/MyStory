package com.dhkim.home.presentation.add

sealed interface AddTimeCapsuleSideEffect {

    data object None: AddTimeCapsuleSideEffect
    data class Message(val message: String): AddTimeCapsuleSideEffect
    data class ShowPlaceBottomSheet(val show: Boolean): AddTimeCapsuleSideEffect
    data class Completed(val isCompleted: Boolean): AddTimeCapsuleSideEffect
}