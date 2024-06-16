package com.dhkim.timecapsule.onboarding.signup

sealed interface SignUpSideEffect {

    data object None: SignUpSideEffect
    data class Message(val message: String): SignUpSideEffect
    data class Completed(val isCompleted: Boolean): SignUpSideEffect
}