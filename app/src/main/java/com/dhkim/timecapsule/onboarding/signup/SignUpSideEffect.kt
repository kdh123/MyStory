package com.dhkim.timecapsule.onboarding.signup

sealed interface SignUpSideEffect {

    data class Completed(val isCompleted: Boolean): SignUpSideEffect
}