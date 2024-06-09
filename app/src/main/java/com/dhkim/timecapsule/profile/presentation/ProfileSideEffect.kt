package com.dhkim.timecapsule.profile.presentation

sealed interface ProfileSideEffect {

    data class ShowBottomSheet(val show: Boolean): ProfileSideEffect
    data class ShowDialog(val show: Boolean): ProfileSideEffect
    data class Message(val message: String): ProfileSideEffect
}