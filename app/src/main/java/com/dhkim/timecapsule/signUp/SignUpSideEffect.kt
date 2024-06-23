package com.dhkim.timecapsule.signUp

sealed interface SignUpSideEffect {

    data object None: SignUpSideEffect
    data class Message(val message: String): SignUpSideEffect
    data class Completed(val isCompleted: Boolean): SignUpSideEffect
}