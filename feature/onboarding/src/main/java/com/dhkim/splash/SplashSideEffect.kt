package com.dhkim.splash

sealed interface SplashSideEffect {

    data object None: SplashSideEffect
    data class ShowPopup(val message: String): SplashSideEffect
    data class Completed(val isCompleted: Boolean): SplashSideEffect
}